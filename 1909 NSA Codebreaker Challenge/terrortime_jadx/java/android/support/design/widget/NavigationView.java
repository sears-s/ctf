package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.support.design.R;
import android.support.design.internal.NavigationMenu;
import android.support.design.internal.NavigationMenuPresenter;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.support.design.internal.ThemeEnforcement;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;

public class NavigationView extends ScrimInsetsFrameLayout {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private static final int[] DISABLED_STATE_SET = {-16842910};
    private static final int PRESENTER_NAVIGATION_VIEW_ID = 1;
    OnNavigationItemSelectedListener listener;
    private final int maxWidth;
    private final NavigationMenu menu;
    private MenuInflater menuInflater;
    private final NavigationMenuPresenter presenter;

    public interface OnNavigationItemSelectedListener {
        boolean onNavigationItemSelected(MenuItem menuItem);
    }

    public static class SavedState extends AbsSavedState {
        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        public Bundle menuState;

        public SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            this.menuState = in.readBundle(loader);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(this.menuState);
        }
    }

    public NavigationView(Context context) {
        this(context, null);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.navigationViewStyle);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        ColorStateList itemIconTint;
        super(context, attrs, defStyleAttr);
        this.presenter = new NavigationMenuPresenter();
        this.menu = new NavigationMenu(context);
        TintTypedArray a = ThemeEnforcement.obtainTintedStyledAttributes(context, attrs, R.styleable.NavigationView, defStyleAttr, R.style.Widget_Design_NavigationView, new int[0]);
        ViewCompat.setBackground(this, a.getDrawable(R.styleable.NavigationView_android_background));
        if (a.hasValue(R.styleable.NavigationView_elevation)) {
            ViewCompat.setElevation(this, (float) a.getDimensionPixelSize(R.styleable.NavigationView_elevation, 0));
        }
        ViewCompat.setFitsSystemWindows(this, a.getBoolean(R.styleable.NavigationView_android_fitsSystemWindows, false));
        this.maxWidth = a.getDimensionPixelSize(R.styleable.NavigationView_android_maxWidth, 0);
        if (a.hasValue(R.styleable.NavigationView_itemIconTint)) {
            itemIconTint = a.getColorStateList(R.styleable.NavigationView_itemIconTint);
        } else {
            itemIconTint = createDefaultColorStateList(16842808);
        }
        boolean textAppearanceSet = false;
        int textAppearance = 0;
        if (a.hasValue(R.styleable.NavigationView_itemTextAppearance)) {
            textAppearance = a.getResourceId(R.styleable.NavigationView_itemTextAppearance, 0);
            textAppearanceSet = true;
        }
        ColorStateList itemTextColor = null;
        if (a.hasValue(R.styleable.NavigationView_itemTextColor)) {
            itemTextColor = a.getColorStateList(R.styleable.NavigationView_itemTextColor);
        }
        if (!textAppearanceSet && itemTextColor == null) {
            itemTextColor = createDefaultColorStateList(16842806);
        }
        Drawable itemBackground = a.getDrawable(R.styleable.NavigationView_itemBackground);
        if (a.hasValue(R.styleable.NavigationView_itemHorizontalPadding)) {
            this.presenter.setItemHorizontalPadding(a.getDimensionPixelSize(R.styleable.NavigationView_itemHorizontalPadding, 0));
        }
        int itemIconPadding = a.getDimensionPixelSize(R.styleable.NavigationView_itemIconPadding, 0);
        this.menu.setCallback(new Callback() {
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                return NavigationView.this.listener != null && NavigationView.this.listener.onNavigationItemSelected(item);
            }

            public void onMenuModeChange(MenuBuilder menu) {
            }
        });
        this.presenter.setId(1);
        this.presenter.initForMenu(context, this.menu);
        this.presenter.setItemIconTintList(itemIconTint);
        if (textAppearanceSet) {
            this.presenter.setItemTextAppearance(textAppearance);
        }
        this.presenter.setItemTextColor(itemTextColor);
        this.presenter.setItemBackground(itemBackground);
        this.presenter.setItemIconPadding(itemIconPadding);
        this.menu.addMenuPresenter(this.presenter);
        addView((View) this.presenter.getMenuView(this));
        if (a.hasValue(R.styleable.NavigationView_menu)) {
            inflateMenu(a.getResourceId(R.styleable.NavigationView_menu, 0));
        }
        if (a.hasValue(R.styleable.NavigationView_headerLayout)) {
            inflateHeaderView(a.getResourceId(R.styleable.NavigationView_headerLayout, 0));
        }
        a.recycle();
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        state.menuState = new Bundle();
        this.menu.savePresenterStates(state.menuState);
        return state;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable savedState) {
        if (!(savedState instanceof SavedState)) {
            super.onRestoreInstanceState(savedState);
            return;
        }
        SavedState state = (SavedState) savedState;
        super.onRestoreInstanceState(state.getSuperState());
        this.menu.restorePresenterStates(state.menuState);
    }

    public void setNavigationItemSelectedListener(OnNavigationItemSelectedListener listener2) {
        this.listener = listener2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthSpec, int heightSpec) {
        int mode = MeasureSpec.getMode(widthSpec);
        if (mode == Integer.MIN_VALUE) {
            widthSpec = MeasureSpec.makeMeasureSpec(Math.min(MeasureSpec.getSize(widthSpec), this.maxWidth), 1073741824);
        } else if (mode == 0) {
            widthSpec = MeasureSpec.makeMeasureSpec(this.maxWidth, 1073741824);
        } else if (mode != 1073741824) {
        }
        super.onMeasure(widthSpec, heightSpec);
    }

    /* access modifiers changed from: protected */
    public void onInsetsChanged(WindowInsetsCompat insets) {
        this.presenter.dispatchApplyWindowInsets(insets);
    }

    public void inflateMenu(int resId) {
        this.presenter.setUpdateSuspended(true);
        getMenuInflater().inflate(resId, this.menu);
        this.presenter.setUpdateSuspended(false);
        this.presenter.updateMenuView(false);
    }

    public Menu getMenu() {
        return this.menu;
    }

    public View inflateHeaderView(int res) {
        return this.presenter.inflateHeaderView(res);
    }

    public void addHeaderView(View view) {
        this.presenter.addHeaderView(view);
    }

    public void removeHeaderView(View view) {
        this.presenter.removeHeaderView(view);
    }

    public int getHeaderCount() {
        return this.presenter.getHeaderCount();
    }

    public View getHeaderView(int index) {
        return this.presenter.getHeaderView(index);
    }

    public ColorStateList getItemIconTintList() {
        return this.presenter.getItemTintList();
    }

    public void setItemIconTintList(ColorStateList tint) {
        this.presenter.setItemIconTintList(tint);
    }

    public ColorStateList getItemTextColor() {
        return this.presenter.getItemTextColor();
    }

    public void setItemTextColor(ColorStateList textColor) {
        this.presenter.setItemTextColor(textColor);
    }

    public Drawable getItemBackground() {
        return this.presenter.getItemBackground();
    }

    public void setItemBackgroundResource(int resId) {
        setItemBackground(ContextCompat.getDrawable(getContext(), resId));
    }

    public void setItemBackground(Drawable itemBackground) {
        this.presenter.setItemBackground(itemBackground);
    }

    public int getItemHorizontalPadding() {
        return this.presenter.getItemHorizontalPadding();
    }

    public void setItemHorizontalPadding(int padding) {
        this.presenter.setItemHorizontalPadding(padding);
    }

    public void setItemHorizontalPaddingResource(int paddingResource) {
        this.presenter.setItemHorizontalPadding(getResources().getDimensionPixelSize(paddingResource));
    }

    public int getItemIconPadding() {
        return this.presenter.getItemIconPadding();
    }

    public void setItemIconPadding(int padding) {
        this.presenter.setItemIconPadding(padding);
    }

    public void setItemIconPaddingResource(int paddingResource) {
        this.presenter.setItemIconPadding(getResources().getDimensionPixelSize(paddingResource));
    }

    public void setCheckedItem(int id) {
        MenuItem item = this.menu.findItem(id);
        if (item != null) {
            this.presenter.setCheckedItem((MenuItemImpl) item);
        }
    }

    public void setCheckedItem(MenuItem checkedItem) {
        MenuItem item = this.menu.findItem(checkedItem.getItemId());
        if (item != null) {
            this.presenter.setCheckedItem((MenuItemImpl) item);
            return;
        }
        throw new IllegalArgumentException("Called setCheckedItem(MenuItem) with an item that is not in the current menu.");
    }

    public MenuItem getCheckedItem() {
        return this.presenter.getCheckedItem();
    }

    public void setItemTextAppearance(int resId) {
        this.presenter.setItemTextAppearance(resId);
    }

    private MenuInflater getMenuInflater() {
        if (this.menuInflater == null) {
            this.menuInflater = new SupportMenuInflater(getContext());
        }
        return this.menuInflater;
    }

    private ColorStateList createDefaultColorStateList(int baseColorThemeAttr) {
        TypedValue value = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(baseColorThemeAttr, value, true)) {
            return null;
        }
        ColorStateList baseColor = AppCompatResources.getColorStateList(getContext(), value.resourceId);
        if (!getContext().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.colorPrimary, value, true)) {
            return null;
        }
        int colorPrimary = value.data;
        int defaultColor = baseColor.getDefaultColor();
        return new ColorStateList(new int[][]{DISABLED_STATE_SET, CHECKED_STATE_SET, EMPTY_STATE_SET}, new int[]{baseColor.getColorForState(DISABLED_STATE_SET, defaultColor), colorPrimary, defaultColor});
    }
}
