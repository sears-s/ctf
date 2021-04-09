package org.jxmpp.jid.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jxmpp.jid.DomainFullJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;

public class JidUtil {

    public static class NotAEntityBareJidStringException extends Exception {
        private static final long serialVersionUID = -1710386661031655082L;

        public NotAEntityBareJidStringException(String message) {
            super(message);
        }
    }

    public static boolean isTypicalValidEntityBareJid(CharSequence jid) {
        try {
            validateTypicalEntityBareJid(jid);
            return true;
        } catch (NotAEntityBareJidStringException | XmppStringprepException e) {
            return false;
        }
    }

    public static EntityBareJid validateTypicalEntityBareJid(CharSequence jidcs) throws NotAEntityBareJidStringException, XmppStringprepException {
        EntityBareJid jid = validateEntityBareJid(jidcs);
        if (jid.getDomain().toString().indexOf(46) != -1) {
            return jid;
        }
        throw new NotAEntityBareJidStringException("Domainpart does not include a dot ('.') character");
    }

    public static boolean isValidEntityBareJid(CharSequence jid) {
        try {
            validateEntityBareJid(jid);
            return true;
        } catch (NotAEntityBareJidStringException | XmppStringprepException e) {
            return false;
        }
    }

    public static EntityBareJid validateEntityBareJid(CharSequence jidcs) throws NotAEntityBareJidStringException, XmppStringprepException {
        String jid = jidcs.toString();
        int atIndex = jid.indexOf(64);
        String str = "'";
        if (atIndex == -1) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(jid);
            sb.append("' does not contain a '@' character");
            throw new NotAEntityBareJidStringException(sb.toString());
        } else if (jid.indexOf(64, atIndex + 1) != -1) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(jid);
            sb2.append("' contains multiple '@' characters");
            throw new NotAEntityBareJidStringException(sb2.toString());
        } else if (XmppStringUtils.parseLocalpart(jid).length() == 0) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append(jid);
            sb3.append("' has empty localpart");
            throw new NotAEntityBareJidStringException(sb3.toString());
        } else if (XmppStringUtils.parseDomain(jid).length() != 0) {
            return JidCreate.entityBareFromUnescaped(jid);
        } else {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str);
            sb4.append(jid);
            sb4.append("' has empty domainpart");
            throw new NotAEntityBareJidStringException(sb4.toString());
        }
    }

    public static void filterEntityBareJid(Collection<? extends Jid> in, Collection<? super EntityBareJid> out) {
        for (Jid jid : in) {
            EntityBareJid bareJid = jid.asEntityBareJidIfPossible();
            if (bareJid != null) {
                out.add(bareJid);
            }
        }
    }

    public static Set<EntityBareJid> filterEntityBareJidSet(Collection<? extends Jid> input) {
        Set<EntityBareJid> res = new HashSet<>(input.size());
        filterEntityBareJid(input, res);
        return res;
    }

    public static List<EntityBareJid> filterEntityBareJidList(Collection<? extends Jid> input) {
        List<EntityBareJid> res = new ArrayList<>(input.size());
        filterEntityBareJid(input, res);
        return res;
    }

    public static void filterEntityFullJid(Collection<? extends Jid> in, Collection<? super EntityFullJid> out) {
        for (Jid jid : in) {
            EntityFullJid fullJid = jid.asEntityFullJidIfPossible();
            if (fullJid != null) {
                out.add(fullJid);
            }
        }
    }

    public static Set<EntityFullJid> filterEntityFullJidSet(Collection<? extends Jid> input) {
        Set<EntityFullJid> res = new HashSet<>(input.size());
        filterEntityFullJid(input, res);
        return res;
    }

    public static List<EntityFullJid> filterEntityFullJidList(Collection<? extends Jid> input) {
        List<EntityFullJid> res = new ArrayList<>(input.size());
        filterEntityFullJid(input, res);
        return res;
    }

    public static void filterDomainFullJid(Collection<? extends Jid> in, Collection<? super DomainFullJid> out) {
        for (Jid jid : in) {
            DomainFullJid domainFullJid = jid.asDomainFullJidIfPossible();
            if (domainFullJid != null) {
                out.add(domainFullJid);
            }
        }
    }

    public static Set<DomainFullJid> filterDomainFullJidSet(Collection<? extends Jid> input) {
        Set<DomainFullJid> res = new HashSet<>(input.size());
        filterDomainFullJid(input, res);
        return res;
    }

    public static List<DomainFullJid> filterDomainFullJidList(Collection<? extends Jid> input) {
        List<DomainFullJid> res = new ArrayList<>(input.size());
        filterDomainFullJid(input, res);
        return res;
    }

    public static Set<EntityBareJid> entityBareJidSetFrom(Collection<? extends CharSequence> jidStrings) {
        Set<EntityBareJid> res = new HashSet<>(jidStrings.size());
        entityBareJidsFrom(jidStrings, res, null);
        return res;
    }

    public static void entityBareJidsFrom(Collection<? extends CharSequence> jidStrings, Collection<? super EntityBareJid> output, List<XmppStringprepException> exceptions) {
        for (CharSequence jid : jidStrings) {
            try {
                output.add(JidCreate.entityBareFrom(jid));
            } catch (XmppStringprepException e) {
                if (exceptions != null) {
                    exceptions.add(e);
                } else {
                    throw new AssertionError(e);
                }
            }
        }
    }

    public static Set<Jid> jidSetFrom(String[] jids) {
        return jidSetFrom((Collection<? extends CharSequence>) Arrays.asList(jids));
    }

    public static Set<Jid> jidSetFrom(Collection<? extends CharSequence> jidStrings) {
        Set<Jid> res = new HashSet<>(jidStrings.size());
        jidsFrom(jidStrings, res, null);
        return res;
    }

    public static void jidsFrom(Collection<? extends CharSequence> jidStrings, Collection<? super Jid> output, List<XmppStringprepException> exceptions) {
        for (CharSequence jidString : jidStrings) {
            try {
                output.add(JidCreate.from(jidString));
            } catch (XmppStringprepException e) {
                if (exceptions != null) {
                    exceptions.add(e);
                } else {
                    throw new AssertionError(e);
                }
            }
        }
    }

    public static List<String> toStringList(Collection<? extends Jid> jids) {
        List<String> res = new ArrayList<>(jids.size());
        toStrings(jids, res);
        return res;
    }

    public static Set<String> toStringSet(Collection<? extends Jid> jids) {
        Set<String> res = new HashSet<>(jids.size());
        toStrings(jids, res);
        return res;
    }

    public static void toStrings(Collection<? extends Jid> jids, Collection<? super String> jidStrings) {
        for (Jid jid : jids) {
            jidStrings.add(jid.toString());
        }
    }
}
