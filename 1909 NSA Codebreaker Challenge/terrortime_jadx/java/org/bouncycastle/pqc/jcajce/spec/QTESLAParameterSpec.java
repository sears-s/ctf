package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.pqc.crypto.qtesla.QTESLASecurityCategory;

public class QTESLAParameterSpec implements AlgorithmParameterSpec {
    public static final String HEURISTIC_I = QTESLASecurityCategory.getName(0);
    public static final String HEURISTIC_III_SIZE = QTESLASecurityCategory.getName(1);
    public static final String HEURISTIC_III_SPEED = QTESLASecurityCategory.getName(2);
    public static final String PROVABLY_SECURE_I = QTESLASecurityCategory.getName(3);
    public static final String PROVABLY_SECURE_III = QTESLASecurityCategory.getName(4);
    private String securityCategory;

    public QTESLAParameterSpec(String str) {
        this.securityCategory = str;
    }

    public String getSecurityCategory() {
        return this.securityCategory;
    }
}
