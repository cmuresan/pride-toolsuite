package uk.ac.ebi.pride.url;

import uk.ac.ebi.pride.gui.url.HttpUtilities;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class ReactomeUrlTest {

    public static final String REACTOME_HTML = "file:///Code/JavaScript/form/formSubmit3.html";

    public static void main(String[] args) throws Exception {
        HttpUtilities.openURL(REACTOME_HTML);
    }
}
