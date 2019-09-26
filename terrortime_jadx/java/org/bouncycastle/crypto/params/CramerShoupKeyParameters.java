package org.bouncycastle.crypto.params;

public class CramerShoupKeyParameters extends AsymmetricKeyParameter {
    private CramerShoupParameters params;

    protected CramerShoupKeyParameters(boolean z, CramerShoupParameters cramerShoupParameters) {
        super(z);
        this.params = cramerShoupParameters;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof CramerShoupKeyParameters)) {
            return false;
        }
        CramerShoupKeyParameters cramerShoupKeyParameters = (CramerShoupKeyParameters) obj;
        CramerShoupParameters cramerShoupParameters = this.params;
        CramerShoupParameters parameters = cramerShoupKeyParameters.getParameters();
        if (cramerShoupParameters != null) {
            return cramerShoupParameters.equals(parameters);
        }
        if (parameters == null) {
            z = true;
        }
        return z;
    }

    public CramerShoupParameters getParameters() {
        return this.params;
    }

    public int hashCode() {
        boolean z = !isPrivate();
        CramerShoupParameters cramerShoupParameters = this.params;
        return cramerShoupParameters != null ? z ^ cramerShoupParameters.hashCode() ? 1 : 0 : z ? 1 : 0;
    }
}
