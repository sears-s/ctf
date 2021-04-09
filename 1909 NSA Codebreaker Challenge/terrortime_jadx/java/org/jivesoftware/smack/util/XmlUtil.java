package org.jivesoftware.smack.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlUtil {
    private static final Logger LOGGER = Logger.getLogger(XmlUtil.class.getName());
    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    static {
        try {
            transformerFactory.setAttribute("indent-number", Integer.valueOf(2));
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.INFO, "XML TransformerFactory does not support indent-number attribute", e);
        }
    }

    public static String prettyFormatXml(CharSequence xml) {
        String str = "yes";
        String xmlString = xml.toString();
        StreamSource source = new StreamSource(new StringReader(xmlString));
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", str);
            transformer.setOutputProperty("indent", str);
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
            return stringWriter.toString();
        } catch (IllegalArgumentException | TransformerException e) {
            LOGGER.log(Level.SEVERE, "Transformer error", e);
            return xmlString;
        }
    }
}
