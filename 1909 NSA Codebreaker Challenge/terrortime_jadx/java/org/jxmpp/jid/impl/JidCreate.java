package org.jxmpp.jid.impl;

import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.DomainFullJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Domainpart;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;
import org.jxmpp.util.cache.Cache;
import org.jxmpp.util.cache.LruCache;

public class JidCreate {
    private static final Cache<String, BareJid> BAREJID_CACHE = new LruCache(100);
    private static final Cache<String, DomainBareJid> DOMAINJID_CACHE = new LruCache(100);
    private static final Cache<String, DomainFullJid> DOMAINRESOURCEJID_CACHE = new LruCache(100);
    private static final Cache<String, EntityJid> ENTITYJID_CACHE = new LruCache(100);
    private static final Cache<String, EntityBareJid> ENTITY_BAREJID_CACHE = new LruCache(100);
    private static final Cache<String, EntityFullJid> ENTITY_FULLJID_CACHE = new LruCache(100);
    private static final Cache<String, FullJid> FULLJID_CACHE = new LruCache(100);
    private static final Cache<String, Jid> JID_CACHE = new LruCache(100);

    public static Jid from(CharSequence localpart, CharSequence domainpart, CharSequence resource) throws XmppStringprepException {
        return from(localpart.toString(), domainpart.toString(), resource.toString());
    }

    public static Jid from(String localpart, String domainpart, String resource) throws XmppStringprepException {
        Jid jid;
        String jidString = XmppStringUtils.completeJidFrom(localpart, domainpart, resource);
        Jid jid2 = (Jid) JID_CACHE.lookup(jidString);
        if (jid2 != null) {
            return jid2;
        }
        if (localpart.length() > 0 && domainpart.length() > 0 && resource.length() > 0) {
            jid = new LocalDomainAndResourcepartJid(localpart, domainpart, resource);
        } else if (localpart.length() > 0 && domainpart.length() > 0 && resource.length() == 0) {
            jid = new LocalAndDomainpartJid(localpart, domainpart);
        } else if (localpart.length() == 0 && domainpart.length() > 0 && resource.length() == 0) {
            jid = new DomainpartJid(domainpart);
        } else if (localpart.length() != 0 || domainpart.length() <= 0 || resource.length() <= 0) {
            throw new IllegalArgumentException("Not a valid combination of localpart, domainpart and resource");
        } else {
            jid = new DomainAndResourcepartJid(domainpart, resource);
        }
        JID_CACHE.put(jidString, jid);
        return jid;
    }

