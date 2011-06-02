package uk.ac.ebi.pride.gui.search;

/**
 * Criterias can be specified with search terms
 *
 * User: rwang
 * Date: 31/05/11
 * Time: 16:07
 */
public enum Criteria {
    CONTAIN ("Contains"),
    EQUAL ("Equals"),
    START_WITH ("Starts with"),
    END_WITH ("Ends with"),
    MORE_THAN ("More than"),
    LESS_THAN ("Less than"),
    EQUAL_OR_MORE_THAN ("Equal or more than"),
    EQUAL_OR_LESS_THAN ("Equal or less than"),
    EMPTY ("Empty");

    private String term;

    private Criteria(String term) {
        this.term = term;
    }

    public String getTerm() {
        return term;
    }
}
