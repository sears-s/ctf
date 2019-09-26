package org.bouncycastle.jce.provider;

import java.security.Permission;
import java.security.spec.DSAParameterSpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.CryptoServicesRegistrar.Property;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.config.ProviderConfigurationPermission;
import org.bouncycastle.jcajce.spec.DHDomainParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;

class BouncyCastleProviderConfiguration implements ProviderConfiguration {
    private static Permission BC_ADDITIONAL_EC_CURVE_PERMISSION;
    private static Permission BC_DH_LOCAL_PERMISSION;
    private static Permission BC_DH_PERMISSION;
    private static Permission BC_EC_CURVE_PERMISSION;
    private static Permission BC_EC_LOCAL_PERMISSION;
    private static Permission BC_EC_PERMISSION;
    private volatile Set acceptableNamedCurves = new HashSet();
    private volatile Map additionalECParameters = new HashMap();
    private volatile Object dhDefaultParams;
    private ThreadLocal dhThreadSpec = new ThreadLocal();
    private volatile ECParameterSpec ecImplicitCaParams;
    private ThreadLocal ecThreadSpec = new ThreadLocal();

    static {
        String str = "BC";
        BC_EC_LOCAL_PERMISSION = new ProviderConfigurationPermission(str, ConfigurableProvider.THREAD_LOCAL_EC_IMPLICITLY_CA);
        BC_EC_PERMISSION = new ProviderConfigurationPermission(str, ConfigurableProvider.EC_IMPLICITLY_CA);
        BC_DH_LOCAL_PERMISSION = new ProviderConfigurationPermission(str, ConfigurableProvider.THREAD_LOCAL_DH_DEFAULT_PARAMS);
        BC_DH_PERMISSION = new ProviderConfigurationPermission(str, ConfigurableProvider.DH_DEFAULT_PARAMS);
        BC_EC_CURVE_PERMISSION = new ProviderConfigurationPermission(str, ConfigurableProvider.ACCEPTABLE_EC_CURVES);
        BC_ADDITIONAL_EC_CURVE_PERMISSION = new ProviderConfigurationPermission(str, ConfigurableProvider.ADDITIONAL_EC_PARAMETERS);
    }

    BouncyCastleProviderConfiguration() {
    }

    public Set getAcceptableNamedCurves() {
        return Collections.unmodifiableSet(this.acceptableNamedCurves);
    }

    public Map getAdditionalECParameters() {
        return Collections.unmodifiableMap(this.additionalECParameters);
    }

    public DHParameterSpec getDHDefaultParameters(int i) {
        Object obj = this.dhThreadSpec.get();
        if (obj == null) {
            obj = this.dhDefaultParams;
        }
        if (obj instanceof DHParameterSpec) {
            DHParameterSpec dHParameterSpec = (DHParameterSpec) obj;
            if (dHParameterSpec.getP().bitLength() == i) {
                return dHParameterSpec;
            }
        } else if (obj instanceof DHParameterSpec[]) {
            DHParameterSpec[] dHParameterSpecArr = (DHParameterSpec[]) obj;
            for (int i2 = 0; i2 != dHParameterSpecArr.length; i2++) {
                if (dHParameterSpecArr[i2].getP().bitLength() == i) {
                    return dHParameterSpecArr[i2];
                }
            }
        }
        DHParameters dHParameters = (DHParameters) CryptoServicesRegistrar.getSizedProperty(Property.DH_DEFAULT_PARAMS, i);
        if (dHParameters != null) {
            return new DHDomainParameterSpec(dHParameters);
        }
        return null;
    }

    public DSAParameterSpec getDSADefaultParameters(int i) {
        DSAParameters dSAParameters = (DSAParameters) CryptoServicesRegistrar.getSizedProperty(Property.DSA_DEFAULT_PARAMS, i);
        if (dSAParameters != null) {
            return new DSAParameterSpec(dSAParameters.getP(), dSAParameters.getQ(), dSAParameters.getG());
        }
        return null;
    }

    public ECParameterSpec getEcImplicitlyCa() {
        ECParameterSpec eCParameterSpec = (ECParameterSpec) this.ecThreadSpec.get();
        return eCParameterSpec != null ? eCParameterSpec : this.ecImplicitCaParams;
    }

    /* access modifiers changed from: 0000 */
    public void setParameter(String str, Object obj) {
        ThreadLocal threadLocal;
        SecurityManager securityManager = System.getSecurityManager();
        if (str.equals(ConfigurableProvider.THREAD_LOCAL_EC_IMPLICITLY_CA)) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_EC_LOCAL_PERMISSION);
            }
            ECParameterSpec convertSpec = ((obj instanceof ECParameterSpec) || obj == null) ? (ECParameterSpec) obj : EC5Util.convertSpec((java.security.spec.ECParameterSpec) obj, false);
            if (convertSpec == null) {
                threadLocal = this.ecThreadSpec;
            } else {
                this.ecThreadSpec.set(convertSpec);
                return;
            }
        } else if (str.equals(ConfigurableProvider.EC_IMPLICITLY_CA)) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_EC_PERMISSION);
            }
            if ((obj instanceof ECParameterSpec) || obj == null) {
                this.ecImplicitCaParams = (ECParameterSpec) obj;
                return;
            } else {
                this.ecImplicitCaParams = EC5Util.convertSpec((java.security.spec.ECParameterSpec) obj, false);
                return;
            }
        } else if (str.equals(ConfigurableProvider.THREAD_LOCAL_DH_DEFAULT_PARAMS)) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_DH_LOCAL_PERMISSION);
            }
            if ((obj instanceof DHParameterSpec) || (obj instanceof DHParameterSpec[]) || obj == null) {
                threadLocal = this.dhThreadSpec;
                if (obj != null) {
                    threadLocal.set(obj);
                    return;
                }
            } else {
                throw new IllegalArgumentException("not a valid DHParameterSpec");
            }
        } else if (str.equals(ConfigurableProvider.DH_DEFAULT_PARAMS)) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_DH_PERMISSION);
            }
            if ((obj instanceof DHParameterSpec) || (obj instanceof DHParameterSpec[]) || obj == null) {
                this.dhDefaultParams = obj;
                return;
            }
            throw new IllegalArgumentException("not a valid DHParameterSpec or DHParameterSpec[]");
        } else if (str.equals(ConfigurableProvider.ACCEPTABLE_EC_CURVES)) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_EC_CURVE_PERMISSION);
            }
            this.acceptableNamedCurves = (Set) obj;
            return;
        } else if (str.equals(ConfigurableProvider.ADDITIONAL_EC_PARAMETERS)) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_ADDITIONAL_EC_CURVE_PERMISSION);
            }
            this.additionalECParameters = (Map) obj;
            return;
        } else {
            return;
        }
        threadLocal.remove();
    }
}
