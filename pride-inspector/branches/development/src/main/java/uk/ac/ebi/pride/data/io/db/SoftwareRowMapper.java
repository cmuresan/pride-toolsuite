package uk.ac.ebi.pride.data.io.db;

import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.pride.data.core.CvParam;
import uk.ac.ebi.pride.data.core.ParamGroup;
import uk.ac.ebi.pride.data.core.Software;
import uk.ac.ebi.pride.data.core.UserParam;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Map a row from database ResultSet to a Software object
 * This class is used with Spring JDBC template
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SoftwareRowMapper implements RowMapper<Software> {
    private final static String COMMENTS = "comments";
    private final static String COMPLETION_TIME = "completion time";

    @Override
    public Software mapRow(ResultSet rs, int rowNum) throws SQLException {
        List<CvParam> cvParams = new ArrayList<CvParam>();
        //ToDo: semantic support, need to add a child term of MS:1000531 (software)
        List<UserParam> userParams = new ArrayList<UserParam>();
        userParams.add(new UserParam(COMMENTS, null, rs.getString("software_comments"), null, null, null));
        String completionTime = rs.getString("software_completion_time");
        if (completionTime != null) {
            userParams.add(new UserParam(COMPLETION_TIME, null, completionTime, null, null, null));
        }
        return new Software(rs.getString("software_name"), rs.getString("software_version"), new ParamGroup(cvParams, userParams));
    }
}