    public static Jid fromOrThrowUnchecked(CharSequence cs) {
        try {
            return from(cs);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Jid from(CharSequence jid) throws XmppStringprepException {
        return from(jid.toString());
    }

    public static Jid from(String jidString) throws XmppStringprepException {
        try {
            return from(XmppStringUtils.parseLocalpart(jidString), XmppStringUtils.parseDomain(jidString), XmppStringUtils.parseResource(jidString));
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jidString, (Exception) e);
        }
    }

    public static Jid fromOrNull(CharSequence cs) {
        try {
            return from(cs);
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static Jid fromUnescapedOrThrowUnchecked(CharSequence cs) {
        try {
            return fromUnescaped(cs);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Jid fromUnescaped(CharSequence unescapedJid) throws XmppStringprepException {
        return fromUnescaped(unescapedJid.toString());
    }

    public static Jid fromUnescaped(String unescapedJidString) throws XmppStringprepException {
        try {
            return from(XmppStringUtils.escapeLocalpart(XmppStringUtils.parseLocalpart(unescapedJidString)), XmppStringUtils.parseDomain(unescapedJidString), XmppStringUtils.parseResource(unescapedJidString));
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(unescapedJidString, (Exception) e);
        }
    }

    public static Jid fromUnescapedOrNull(CharSequence cs) {
        try {
            return fromUnescaped(cs);
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static BareJid bareFromOrThrowUnchecked(CharSequence cs) {
        try {
            return bareFrom(cs);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static BareJid bareFrom(CharSequence jid) throws XmppStringprepException {
        return bareFrom(jid.toString());
    }

    public static BareJid bareFrom(String jid) throws XmppStringprepException {
        BareJid bareJid;
        BareJid bareJid2 = (BareJid) BAREJID_CACHE.lookup(jid);
        if (bareJid2 != null) {
            return bareJid2;
        }
        String localpart = XmppStringUtils.parseLocalpart(jid);
        String domainpart = XmppStringUtils.parseDomain(jid);
        try {
            if (localpart.length() != 0) {
                bareJid = new LocalAndDomainpartJid(localpart, domainpart);
            } else {
                bareJid = new DomainpartJid(domainpart);
            }
            BAREJID_CACHE.put(jid, bareJid);
            return bareJid;
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jid, (Exception) e);
        }
    }

    public static BareJid bareFrom(Localpart localpart, DomainBareJid domainBareJid) {
        return bareFrom(localpart, domainBareJid.getDomain());
    }

    public static BareJid bareFrom(Localpart localpart, Domainpart domain) {
        if (localpart != null) {
            return new LocalAndDomainpartJid(localpart, domain);
        }
        return new DomainpartJid(domain);
    }

    public static BareJid bareFromOrNull(CharSequence cs) {
        try {
            return bareFrom(cs);
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static FullJid fullFromOrThrowUnchecked(CharSequence cs) {
        try {
            return fullFrom(cs);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static FullJid fullFrom(CharSequence jid) throws XmppStringprepException {
        return fullFrom(jid.toString());
    }

    public static FullJid fullFrom(String jid) throws XmppStringprepException {
        FullJid fullJid = (FullJid) FULLJID_CACHE.lookup(jid);
        if (fullJid != null) {
            return fullJid;
        }
        try {
            FullJid fullJid2 = fullFrom(XmppStringUtils.parseLocalpart(jid), XmppStringUtils.parseDomain(jid), XmppStringUtils.parseResource(jid));
            FULLJID_CACHE.put(jid, fullJid2);
            return fullJid2;
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jid, (Exception) e);
        }
    }

    public static FullJid fullFrom(String localpart, String domainpart, String resource) throws XmppStringprepException {
        if (localpart != null) {
            try {
                if (localpart.length() != 0) {
                    return new LocalDomainAndResourcepartJid(localpart, domainpart, resource);
                }
            } catch (XmppStringprepException e) {
                StringBuilder sb = new StringBuilder();
                sb.append(localpart);
                sb.append('@');
                sb.append(domainpart);
                sb.append('/');
                sb.append(resource);
                throw new XmppStringprepException(sb.toString(), (Exception) e);
            }
        }
        return new DomainAndResourcepartJid(domainpart, resource);
    }

    public static FullJid fullFrom(Localpart localpart, DomainBareJid domainBareJid, Resourcepart resource) {
        return fullFrom(localpart, domainBareJid.getDomain(), resource);
    }

    public static FullJid fullFrom(Localpart localpart, Domainpart domainpart, Resourcepart resource) {
        return fullFrom(entityBareFrom(localpart, domainpart), resource);
    }

    public static FullJid fullFromOrNull(CharSequence cs) {
        try {
            return fullFrom(cs);
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static EntityJid entityFromOrThrowUnchecked(CharSequence cs) {
        try {
            return entityFrom(cs);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static EntityFullJid fullFrom(EntityBareJid bareJid, Resourcepart resource) {
        return new LocalDomainAndResourcepartJid(bareJid, resource);
    }

    public static EntityJid entityFrom(CharSequence jid) throws XmppStringprepException {
        return entityFrom(jid.toString());
    }

    public static EntityJid entityFrom(String jidString) throws XmppStringprepException {
        return entityFrom(jidString, false);
    }

    public static EntityJid entityFromUnescapedOrThrowUnchecked(CharSequence cs) {
        try {
            return entityFromUnescaped(cs);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static EntityJid entityFromUnescaped(CharSequence jid) throws XmppStringprepException {
        return entityFromUnescaped(jid.toString());
    }

    public static EntityJid entityFromUnescaped(String jidString) throws XmppStringprepException {
        return entityFrom(jidString, true);
    }

    public static EntityJid entityFromUnesacpedOrNull(CharSequence cs) {
        try {
            return entityFromUnescaped(cs.toString());
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    private static EntityJid entityFrom(String jidString, boolean unescaped) throws XmppStringprepException {
        Localpart localpart;
        EntityJid entityJid;
        EntityJid entityJid2 = (EntityJid) ENTITYJID_CACHE.lookup(jidString);
        if (entityJid2 != null) {
            return entityJid2;
        }
        String localpartString = XmppStringUtils.parseLocalpart(jidString);
        if (localpartString.length() != 0) {
            if (unescaped) {
                try {
                    localpart = Localpart.fromUnescaped(localpartString);
                } catch (XmppStringprepException e) {
                    throw new XmppStringprepException(jidString, (Exception) e);
                }
            } else {
                localpart = Localpart.from(localpartString);
            }
            try {
                Domainpart domainpart = Domainpart.from(XmppStringUtils.parseDomain(jidString));
                String resourceString = XmppStringUtils.parseResource(jidString);
                if (resourceString.length() > 0) {
                    try {
                        entityJid = entityFullFrom(localpart, domainpart, Resourcepart.from(resourceString));
                    } catch (XmppStringprepException e2) {
                        throw new XmppStringprepException(jidString, (Exception) e2);
                    }
                } else {
                    entityJid = entityBareFrom(localpart, domainpart);
                }
                ENTITYJID_CACHE.put(jidString, entityJid);
                return entityJid;
            } catch (XmppStringprepException e3) {
                throw new XmppStringprepException(jidString, (Exception) e3);
            }
        } else {
            throw new XmppStringprepException("Does not contain a localpart", jidString);
        }
    }

    public static EntityJid entityFromOrNull(CharSequence cs) {
        try {
            return entityFrom(cs);
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static EntityBareJid entityBareFromOrThrowUnchecked(CharSequence cs) {
        try {
            return entityBareFrom(cs);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static EntityBareJid entityBareFrom(CharSequence jid) throws XmppStringprepException {
        return entityBareFrom(jid.toString());
    }

    public static EntityBareJid entityBareFrom(String jid) throws XmppStringprepException {
        EntityBareJid bareJid = (EntityBareJid) ENTITY_BAREJID_CACHE.lookup(jid);
        if (bareJid != null) {
            return bareJid;
        }
        try {
            EntityBareJid bareJid2 = new LocalAndDomainpartJid(XmppStringUtils.parseLocalpart(jid), XmppStringUtils.parseDomain(jid));
            ENTITY_BAREJID_CACHE.put(jid, bareJid2);
            return bareJid2;
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jid, (Exception) e);
        }
    }

    public static EntityBareJid entityBareFromUnescapedOrThrowUnchecked(CharSequence cs) {
        try {
            return entityBareFromUnescaped(cs);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static EntityBareJid entityBareFromUnescaped(CharSequence unescapedJid) throws XmppStringprepException {
        return entityBareFromUnescaped(unescapedJid.toString());
    }

    public static EntityBareJid entityBareFromUnescaped(String unescapedJidString) throws XmppStringprepException {
        EntityBareJid bareJid = (EntityBareJid) ENTITY_BAREJID_CACHE.lookup(unescapedJidString);
        if (bareJid != null) {
            return bareJid;
        }
        try {
            EntityBareJid bareJid2 = new LocalAndDomainpartJid(XmppStringUtils.escapeLocalpart(XmppStringUtils.parseLocalpart(unescapedJidString)), XmppStringUtils.parseDomain(unescapedJidString));
            ENTITY_BAREJID_CACHE.put(unescapedJidString, bareJid2);
            return bareJid2;
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(unescapedJidString, (Exception) e);
        }
    }

    public static EntityBareJid entityBareFromUnescapedOrNull(CharSequence cs) {
        try {
            return entityBareFromUnescaped(cs.toString());
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static EntityBareJid entityBareFrom(Localpart localpart, DomainBareJid domainBareJid) {
        return entityBareFrom(localpart, domainBareJid.getDomain());
    }

    public static EntityBareJid entityBareFrom(Localpart localpart, Domainpart domain) {
        return new LocalAndDomainpartJid(localpart, domain);
    }

    public static EntityBareJid entityBareFromOrNull(CharSequence cs) {
        try {
            return entityBareFrom(cs);
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static EntityFullJid entityFullFromOrThrowUnchecked(CharSequence cs) {
        try {
            return entityFullFrom(cs);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static EntityFullJid entityFullFrom(CharSequence jid) throws XmppStringprepException {
        return entityFullFrom(jid.toString());
    }

    public static EntityFullJid entityFullFrom(String jid) throws XmppStringprepException {
        EntityFullJid fullJid = (EntityFullJid) ENTITY_FULLJID_CACHE.lookup(jid);
        if (fullJid != null) {
            return fullJid;
        }
        try {
            EntityFullJid fullJid2 = entityFullFrom(XmppStringUtils.parseLocalpart(jid), XmppStringUtils.parseDomain(jid), XmppStringUtils.parseResource(jid));
            ENTITY_FULLJID_CACHE.put(jid, fullJid2);
            return fullJid2;
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jid, (Exception) e);
        }
    }

    public static EntityFullJid entityFullFromOrNull(CharSequence cs) {
        try {
            return entityFullFrom(cs);
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static EntityFullJid entityFullFromUnescapedOrThrowUnchecked(CharSequence cs) {
        try {
            return entityFullFromUnescaped(cs);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static EntityFullJid entityFullFromUnescaped(CharSequence unescapedJid) throws XmppStringprepException {
        return entityFullFromUnescaped(unescapedJid.toString());
    }

    public static EntityFullJid entityFullFromUnescaped(String unescapedJidString) throws XmppStringprepException {
        EntityFullJid fullJid = (EntityFullJid) ENTITY_FULLJID_CACHE.lookup(unescapedJidString);
        if (fullJid != null) {
            return fullJid;
        }
        try {
            EntityFullJid fullJid2 = new LocalDomainAndResourcepartJid(XmppStringUtils.escapeLocalpart(XmppStringUtils.parseLocalpart(unescapedJidString)), XmppStringUtils.parseDomain(unescapedJidString), XmppStringUtils.parseResource(unescapedJidString));
            ENTITY_FULLJID_CACHE.put(unescapedJidString, fullJid2);
            return fullJid2;
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(unescapedJidString, (Exception) e);
        }
    }

    public static EntityFullJid entityFullFromUnescapedOrNull(CharSequence cs) {
        try {
            return entityFullFromUnescaped(cs.toString());
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    public static EntityFullJid entityFullFrom(String localpart, String domainpart, String resource) throws XmppStringprepException {
        try {
            return new LocalDomainAndResourcepartJid(localpart, domainpart, resource);
        } catch (XmppStringprepException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(localpart);
            sb.append('@');
            sb.append(domainpart);
            sb.append('/');
            sb.append(resource);
            throw new XmppStringprepException(sb.toString(), (Exception) e);
        }
    }

    public static EntityFullJid entityFullFrom(Localpart localpart, DomainBareJid domainBareJid, Resourcepart resource) {
        return entityFullFrom(localpart, domainBareJid.getDomain(), resource);
    }

    public static EntityFullJid entityFullFrom(Localpart localpart, Domainpart domainpart, Resourcepart resource) {
        return entityFullFrom(entityBareFrom(localpart, domainpart), resource);
    }

    public static EntityFullJid entityFullFrom(EntityBareJid bareJid, Resourcepart resource) {
        return new LocalDomainAndResourcepartJid(bareJid, resource);
    }

    @Deprecated
    public static DomainBareJid serverBareFrom(String jid) throws XmppStringprepException {
        return domainBareFrom(jid);
    }

    public static DomainBareJid domainBareFromOrThrowUnchecked(CharSequence cs) {
        try {
            return domainBareFrom(cs);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static DomainBareJid domainBareFrom(CharSequence jid) throws XmppStringprepException {
        return domainBareFrom(jid.toString());
    }

    public static DomainBareJid domainBareFrom(String jid) throws XmppStringprepException {
        DomainBareJid domainJid = (DomainBareJid) DOMAINJID_CACHE.lookup(jid);
        if (domainJid != null) {
            return domainJid;
        }
        try {
            DomainBareJid domainJid2 = new DomainpartJid(XmppStringUtils.parseDomain(jid));
            DOMAINJID_CACHE.put(jid, domainJid2);
            return domainJid2;
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jid, (Exception) e);
        }
    }

    public static DomainBareJid domainBareFrom(Domainpart domainpart) {
        return new DomainpartJid(domainpart);
    }

    public static DomainBareJid domainBareFromOrNull(CharSequence cs) {
        try {
            return domainBareFrom(cs);
        } catch (XmppStringprepException e) {
            return null;
        }
    }

    @Deprecated
    public static DomainFullJid serverFullFrom(String jid) throws XmppStringprepException {
        return donmainFullFrom(jid);
    }

    @Deprecated
    public static DomainFullJid donmainFullFrom(String jid) throws XmppStringprepException {
        return domainFullFrom(jid);
    }

    public static DomainFullJid domainFullFromOrThrowUnchecked(CharSequence cs) {
        try {
            return domainFullFrom(cs);
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static DomainFullJid domainFullFrom(CharSequence jid) throws XmppStringprepException {
        return domainFullFrom(jid.toString());
    }

    public static DomainFullJid domainFullFrom(String jid) throws XmppStringprepException {
        DomainFullJid domainResourceJid = (DomainFullJid) DOMAINRESOURCEJID_CACHE.lookup(jid);
        if (domainResourceJid != null) {
            return domainResourceJid;
        }
        try {
            DomainFullJid domainResourceJid2 = new DomainAndResourcepartJid(XmppStringUtils.parseDomain(jid), XmppStringUtils.parseResource(jid));
            DOMAINRESOURCEJID_CACHE.put(jid, domainResourceJid2);
            return domainResourceJid2;
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jid, (Exception) e);
        }
    }

    public static DomainFullJid domainFullFrom(Domainpart domainpart, Resourcepart resource) {
        return domainFullFrom(domainBareFrom(domainpart), resource);
    }

    public static DomainFullJid domainFullFrom(DomainBareJid domainBareJid, Resourcepart resource) {
        return new DomainAndResourcepartJid(domainBareJid, resource);
    }

    public static DomainFullJid domainFullFromOrNull(CharSequence cs) {
        try {
            return domainFullFrom(cs);
        } catch (XmppStringprepException e) {
            return null;
        }
    }
}
