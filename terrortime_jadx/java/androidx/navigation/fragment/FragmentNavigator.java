package androidx.navigation.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.Navigator.Name;
import androidx.navigation.NavigatorProvider;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

@Name("fragment")
public class FragmentNavigator extends Navigator<Destination> {
    private static final String KEY_BACK_STACK_IDS = "androidx-nav-fragment:navigator:backStackIds";
    private static final String TAG = "FragmentNavigator";
    ArrayDeque<Integer> mBackStack = new ArrayDeque<>();
    private final int mContainerId;
    private final Context mContext;
    final FragmentManager mFragmentManager;
    boolean mIsPendingBackStackOperation = false;
    private final OnBackStackChangedListener mOnBackStackChangedListener = new OnBackStackChangedListener() {
        public void onBackStackChanged() {
            if (FragmentNavigator.this.mIsPendingBackStackOperation) {
                FragmentNavigator fragmentNavigator = FragmentNavigator.this;
                fragmentNavigator.mIsPendingBackStackOperation = !fragmentNavigator.isBackStackEqual();
                return;
            }
            int newCount = FragmentNavigator.this.mFragmentManager.getBackStackEntryCount() + 1;
            if (newCount < FragmentNavigator.this.mBackStack.size()) {
                while (FragmentNavigator.this.mBackStack.size() > newCount) {
                    FragmentNavigator.this.mBackStack.removeLast();
                }
                FragmentNavigator.this.dispatchOnNavigatorBackPress();
            }
        }
    };

    public static class Destination extends NavDestination {
        private String mClassName;

        public Destination(NavigatorProvider navigatorProvider) {
            this(navigatorProvider.getNavigator(FragmentNavigator.class));
        }

        public Destination(Navigator<? extends Destination> fragmentNavigator) {
            super(fragmentNavigator);
        }

        public void onInflate(Context context, AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.FragmentNavigator);
            String className = a.getString(R.styleable.FragmentNavigator_android_name);
            if (className != null) {
                setClassName(className);
            }
            a.recycle();
        }

        public final Destination setClassName(String className) {
            this.mClassName = className;
            return this;
        }

