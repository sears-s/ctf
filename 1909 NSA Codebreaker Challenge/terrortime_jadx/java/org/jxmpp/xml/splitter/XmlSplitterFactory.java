package org.jxmpp.xml.splitter;

public interface XmlSplitterFactory {
    XmlSplitter createXmlSplitter(CompleteElementCallback completeElementCallback, DeclarationCallback declarationCallback, ProcessingInstructionCallback processingInstructionCallback);
}
