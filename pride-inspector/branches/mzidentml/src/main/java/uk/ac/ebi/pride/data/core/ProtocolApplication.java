package uk.ac.ebi.pride.data.core;

/**
 * ToDo: document this class
 * <p/>
 * User: yperez
 * Date: 05/08/11
 * Time: 15:28
 */
public class ProtocolApplication extends Identifiable {
    private String activeDate = null;

    public ProtocolApplication(String id, String name, String activeDate) {
        super(id, name);
        this.activeDate = activeDate;
    }

    public String getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(String activeDate) {
        this.activeDate = activeDate;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
