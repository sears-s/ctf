package android.support.design.internal;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.view.ViewGroup;

public class BottomNavigationPresenter implements MenuPresenter {
    private int id;
    private MenuBuilder menu;
    private BottomNavigationMenuView menuView;
    private boolean updateSuspended = false;

    static class SavedState implements Parcelable {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int selectedItemId;

        SavedState() {
        }

        SavedState(Parcel in) {
            this.selectedItemId = in.readInt();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.selectedItemId);
        }
    }

    public void setBottomNavigationMenuView(BottomNavigationMenuView menuView2) {
        this.menuView = menuView2;
    }

    public void initForMenu(Context context, MenuBuilder menu2) {
        this.menu = menu2;
        this.menuView.initialize(this.menu);
    }

    public MenuView getMenuView(ViewGroup root) {
        return this.menuView;
    }

    public void updateMenuView(boolean cleared) {
        if (!this.updateSuspended) {
            if (cleared) {
                this.menuView.buildMenuView();
            } else {
                this.menuView.updateMenuView();
            }
        }
    }

    public void setCallback(Callback cb) {
    }

    public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        return false;
    }

    public void onCloseMenu(MenuBuilder menu2, boolean allMenusAreClosing) {
    }

    public boolean flagActionItems() {
        return false;
    }

    public boolean expandItemActionView(MenuBuilder menu2, MenuItemImpl item) {
        return false;
    }

    public boolean collapseItemActionView(MenuBuilder menu2, MenuItemImpl item) {
        return false;
    }

    public void setId(int id2) {
        this.id = id2;
    }

    public int getId() {
        return this.id;
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState();
        savedState.selectedItemId = this.menuView.getSelectedItemId();
        return savedState;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            this.menuView.tryRestoreSelectedItemId(((SavedState) state).selectedItemId);
        }
    }

    public void setUpdateSuspended(boolean updateSuspended2) {
        this.updateSuspended = updateSuspended2;
    }
}
