package org.bouncycastle.math.ec;

class ValidityPrecompInfo implements PreCompInfo {
    static final String PRECOMP_NAME = "bc_validity";
    private boolean curveEquationPassed = false;
    private boolean failed = false;
    private boolean orderPassed = false;

    ValidityPrecompInfo() {
    }

    /* access modifiers changed from: 0000 */
    public boolean hasCurveEquationPassed() {
        return this.curveEquationPassed;
    }

    /* access modifiers changed from: 0000 */
    public boolean hasFailed() {
        return this.failed;
    }

    /* access modifiers changed from: 0000 */
    public boolean hasOrderPassed() {
        return this.orderPassed;
    }

    /* access modifiers changed from: 0000 */
    public void reportCurveEquationPassed() {
        this.curveEquationPassed = true;
    }

    /* access modifiers changed from: 0000 */
    public void reportFailed() {
        this.failed = true;
    }

    /* access modifiers changed from: 0000 */
    public void reportOrderPassed() {
        this.orderPassed = true;
    }
}
