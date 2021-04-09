package androidx.navigation.ui;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import androidx.navigation.NavController;
import androidx.navigation.NavController.OnDestinationChangedListener;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions.Builder;
import java.lang.ref.WeakReference;
import java.util.Set;

public final class NavigationUI {
    private NavigationUI() {
    }

    public static boolean onNavDestinationSelected(MenuItem item, NavController navController) {
        Builder builder = new Builder().setLaunchSingleTop(true).setEnterAnim(R.anim.nav_default_enter_anim).setExitAnim(R.anim.nav_default_exit_anim).setPopEnterAnim(R.anim.nav_default_pop_enter_anim).setPopExitAnim(R.anim.nav_default_pop_exit_anim);
        if ((item.getOrder() & 196608) == 0) {
            builder.setPopUpTo(findStartDestination(navController.getGraph()).getId(), false);
        }
        try {
            navController.navigate(item.getItemId(), null, builder.build());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean navigateUp(NavController navController, DrawerLayout drawerLayout) {
        return navigateUp(navController, new AppBarConfiguration.Builder(navController.getGraph()).setDrawerLayout(drawerLayout).build());
    }

    public static boolean navigateUp(NavController navController, AppBarConfiguration configuration) {
        DrawerLayout drawerLayout = configuration.getDrawerLayout();
        NavDestination currentDestination = navController.getCurrentDestination();
        Set<Integer> topLevelDestinations = configuration.getTopLevelDestinations();
        if (drawerLayout != null && currentDestination != null && matchDestinations(currentDestination, topLevelDestinations)) {
            drawerLayout.openDrawer((int) GravityCompat.START);
            return true;
        } else if (navController.navigateUp()) {
            return true;
        } else {
            if (configuration.getFallbackOnNavigateUpListener() != null) {
                return configuration.getFallbackOnNavigateUpListener().onNavigateUp();
            }
            return false;
        }
    }

    public static void setupActionBarWithNavController(AppCompatActivity activity, NavController navController) {
        setupActionBarWithNavController(activity, navController, new AppBarConfiguration.Builder(navController.getGraph()).build());
    }

    public static void setupActionBarWithNavController(AppCompatActivity activity, NavController navController, DrawerLayout drawerLayout) {
        setupActionBarWithNavController(activity, navController, new AppBarConfiguration.Builder(navController.getGraph()).setDrawerLayout(drawerLayout).build());
    }

    public static void setupActionBarWithNavController(AppCompatActivity activity, NavController navController, AppBarConfiguration configuration) {
        navController.addOnDestinationChangedListener(new ActionBarOnDestinationChangedListener(activity, configuration));
    }

    public static void setupWithNavController(Toolbar toolbar, NavController navController) {
        setupWithNavController(toolbar, navController, new AppBarConfiguration.Builder(navController.getGraph()).build());
    }

    public static void setupWithNavController(Toolbar toolbar, NavController navController, DrawerLayout drawerLayout) {
        setupWithNavController(toolbar, navController, new AppBarConfiguration.Builder(navController.getGraph()).setDrawerLayout(drawerLayout).build());
    }

    public static void setupWithNavController(Toolbar toolbar, final NavController navController, final AppBarConfiguration configuration) {
        navController.addOnDestinationChangedListener(new ToolbarOnDestinationChangedListener(toolbar, configuration));
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NavigationUI.navigateUp(navController, configuration);
            }
        });
    }

    public static void setupWithNavController(CollapsingToolbarLayout collapsingToolbarLayout, Toolbar toolbar, NavController navController) {
        setupWithNavController(collapsingToolbarLayout, toolbar, navController, new AppBarConfiguration.Builder(navController.getGraph()).build());
    }

    public static void setupWithNavController(CollapsingToolbarLayout collapsingToolbarLayout, Toolbar toolbar, NavController navController, DrawerLayout drawerLayout) {
        setupWithNavController(collapsingToolbarLayout, toolbar, navController, new AppBarConfiguration.Builder(navController.getGraph()).setDrawerLayout(drawerLayout).build());
    }

    public static void setupWithNavController(CollapsingToolbarLayout collapsingToolbarLayout, Toolbar toolbar, final NavController navController, final AppBarConfiguration configuration) {
        navController.addOnDestinationChangedListener(new CollapsingToolbarOnDestinationChangedListener(collapsingToolbarLayout, toolbar, configuration));
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NavigationUI.navigateUp(navController, configuration);
            }
        });
    }

    public static void setupWithNavController(final NavigationView navigationView, final NavController navController) {
        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) {
                    ViewParent parent = navigationView.getParent();
                    if (parent instanceof DrawerLayout) {
                        ((DrawerLayout) parent).closeDrawer((View) navigationView);
                    } else {
                        BottomSheetBehavior bottomSheetBehavior = NavigationUI.findBottomSheetBehavior(navigationView);
                        if (bottomSheetBehavior != null) {
                            bottomSheetBehavior.setState(5);
                        }
                    }
                }
                return handled;
            }
        });
        final WeakReference<NavigationView> weakReference = new WeakReference<>(navigationView);
        navController.addOnDestinationChangedListener(new OnDestinationChangedListener() {
            public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
                NavigationView view = (NavigationView) weakReference.get();
                if (view == null) {
                    navController.removeOnDestinationChangedListener(this);
                    return;
                }
                Menu menu = view.getMenu();
                int size = menu.size();
                for (int h = 0; h < size; h++) {
                    MenuItem item = menu.getItem(h);
                    item.setChecked(NavigationUI.matchDestination(destination, item.getItemId()));
                }
            }
        });
    }

    static BottomSheetBehavior findBottomSheetBehavior(View view) {
        LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            ViewParent parent = view.getParent();
            if (parent instanceof View) {
                return findBottomSheetBehavior((View) parent);
            }
            return null;
        }
        Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
        if (!(behavior instanceof BottomSheetBehavior)) {
            return null;
        }
        return (BottomSheetBehavior) behavior;
    }

    public static void setupWithNavController(BottomNavigationView bottomNavigationView, final NavController navController) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                return NavigationUI.onNavDestinationSelected(item, navController);
            }
        });
        final WeakReference<BottomNavigationView> weakReference = new WeakReference<>(bottomNavigationView);
        navController.addOnDestinationChangedListener(new OnDestinationChangedListener() {
            public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
                BottomNavigationView view = (BottomNavigationView) weakReference.get();
                if (view == null) {
                    navController.removeOnDestinationChangedListener(this);
                    return;
                }
                Menu menu = view.getMenu();
                int size = menu.size();
                for (int h = 0; h < size; h++) {
                    MenuItem item = menu.getItem(h);
                    if (NavigationUI.matchDestination(destination, item.getItemId())) {
                        item.setChecked(true);
                    }
                }
            }
        });
    }

    static boolean matchDestination(NavDestination destination, int destId) {
        NavDestination currentDestination = destination;
        while (currentDestination.getId() != destId && currentDestination.getParent() != null) {
            currentDestination = currentDestination.getParent();
        }
        return currentDestination.getId() == destId;
    }

    static boolean matchDestinations(NavDestination destination, Set<Integer> destinationIds) {
        NavDestination currentDestination = destination;
        while (!destinationIds.contains(Integer.valueOf(currentDestination.getId()))) {
            currentDestination = currentDestination.getParent();
            if (currentDestination == null) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=androidx.navigation.NavGraph, code=androidx.navigation.NavDestination, for r3v0, types: [androidx.navigation.NavDestination, androidx.navigation.NavGraph] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static androidx.navigation.NavDestination findStartDestination(androidx.navigation.NavDestination r3) {
        /*
            r0 = r3
        L_0x0001:
            boolean r1 = r0 instanceof androidx.navigation.NavGraph
            if (r1 == 0) goto L_0x0011
            r1 = r0
            androidx.navigation.NavGraph r1 = (androidx.navigation.NavGraph) r1
            int r2 = r1.getStartDestination()
            androidx.navigation.NavDestination r0 = r1.findNode(r2)
            goto L_0x0001
        L_0x0011:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.navigation.ui.NavigationUI.findStartDestination(androidx.navigation.NavGraph):androidx.navigation.NavDestination");
    }
}
