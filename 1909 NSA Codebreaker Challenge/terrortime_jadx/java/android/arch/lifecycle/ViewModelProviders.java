package android.arch.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.ViewModelProvider.AndroidViewModelFactory;
import android.arch.lifecycle.ViewModelProvider.Factory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class ViewModelProviders {

    @Deprecated
    public static class DefaultFactory extends AndroidViewModelFactory {
        @Deprecated
        public DefaultFactory(Application application) {
            super(application);
        }
    }

    private static Application checkApplication(Activity activity) {
        Application application = activity.getApplication();
        if (application != null) {
            return application;
        }
        throw new IllegalStateException("Your activity/fragment is not yet attached to Application. You can't request ViewModel before onCreate call.");
    }

    private static Activity checkActivity(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity != null) {
            return activity;
        }
        throw new IllegalStateException("Can't create ViewModelProvider for detached fragment");
    }

    public static ViewModelProvider of(Fragment fragment) {
        return of(fragment, (Factory) null);
    }

    public static ViewModelProvider of(FragmentActivity activity) {
        return of(activity, (Factory) null);
    }

    public static ViewModelProvider of(Fragment fragment, Factory factory) {
        Application application = checkApplication(checkActivity(fragment));
        if (factory == null) {
            factory = AndroidViewModelFactory.getInstance(application);
        }
        return new ViewModelProvider(ViewModelStores.of(fragment), factory);
    }

    public static ViewModelProvider of(FragmentActivity activity, Factory factory) {
        Application application = checkApplication(activity);
        if (factory == null) {
            factory = AndroidViewModelFactory.getInstance(application);
        }
        return new ViewModelProvider(ViewModelStores.of(activity), factory);
    }
}
