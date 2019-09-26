package androidx.navigation;

import android.os.Bundle;

public final class NavArgument {
    private final Object mDefaultValue;
    private final boolean mDefaultValuePresent;
    private final boolean mIsNullable;
    private final NavType mType;

    public static final class Builder {
        private Object mDefaultValue;
        private boolean mDefaultValuePresent = false;
        private boolean mIsNullable = false;
        private NavType<?> mType;

        public Builder setType(NavType<?> type) {
            this.mType = type;
            return this;
        }

        public Builder setIsNullable(boolean isNullable) {
            this.mIsNullable = isNullable;
            return this;
        }

        public Builder setDefaultValue(Object defaultValue) {
            this.mDefaultValue = defaultValue;
            this.mDefaultValuePresent = true;
            return this;
        }

        public NavArgument build() {
            if (this.mType == null) {
                this.mType = NavType.inferFromValueType(this.mDefaultValue);
            }
            return new NavArgument(this.mType, this.mIsNullable, this.mDefaultValue, this.mDefaultValuePresent);
        }
    }

    NavArgument(NavType<?> type, boolean isNullable, Object defaultValue, boolean defaultValuePresent) {
        if (!type.isNullableAllowed() && isNullable) {
            StringBuilder sb = new StringBuilder();
            sb.append(type.getName());
            sb.append(" does not allow nullable values");
            throw new IllegalArgumentException(sb.toString());
        } else if (isNullable || !defaultValuePresent || defaultValue != null) {
            this.mType = type;
            this.mIsNullable = isNullable;
            this.mDefaultValue = defaultValue;
            this.mDefaultValuePresent = defaultValuePresent;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Argument with type ");
            sb2.append(type.getName());
            sb2.append(" has null value but is not nullable.");
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    public boolean isDefaultValuePresent() {
        return this.mDefaultValuePresent;
    }

    public NavType<?> getType() {
        return this.mType;
    }

    public boolean isNullable() {
        return this.mIsNullable;
    }

    public Object getDefaultValue() {
        return this.mDefaultValue;
    }

    /* access modifiers changed from: 0000 */
    public void putDefaultValue(String name, Bundle bundle) {
        if (this.mDefaultValuePresent) {
            this.mType.put(bundle, name, this.mDefaultValue);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean verify(String name, Bundle bundle) {
        if (!this.mIsNullable && bundle.containsKey(name) && bundle.get(name) == null) {
            return false;
        }
        try {
            this.mType.get(bundle, name);
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NavArgument that = (NavArgument) o;
        if (this.mIsNullable != that.mIsNullable || this.mDefaultValuePresent != that.mDefaultValuePresent || !this.mType.equals(that.mType)) {
            return false;
        }
        Object obj = this.mDefaultValue;
        if (obj != null) {
            z = obj.equals(that.mDefaultValue);
        } else if (that.mDefaultValue != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int result = ((((this.mType.hashCode() * 31) + (this.mIsNullable ? 1 : 0)) * 31) + (this.mDefaultValuePresent ? 1 : 0)) * 31;
        Object obj = this.mDefaultValue;
        return result + (obj != null ? obj.hashCode() : 0);
    }
}
