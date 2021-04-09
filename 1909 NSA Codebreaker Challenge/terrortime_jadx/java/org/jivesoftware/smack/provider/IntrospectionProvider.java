package org.jivesoftware.smack.provider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.ParserUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class IntrospectionProvider {

    public static abstract class IQIntrospectionProvider<I extends IQ> extends IQProvider<I> {
        private final Class<I> elementClass;

        protected IQIntrospectionProvider(Class<I> elementClass2) {
            this.elementClass = elementClass2;
        }

        public I parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
            try {
                return (IQ) IntrospectionProvider.parseWithIntrospection(this.elementClass, parser, initialDepth);
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                throw new SmackException((Throwable) e);
            }
        }
    }

    public static abstract class PacketExtensionIntrospectionProvider<PE extends ExtensionElement> extends ExtensionElementProvider<PE> {
        private final Class<PE> elementClass;

        protected PacketExtensionIntrospectionProvider(Class<PE> elementClass2) {
            this.elementClass = elementClass2;
        }

        public PE parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
            try {
                return (ExtensionElement) IntrospectionProvider.parseWithIntrospection(this.elementClass, parser, initialDepth);
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                throw new SmackException((Throwable) e);
            }
        }
    }

    public static Object parseWithIntrospection(Class<?> objectClass, XmlPullParser parser, int initialDepth) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, XmlPullParserException, IOException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        ParserUtils.assertAtStartTag(parser);
        Object object = objectClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        while (true) {
            int eventType = parser.next();
            if (eventType == 2) {
                String name = parser.getName();
                String stringValue = parser.nextText();
                Class cls = object.getClass();
                StringBuilder sb = new StringBuilder();
                sb.append("get");
                sb.append(Character.toUpperCase(name.charAt(0)));
                sb.append(name.substring(1));
                Class<?> propertyType = cls.getMethod(sb.toString(), new Class[0]).getReturnType();
                Object value = decode(propertyType, stringValue);
                Class cls2 = object.getClass();
                StringBuilder sb2 = new StringBuilder();
                sb2.append("set");
                sb2.append(Character.toUpperCase(name.charAt(0)));
                sb2.append(name.substring(1));
                cls2.getMethod(sb2.toString(), new Class[]{propertyType}).invoke(object, new Object[]{value});
            } else if (eventType == 3 && parser.getDepth() == initialDepth) {
                ParserUtils.assertAtEndTag(parser);
                return object;
            }
        }
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.Object decode(java.lang.Class<?> r2, java.lang.String r3) throws java.lang.ClassNotFoundException {
        /*
            java.lang.String r0 = r2.getName()
            int r1 = r0.hashCode()
            switch(r1) {
                case -1325958191: goto L_0x005d;
                case -530663260: goto L_0x0052;
                case 104431: goto L_0x0048;
                case 3039496: goto L_0x003e;
                case 3327612: goto L_0x0034;
                case 64711720: goto L_0x002a;
                case 97526364: goto L_0x0020;
                case 109413500: goto L_0x0016;
                case 1195259493: goto L_0x000c;
                default: goto L_0x000b;
            }
        L_0x000b:
            goto L_0x0067
        L_0x000c:
            java.lang.String r1 = "java.lang.String"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000b
            r1 = 0
            goto L_0x0068
        L_0x0016:
            java.lang.String r1 = "short"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000b
            r1 = 6
            goto L_0x0068
        L_0x0020:
            java.lang.String r1 = "float"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000b
            r1 = 4
            goto L_0x0068
        L_0x002a:
            java.lang.String r1 = "boolean"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000b
            r1 = 1
            goto L_0x0068
        L_0x0034:
            java.lang.String r1 = "long"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000b
            r1 = 3
            goto L_0x0068
        L_0x003e:
            java.lang.String r1 = "byte"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000b
            r1 = 7
            goto L_0x0068
        L_0x0048:
            java.lang.String r1 = "int"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000b
            r1 = 2
            goto L_0x0068
        L_0x0052:
            java.lang.String r1 = "java.lang.Class"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000b
            r1 = 8
            goto L_0x0068
        L_0x005d:
            java.lang.String r1 = "double"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x000b
            r1 = 5
            goto L_0x0068
        L_0x0067:
            r1 = -1
        L_0x0068:
            switch(r1) {
                case 0: goto L_0x0095;
                case 1: goto L_0x0090;
                case 2: goto L_0x008b;
                case 3: goto L_0x0086;
                case 4: goto L_0x0081;
                case 5: goto L_0x007c;
                case 6: goto L_0x0077;
                case 7: goto L_0x0072;
                case 8: goto L_0x006d;
                default: goto L_0x006b;
            }
        L_0x006b:
            r1 = 0
            return r1
        L_0x006d:
            java.lang.Class r1 = java.lang.Class.forName(r3)
            return r1
        L_0x0072:
            java.lang.Byte r1 = java.lang.Byte.valueOf(r3)
            return r1
        L_0x0077:
            java.lang.Short r1 = java.lang.Short.valueOf(r3)
            return r1
        L_0x007c:
            java.lang.Double r1 = java.lang.Double.valueOf(r3)
            return r1
        L_0x0081:
            java.lang.Float r1 = java.lang.Float.valueOf(r3)
            return r1
        L_0x0086:
            java.lang.Long r1 = java.lang.Long.valueOf(r3)
            return r1
        L_0x008b:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r3)
            return r1
        L_0x0090:
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r3)
            return r1
        L_0x0095:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jivesoftware.smack.provider.IntrospectionProvider.decode(java.lang.Class, java.lang.String):java.lang.Object");
    }
}
