package uk.ac.ebi.pride.data.io.db;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import uk.ac.ebi.pride.data.core.CvParam;
import uk.ac.ebi.pride.data.core.ParamGroup;
import uk.ac.ebi.pride.data.core.UserParam;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Rui Wang
 * @version $Id$
 */
public class PeptideParamResultSetExtractor implements ResultSetExtractor<Map<Comparable, ParamGroup>>{
    @Override
    public Map<Comparable, ParamGroup> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Comparable, ParamGroup> results = new HashMap<Comparable, ParamGroup>();

        while(rs.next()) {
            Comparable peptideId = rs.getString("parent_element_fk");
            // get or create param list
            ParamGroup paramGroup = results.get(peptideId);
            if (paramGroup == null) {
                paramGroup = new ParamGroup();
                results.put(peptideId, paramGroup);
            }
            // store parameters
            String cvLabel = rs.getString("cv_label");
            String name = rs.getString("name");
            String accession = rs.getString("accession");
            String value = rs.getString("value");
            if (cvLabel == null) {
                // user param
                UserParam newParam = new UserParam(name, accession, value, null, null, null);
                paramGroup.addUserParam(newParam);
            } else {
                // cv param
                CvParam newParam = new CvParam(accession, name, cvLabel, value, null, null, null);
                paramGroup.addCvParam(newParam);
            }
        }

        return results;
    }
}
