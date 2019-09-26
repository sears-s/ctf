package com.badguy.terrortime;

public class ParameterValidatorClass {
    private final String ipv4Address = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
    private final String pin = "\\d\\d\\d\\d\\d\\d";

    /* access modifiers changed from: 0000 */
    public boolean isValidUserName(String username) {
        if (username == null) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean isValidPin(String pin2) {
        if (pin2 == null) {
            return false;
        }
        getClass();
        return pin2.matches("\\d\\d\\d\\d\\d\\d");
    }

    /* access modifiers changed from: 0000 */
    public boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean isValidIpAddress(String ipAddress) {
        if (ipAddress == null) {
            return false;
        }
        return true;
    }
}
