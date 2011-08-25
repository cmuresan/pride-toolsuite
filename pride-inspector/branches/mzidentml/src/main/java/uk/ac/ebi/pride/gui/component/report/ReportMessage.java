package uk.ac.ebi.pride.gui.component.report;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 25/05/11
 * Time: 12:28
 * To change this template use File | Settings | File Templates.
 */
public class ReportMessage {
    public static enum Type {SUCCESS, WARNING, ERROR, PLAIN}

    private Type type;
    private String message;
    private String description;

    public ReportMessage(Type type, String message, String description) {
        this.type = type;
        this.message = message;
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
