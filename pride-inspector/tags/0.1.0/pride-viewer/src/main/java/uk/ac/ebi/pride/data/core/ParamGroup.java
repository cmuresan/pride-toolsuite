package uk.ac.ebi.pride.data.core;

import java.util.Collection;
import java.util.List;

/**
 * ToDo: thread safe
 * ToDo: PropertyChange notification.
 * User: rwang
 * Date: 25-Jan-2010
 * Time: 15:58:01
 */
public class ParamGroup {

    // ToDo: merge these two collection into one ?
    private List<CvParam> cvParams = null;
    private List<UserParam> userParams = null;

    public ParamGroup(ParamGroup params) {
        if (params !=  null) {
            this.cvParams = params.getCvParams();
            this.userParams = params.getUserParams();
        }
    }

    public ParamGroup(List<CvParam> cvParams,
                      List<UserParam> userParams) {
        this.cvParams = cvParams;
        this.userParams = userParams;
    }

    // cv params
    public List<CvParam> getCvParams() {
        return cvParams;
    }

    public void setCvParams(List<CvParam> cvs) {
        cvParams = cvs;
    }

    public void addCvParam(CvParam cv) {
        cvParams.add(cv);
    }

    public void removeCvParam(CvParam cv) {
        cvParams.remove(cv);
    }

    // user params
    public List<UserParam> getUserParams() {
        return userParams;
    }

    public void setUserParams(List<UserParam> ups) {
        userParams = ups;
    }

    public void addUserParam(UserParam up) {
        userParams.add(up);
    }

    public void removeUserParam(UserParam up) {
        userParams.remove(up);
    }
}
