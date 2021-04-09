package org.bouncycastle.crypto.params;

public class DHKeyParameters extends AsymmetricKeyParameter {
    private DHParameters params;

    protected DHKeyParameters(boolean z, DHParameters dHParameters) {
        super(z);
        this.params = dHParameters;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof DHKeyParameters)) {
            return false;
        }
        DHKeyParameters dHKeyParameters = (DHKeyParameters) obj;
        DHParameters dHParameters = this.params;
        DHParameters parameters = dHKeyParameters.getParameters();
        if (dHParameters != null) {
            return dHParameters.equals(parameters);
        }
        if (parameters == null) {
            z = true;
        }
        return z;
    }

    public DHParameters getParameters() {
        return this.params;
    }

    public int hashCode() {
        boolean z = !isPrivate();
        DHParameters dHParameters = this.params;
        return dHParameters != null ? z ^ dHParameters.hashCode() ? 1 : 0 : z ? 1 : 0;
    }
}
