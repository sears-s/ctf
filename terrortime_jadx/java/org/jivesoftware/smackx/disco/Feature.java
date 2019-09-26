package org.jivesoftware.smackx.disco;

public class Feature {

    public enum Support {
        optional,
        recommended,
        required;

        public boolean isRequired() {
            return this == required;
        }

        public boolean isNotRequired() {
            return !isRequired();
        }
    }
}
