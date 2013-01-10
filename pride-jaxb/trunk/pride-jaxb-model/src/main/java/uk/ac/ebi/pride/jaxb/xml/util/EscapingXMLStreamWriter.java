package uk.ac.ebi.pride.jaxb.xml.util;

import it.unimi.dsi.fastutil.chars.CharOpenHashSet;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Delegating {@link XMLStreamWriter} that filters out UTF-8 characters that
 * are illegal in XML.
 *
 * See forum post: http://glassfish.10926.n7.nabble.com/Escaping-illegal-characters-during-marshalling-td59751.html#a20090044
 *
 * @author Erik van Zijst
 */
public class EscapingXMLStreamWriter implements XMLStreamWriter {

    private final XMLStreamWriter writer;
    public static final char substitute = '\uFFFD';
    private static final CharOpenHashSet illegalChars;

    static {
        /**
         // excluded control characters
        \u0000 Null character
        \u0001 Start of header
        \u0002 Start of text
        \u0003 End of text
        \u0004 End of transmission
        \u0005 Enquiry
        \u0006 Positive acknowledge
        \u0007 Alert (bell)
        \u0008 Backspace
        \u000B Vertical tab
        \u000C Form feed
        \u000E Shift out
        \u000F Shift in
        \u0010 Data link escape
        \u0011 Device control 1 (XON)
        \u0012 Device control 2 (tape on)
        \u0013 Device control 3 (XOFF)
        \u0014 Device control 4 (tape off)
        \u0015 Negative acknowledgement
        \u0016 Synchronous idle
        \u0017 End of transmission block
        \u0018 Cancel
        \u0019 End of medium
        \u001A Substitute
        \u001B Escape
        \u001C File separator (Form separator)
        \u001D Group separator
        \u001E Record separator
        \u001F Unit separator

        // not excluded control characters
        \u0009 Horizontal tab
        \u000A Line feed
        \u000D Carriage return

         */

        final String escapeString = "\u0000\u0001\u0002\u0003\u0004\u0005" +
            "\u0006\u0007\u0008\u000B\u000C\u000E\u000F\u0010\u0011\u0012" +
            "\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C" +
            "\u001D\u001E\u001F\uFFFE\uFFFF";

        illegalChars = new CharOpenHashSet();
        for (int i = 0; i < escapeString.length(); i++) {
            illegalChars.add(escapeString.charAt(i));
        }
    }

    public EscapingXMLStreamWriter(XMLStreamWriter writer) {

        if (null == writer) {
            throw new IllegalArgumentException("null");
        } else {
            this.writer = writer;
        }
    }

    private boolean isIllegal(char c) {
        return illegalChars.contains(c);
    }

    /**
     * Substitutes all illegal characters in the given string by the value of
     * {@link EscapingXMLStreamWriter#substitute}. If no illegal characters
     * were found, no copy is made and the given string is returned.
     *
     * @param string
     * @return
     */
    private String escapeCharacters(String string) {

        char[] copy = null;
        boolean copied = false;
        for (int i = 0; i < string.length(); i++) {
            if (isIllegal(string.charAt(i))) {
                if (!copied) {
                    copy = string.toCharArray();
                    copied = true;
                }
                copy[i] = substitute;
            }
        }
        return copied ? new String(copy) : string;
    }

    public void writeStartElement(String s) throws XMLStreamException {
        writer.writeStartElement(s);
    }

    public void writeStartElement(String s, String s1) throws XMLStreamException {
        writer.writeStartElement(s, s1);
    }

    public void writeStartElement(String s, String s1, String s2)
        throws XMLStreamException {
        writer.writeStartElement(s, s1, s2);
    }

    public void writeEmptyElement(String s, String s1) throws XMLStreamException {
        writer.writeEmptyElement(s, s1);
    }

    public void writeEmptyElement(String s, String s1, String s2)
        throws XMLStreamException {
        writer.writeEmptyElement(s, s1, s2);
    }

    public void writeEmptyElement(String s) throws XMLStreamException {
        writer.writeEmptyElement(s);
    }

    public void writeEndElement() throws XMLStreamException {
        writer.writeEndElement();
    }

    public void writeEndDocument() throws XMLStreamException {
        writer.writeEndDocument();
    }

    public void close() throws XMLStreamException {
        writer.close();
    }

    public void flush() throws XMLStreamException {
        writer.flush();
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        writer.writeAttribute(localName, escapeCharacters(value));
    }

    public void writeAttribute(String prefix, String namespaceUri, String localName, String value)
        throws XMLStreamException {
        writer.writeAttribute(prefix, namespaceUri, localName, escapeCharacters(value));
    }

    public void writeAttribute(String namespaceUri, String localName, String value)
        throws XMLStreamException {
        writer.writeAttribute(namespaceUri, localName, escapeCharacters(value));
    }

    public void writeNamespace(String s, String s1) throws XMLStreamException {
        writer.writeNamespace(s, s1);
    }

    public void writeDefaultNamespace(String s) throws XMLStreamException {
        writer.writeDefaultNamespace(s);
    }

    public void writeComment(String s) throws XMLStreamException {
        writer.writeComment(s);
    }

    public void writeProcessingInstruction(String s) throws XMLStreamException {
        writer.writeProcessingInstruction(s);
    }

    public void writeProcessingInstruction(String s, String s1)
        throws XMLStreamException {
        writer.writeProcessingInstruction(s, s1);
    }

    public void writeCData(String s) throws XMLStreamException {
        writer.writeCData(escapeCharacters(s));
    }

    public void writeDTD(String s) throws XMLStreamException {
        writer.writeDTD(s);
    }

    public void writeEntityRef(String s) throws XMLStreamException {
        writer.writeEntityRef(s);
    }

    public void writeStartDocument() throws XMLStreamException {
        writer.writeStartDocument();
    }

    public void writeStartDocument(String s) throws XMLStreamException {
        writer.writeStartDocument(s);
    }

    public void writeStartDocument(String s, String s1)
        throws XMLStreamException {
        writer.writeStartDocument(s, s1);
    }

    public void writeCharacters(String s) throws XMLStreamException {
        writer.writeCharacters(escapeCharacters(s));
    }

    public void writeCharacters(char[] chars, int start, int len)
        throws XMLStreamException {
        writer.writeCharacters(escapeCharacters(new String(chars, start, len)));
    }

    public String getPrefix(String s) throws XMLStreamException {
        return writer.getPrefix(s);
    }

    public void setPrefix(String s, String s1) throws XMLStreamException {
        writer.setPrefix(s, s1);
    }

    public void setDefaultNamespace(String s) throws XMLStreamException {
        writer.setDefaultNamespace(s);
    }

    public void setNamespaceContext(NamespaceContext namespaceContext)
        throws XMLStreamException {
        writer.setNamespaceContext(namespaceContext);
    }

    public NamespaceContext getNamespaceContext() {
        return writer.getNamespaceContext();
    }

    public Object getProperty(String s) throws IllegalArgumentException {
        return writer.getProperty(s);
    }
}