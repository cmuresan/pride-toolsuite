package uk.ac.ebi.pride.gui.url;

import uk.ac.ebi.pride.gui.utils.ProteinAccessionPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generate hyperlink based a given protein accession.
 * <p/>
 * User: rwang
 * Date: 10-Sep-2010
 * Time: 15:41:00
 */
public class ProteinAccHyperLinkGenerator implements HyperLinkGenerator<String> {

    @Override
    public String generate(String value) {
        String url = null;
        if (value != null) {
            // iterate over id patterns
            for (ProteinAccessionPattern p : ProteinAccessionPattern.values()) {
                Pattern idPattern = p.getIdPattern();
                Matcher m = idPattern.matcher(value);
                if (m.matches()) {
                    Object[] args = {value};
                    url = p.getUrlPattern().format(args);
                    break;
                }
            }
        }
        return url;
    }
}