        public final String getClassName() {
            String str = this.mClassName;
            if (str != null) {
                return str;
            }
            throw new IllegalStateException("Fragment class was not set");
        }
    }

    public static final class Extras implements androidx.navigation.Navigator.Extras {
        private final LinkedHashMap<View, String> mSharedElements = new LinkedHashMap<>();

        public static final class Builder {
            private final LinkedHashMap<View, String> mSharedElements = new LinkedHashMap<>();

            public Builder addSharedElements(Map<View, String> sharedElements) {
                for (Entry<View, String> sharedElement : sharedElements.entrySet()) {
                    View view = (View) sharedElement.getKey();
                    String name = (String) sharedElement.getValue();
                    if (!(view == null || name == null)) {
                        addSharedElement(view, name);
                    }
                }
                return this;
            }

            public Builder addSharedElement(View sharedElement, String name) {
                this.mSharedElements.put(sharedElement, name);
                return this;
            }

            public Extras build() {
                return new Extras(this.mSharedElements);
            }
        }

        Extras(Map<View, String> sharedElements) {
            this.mSharedElements.putAll(sharedElements);
        }

        public Map<View, String> getSharedElements() {
            return Collections.unmodifiableMap(this.mSharedElements);
        }
    }

    public FragmentNavigator(Context context, FragmentManager manager, int containerId) {
        this.mContext = context;
        this.mFragmentManager = manager;
        this.mContainerId = containerId;
    }

    /* access modifiers changed from: protected */
    public void onBackPressAdded() {
        this.mFragmentManager.addOnBackStackChangedListener(this.mOnBackStackChangedListener);
    }

    /* access modifiers changed from: protected */
    public void onBackPressRemoved() {
        this.mFragmentManager.removeOnBackStackChangedListener(this.mOnBackStackChangedListener);
    }

    public boolean popBackStack() {
        if (this.mBackStack.isEmpty()) {
            return false;
        }
        if (this.mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state");
            return false;
        }
        if (this.mFragmentManager.getBackStackEntryCount() > 0) {
            this.mFragmentManager.popBackStack(generateBackStackName(this.mBackStack.size(), ((Integer) this.mBackStack.peekLast()).intValue()), 1);
            this.mIsPendingBackStackOperation = true;
        }
        this.mBackStack.removeLast();
        return true;
    }

    public Destination createDestination() {
        return new Destination((Navigator<? extends Destination>) this);
    }

    public Fragment instantiateFragment(Context context, FragmentManager fragmentManager, String className, Bundle args) {
        return Fragment.instantiate(context, className, args);
    }

    public NavDestination navigate(Destination destination, Bundle args, NavOptions navOptions, androidx.navigation.Navigator.Extras navigatorExtras) {
        boolean isAdded;
        Bundle bundle = args;
        androidx.navigation.Navigator.Extras extras = navigatorExtras;
        if (this.mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state");
            return null;
        }
        String className = destination.getClassName();
        boolean isSingleTopReplacement = false;
        if (className.charAt(0) == '.') {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mContext.getPackageName());
            sb.append(className);
            className = sb.toString();
        }
        Fragment frag = instantiateFragment(this.mContext, this.mFragmentManager, className, bundle);
        frag.setArguments(bundle);
        FragmentTransaction ft = this.mFragmentManager.beginTransaction();
        int enterAnim = navOptions != null ? navOptions.getEnterAnim() : -1;
        int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
        int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
        int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
        if (!(enterAnim == -1 && exitAnim == -1 && popEnterAnim == -1 && popExitAnim == -1)) {
            ft.setCustomAnimations(enterAnim != -1 ? enterAnim : 0, exitAnim != -1 ? exitAnim : 0, popEnterAnim != -1 ? popEnterAnim : 0, popExitAnim != -1 ? popExitAnim : 0);
        }
        ft.replace(this.mContainerId, frag);
        ft.setPrimaryNavigationFragment(frag);
        int destId = destination.getId();
        boolean initialNavigation = this.mBackStack.isEmpty();
        if (navOptions != null && !initialNavigation && navOptions.shouldLaunchSingleTop() && ((Integer) this.mBackStack.peekLast()).intValue() == destId) {
            isSingleTopReplacement = true;
        }
        if (initialNavigation) {
            isAdded = true;
        } else if (isSingleTopReplacement) {
            if (this.mBackStack.size() > 1) {
                this.mFragmentManager.popBackStack(generateBackStackName(this.mBackStack.size(), ((Integer) this.mBackStack.peekLast()).intValue()), 1);
                ft.addToBackStack(generateBackStackName(this.mBackStack.size(), destId));
                this.mIsPendingBackStackOperation = true;
            }
            isAdded = false;
        } else {
            ft.addToBackStack(generateBackStackName(this.mBackStack.size() + 1, destId));
            this.mIsPendingBackStackOperation = true;
            isAdded = true;
        }
        if (extras instanceof Extras) {
            for (Entry<View, String> sharedElement : ((Extras) extras).getSharedElements().entrySet()) {
                ft.addSharedElement((View) sharedElement.getKey(), (String) sharedElement.getValue());
                Bundle bundle2 = args;
                androidx.navigation.Navigator.Extras extras2 = navigatorExtras;
            }
        }
        ft.setReorderingAllowed(true);
        ft.commit();
        if (!isAdded) {
            return null;
        }
        this.mBackStack.add(Integer.valueOf(destId));
        return destination;
    }

    public Bundle onSaveState() {
        Bundle b = new Bundle();
        int[] backStack = new int[this.mBackStack.size()];
        int index = 0;
        Iterator it = this.mBackStack.iterator();
        while (it.hasNext()) {
            int index2 = index + 1;
            backStack[index] = ((Integer) it.next()).intValue();
            index = index2;
        }
        b.putIntArray(KEY_BACK_STACK_IDS, backStack);
        return b;
    }

    public void onRestoreState(Bundle savedState) {
        if (savedState != null) {
            int[] backStack = savedState.getIntArray(KEY_BACK_STACK_IDS);
            if (backStack != null) {
                this.mBackStack.clear();
                for (int destId : backStack) {
                    this.mBackStack.add(Integer.valueOf(destId));
                }
            }
        }
    }

    private String generateBackStackName(int backStackIndex, int destId) {
        StringBuilder sb = new StringBuilder();
        sb.append(backStackIndex);
        sb.append("-");
        sb.append(destId);
        return sb.toString();
    }

    private int getDestId(String backStackName) {
        String[] split = backStackName != null ? backStackName.split("-") : new String[0];
        String str = "Invalid back stack entry on the NavHostFragment's back stack - use getChildFragmentManager() if you need to do custom FragmentTransactions from within Fragments created via your navigation graph.";
        if (split.length == 2) {
            try {
                Integer.parseInt(split[0]);
                return Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                throw new IllegalStateException(str);
            }
        } else {
            throw new IllegalStateException(str);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isBackStackEqual() {
        int fragmentBackStackCount = this.mFragmentManager.getBackStackEntryCount();
        if (this.mBackStack.size() != fragmentBackStackCount + 1) {
            return false;
        }
        Iterator<Integer> backStackIterator = this.mBackStack.descendingIterator();
        int fragmentBackStackIndex = fragmentBackStackCount - 1;
        while (backStackIterator.hasNext() && fragmentBackStackIndex >= 0) {
            try {
                int fragmentBackStackIndex2 = fragmentBackStackIndex - 1;
                try {
                    if (((Integer) backStackIterator.next()).intValue() != getDestId(this.mFragmentManager.getBackStackEntryAt(fragmentBackStackIndex).getName())) {
                        return false;
                    }
                    fragmentBackStackIndex = fragmentBackStackIndex2;
                } catch (NumberFormatException e) {
                    throw new IllegalStateException("Invalid back stack entry on the NavHostFragment's back stack - use getChildFragmentManager() if you need to do custom FragmentTransactions from within Fragments created via your navigation graph.");
                }
            } catch (NumberFormatException e2) {
                int i = fragmentBackStackIndex;
                NumberFormatException numberFormatException = e2;
                throw new IllegalStateException("Invalid back stack entry on the NavHostFragment's back stack - use getChildFragmentManager() if you need to do custom FragmentTransactions from within Fragments created via your navigation graph.");
            }
        }
        return true;
    }
}
