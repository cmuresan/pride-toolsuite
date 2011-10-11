package uk.ac.ebi.pride.data.core;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 05/08/11
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
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
