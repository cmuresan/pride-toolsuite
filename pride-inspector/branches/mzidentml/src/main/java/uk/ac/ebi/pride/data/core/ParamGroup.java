package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

/**
 * ParamGroup is a container for CvParams and UserParams.
 * This class is included in neither mzML nor PRIDE xml definition.
 * In theory, both cv params and user params can be null.
 * This container is extended for some several classes that contain
 * cvParameters as information.
 * <p/>
 * User: rwang
 * Date: 25-Jan-2010
 * Time: 15:58:01
 */
public class ParamGroup implements MassSpecObject {

    /**
     * List of cv paramemters
     */
    private List<CvParam> cvParams = null;

    /**
     * List of user parameters
     */
    private List<UserParam> userParams = null;

    /**
     * Constructor creates an empty param group
     */
    public ParamGroup() {
        this.cvParams   = new ArrayList<CvParam>();
        this.userParams = new ArrayList<UserParam>();
    }

    /**
     * Constructor
     *
     * @param params optional.
     */
    public ParamGroup(ParamGroup params) {
        if (params != null) {
            this.cvParams   = params.getCvParams();
            this.userParams = params.getUserParams();
        }
    }

    /**
     * Constructor Using Single Entries
     *
     * @param cvParam
     * @param userParam
     */
    public ParamGroup(CvParam cvParam, UserParam userParam) {
        if (cvParam != null) {
            List<CvParam> cvParams = new ArrayList<CvParam>();

            cvParams.add(cvParam);
            this.cvParams = cvParams;
        }

        if (userParam != null) {
            List<UserParam> userParams = new ArrayList<UserParam>();

            userParams.add(userParam);
            this.userParams = userParams;
        }
    }

    /**
     * Constructor
     *
     * @param cvParams   optional.
     * @param userParams optional.
     */
    public ParamGroup(List<CvParam> cvParams, List<UserParam> userParams) {
        this.cvParams   = cvParams;
        this.userParams = userParams;
    }

    /**
     * This method return a List of CvParam, the result List in a new Instance
     * of the current List of CvParam
     *
     * @return List of CvParam
     */
    public List<CvParam> getCvParams() {
        return (cvParams == null)
               ? null
               : new ArrayList<CvParam>(cvParams);
    }

    public void setCvParams(List<CvParam> cvs) {
        cvParams = cvs;
    }

    public void addCvParam(CvParam cv) {
        if (cvParams == null) {
            cvParams = new ArrayList<CvParam>();
        }

        cvParams.add(cv);
    }

    public void addCvParams(List<CvParam> cvs) {

        // Note: didn't use addAll() from List, because List can
        // contain NULL elements.
        for (CvParam cv : cvs) {
            addCvParam(cv);
        }
    }

    public void removeCvParam(CvParam cv) {
        cvParams.remove(cv);
    }

    public void removeCvParams(List<CvParam> cvs) {
        cvParams.removeAll(cvs);
    }

    /**
     * This method return a List of CvParam, the result List in a new Instance
     * of the current List of CvParam
     *
     * @return
     */
    public List<UserParam> getUserParams() {
        return (userParams == null)
               ? null
               : new ArrayList<UserParam>(userParams);
    }

    public void setUserParams(List<UserParam> ups) {
        userParams = ups;
    }

    public void addUserParam(UserParam up) {
        if (userParams == null) {
            userParams = new ArrayList<UserParam>();
        }

        userParams.add(up);
    }

    public void addUserParams(List<UserParam> ups) {

        // Note: didn't use addAll() from List, because List can
        // contain NULL elements.
        for (UserParam up : ups) {
            addUserParam(up);
        }
    }

    public void removeUserParam(UserParam up) {
        userParams.remove(up);
    }

    public void removeUserParams(List<UserParam> ups) {
        userParams.removeAll(ups);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        ParamGroup that = (ParamGroup) o;

        if (!cvParams.equals(that.cvParams)) {
            return false;
        }

        if (!userParams.equals(that.userParams)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (cvParams != null)
                     ? cvParams.hashCode()
                     : 0;

        result = 31 * result + ((userParams != null)
                                ? userParams.hashCode()
                                : 0);

        return result;
    }
}



