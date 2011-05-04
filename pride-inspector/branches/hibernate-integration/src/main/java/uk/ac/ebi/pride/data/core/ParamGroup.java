package uk.ac.ebi.pride.data.core;

import java.util.ArrayList;
import java.util.List;

/**
 * ParamGroup is a container for CvParams and UserParams.
 * This class is included in neither mzML nor PRIDE xml definition.
 * In theory, both cv params and user params can be null.
 *
 * User: rwang
 * Date: 25-Jan-2010
 * Time: 15:58:01
 */
public class ParamGroup implements MassSpecObject {

    /** List of cv paramemters */
    private List<CvParam> cvParams = null;
    /** List of user parameters */
    private List<UserParam> userParams = null;

    /**
     * Constructor creates an empty param group
     */
    public ParamGroup() {
        this.cvParams = new ArrayList<CvParam>();
        this.userParams = new ArrayList<UserParam>();
    }

    /**
     * Constructor
     * @param params    optional.
     */
    public ParamGroup(ParamGroup params) {
        if (params != null) {
            setCvParams(params.getCvParams());
            setUserParams(params.getUserParams());
        }
    }

    /**
     * Constructor
     * @param cvParams  optional.
     * @param userParams    optional.
     */
    public ParamGroup(List<CvParam> cvParams,
                      List<UserParam> userParams) {
        setCvParams(cvParams);
        setUserParams(userParams);
    }

    public List<CvParam> getCvParams() {
        return cvParams == null ? null : new ArrayList<CvParam>(cvParams);
    }

    public void setCvParams(List<CvParam> cvs) {
        cvParams = cvs;
    }

    public void addCvParam(CvParam cv) {
        if (cv == null) {
            throw new IllegalArgumentException("Can not add NULL Cv parameters");
        } else{
            if (cvParams == null) {
                cvParams = new ArrayList<CvParam>();
            }
            cvParams.add(cv);
        }
    }

    public void addCvParams(List<CvParam> cvs) {
        if (cvs == null) {
            throw new IllegalArgumentException("Can not add NULL CV parameters");
        } else {
            // Note: didn't use addAll() from List, because List can
            // contain NULL elements.
            for(CvParam cv : cvs) {
                addCvParam(cv);
            }
        }
    }

    public void removeCvParam(CvParam cv) {
        cvParams.remove(cv);
    }

    public void removeCvParams(List<CvParam> cvs) {
        cvParams.removeAll(cvs);
    }

    public List<UserParam> getUserParams() {
        return userParams == null ? null : new ArrayList<UserParam>(userParams);
    }

    public void setUserParams(List<UserParam> ups) {
        userParams = ups;
    }

    public void addUserParam(UserParam up) {
        if (up == null) {
            throw new IllegalArgumentException("Can not add NULL user parameters");
        } else{
            if (userParams == null) {
                userParams = new ArrayList<UserParam>();
            }
            userParams.add(up);
        }
    }

    public void addUserParams(List<UserParam> ups) {
        if (ups == null) {
            throw new IllegalArgumentException("Can not add NULL user parameters");
        } else {
            // Note: didn't use addAll() from List, because List can
            // contain NULL elements.
            for(UserParam up : ups) {
                addUserParam(up);
            }
        }
    }

    public void removeUserParam(UserParam up) {
        userParams.remove(up);
    }

    public void removeUserParams(List<UserParam> ups) {
        userParams.removeAll(ups);
    }

    /**
     * Get cv param by accession number and cv label.
     *
     * @param cvLabel   cv label.
     * @param accession cv accession.
     * @return CvParam  cv param.
     */
    public CvParam getCvParam(String cvLabel, String accession) {
        CvParam cvParam = null;
        if (cvParams != null) {
            for (CvParam param : cvParams) {
                if (cvParam.getAccession().equals(accession) || cvParam.getCvLookupID().equals(cvLabel)) {
                    cvParam = param;
                }
            }
        }
        return cvParam;
    }
}
