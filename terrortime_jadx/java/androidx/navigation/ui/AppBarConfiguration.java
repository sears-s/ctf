package androidx.navigation.ui;

import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import androidx.navigation.NavGraph;
import java.util.HashSet;
import java.util.Set;

public final class AppBarConfiguration {
    private final DrawerLayout mDrawerLayout;
    private final OnNavigateUpListener mFallbackOnNavigateUpListener;
    private final Set<Integer> mTopLevelDestinations;

    public static final class Builder {
        private DrawerLayout mDrawerLayout;
        private OnNavigateUpListener mFallbackOnNavigateUpListener;
        private final Set<Integer> mTopLevelDestinations = new HashSet();

        public Builder(NavGraph navGraph) {
            this.mTopLevelDestinations.add(Integer.valueOf(NavigationUI.findStartDestination(navGraph).getId()));
        }

        public Builder(Menu topLevelMenu) {
            int size = topLevelMenu.size();
            for (int index = 0; index < size; index++) {
                this.mTopLevelDestinations.add(Integer.valueOf(topLevelMenu.getItem(index).getItemId()));
            }
        }

        public Builder(int... topLevelDestinationIds) {
            for (int destinationId : topLevelDestinationIds) {
                this.mTopLevelDestinations.add(Integer.valueOf(destinationId));
            }
        }

        public Builder(Set<Integer> topLevelDestinationIds) {
            this.mTopLevelDestinations.addAll(topLevelDestinationIds);
        }

        public Builder setDrawerLayout(DrawerLayout drawerLayout) {
            this.mDrawerLayout = drawerLayout;
            return this;
        }

        public Builder setFallbackOnNavigateUpListener(OnNavigateUpListener fallbackOnNavigateUpListener) {
            this.mFallbackOnNavigateUpListener = fallbackOnNavigateUpListener;
            return this;
        }

        public AppBarConfiguration build() {
            return new AppBarConfiguration(this.mTopLevelDestinations, this.mDrawerLayout, this.mFallbackOnNavigateUpListener);
        }
    }

    public interface OnNavigateUpListener {
        boolean onNavigateUp();
    }

    private AppBarConfiguration(Set<Integer> topLevelDestinations, DrawerLayout drawerLayout, OnNavigateUpListener fallbackOnNavigateUpListener) {
        this.mTopLevelDestinations = topLevelDestinations;
        this.mDrawerLayout = drawerLayout;
        this.mFallbackOnNavigateUpListener = fallbackOnNavigateUpListener;
    }

    public Set<Integer> getTopLevelDestinations() {
        return this.mTopLevelDestinations;
    }

    public DrawerLayout getDrawerLayout() {
        return this.mDrawerLayout;
    }

    public OnNavigateUpListener getFallbackOnNavigateUpListener() {
        return this.mFallbackOnNavigateUpListener;
    }
}
