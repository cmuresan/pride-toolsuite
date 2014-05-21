package uk.ac.ebi.pride.pia.tools;


import uk.ac.ebi.pride.data.core.CvParam;
import uk.ac.ebi.pride.data.core.ParamGroup;
import uk.ac.ebi.pride.data.core.UserParam;

import java.util.List;

/**
 * Some additional functions for handling mzIdentML files.
 * 
 * @author yperez
 *
 */
public class MzIdentMLTools {
	
	/**
	 * We don't ever want to instantiate this class
	 */
	private MzIdentMLTools() {
		throw new AssertionError();
	}
	

	/**
	 * Compares the given Params for equality, i.e. all the Strings in it.
	 * Returns also true, if both params are null.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean paramsEqual(ParamGroup x, ParamGroup y) {
		if ((x == null) && (y == null)) {
			// both are null
			return true;
		}
		
		List<CvParam> cv1 = x.getCvParams();
		List<CvParam> cv2 = y.getCvParams();
		if ((cv1 != null) && (cv2 != null)) {
			// both are cvParams
			return cvParamsEqualOrNull(cv1, cv2);
		} else {
			List<UserParam> up1 = x.getUserParams();
			List<UserParam> up2 = y.getUserParams();
			
			return userParamsEqualOrNull(up1, up2);
		}
	}
	
	public static boolean getCvParamEqualsOrNull(CvParam x, CvParam y){

        if ((x != null) && (y != null)) {
            // both are not null
            boolean equal = true;

            // required
            equal &= x.getAccession().equals(y.getAccession());
            equal &= x.getCvLookupID().equals(y.getCvLookupID());
            equal &= x.getName().equals(y.getName());

            // optional
            equal &= PIATools.bothNullOrEqual(x.getCvLookupID(),
                    y.getCvLookupID());
            equal &= PIATools.bothNullOrEqual(x.getUnitAcc(),
                    y.getUnitAcc());
            equal &= PIATools.bothNullOrEqual(x.getUnitName(),
                    y.getUnitName());
            equal &= PIATools.bothNullOrEqual(x.getValue(),
                    y.getValue());

            return equal;
        } else {
            // both must be null then
            return (x == null) && (y == null);
        }

    }

    public static boolean getUserParamEqualsOrNull(UserParam x, UserParam y){
        if ((x != null) && (y != null)) {
            // both are not null
            boolean equal = true;

            // required
            equal &= x.getName().equals(y.getName());

            // optional
            equal &= PIATools.bothNullOrEqual(x.getType(),
                    y.getType());
            equal &= PIATools.bothNullOrEqual(x.getUnitAcc(),
                    y.getUnitAcc());
            equal &= PIATools.bothNullOrEqual(x.getUnitCVLookupID(),
                    y.getUnitCVLookupID());
            equal &= PIATools.bothNullOrEqual(x.getUnitName(),
                    y.getUnitName());
            equal &= PIATools.bothNullOrEqual(x.getValue(),
                    y.getValue());


            return equal;
        } else {
            // both must be null then
            return (x == null) && (y == null);
        }
    }

	/**
	 * Checks for the given cvParams, whether both are null are equal.
	 */
	public static boolean cvParamsEqualOrNull(List<CvParam> x, List<CvParam> y) {
        boolean equal = true;
        if (x.size() == y.size()) {
            boolean failed = false;

            for (CvParam param : x) {
                boolean found = false;

                // look for this param in the other list...
                for (CvParam checkParam : y) {
                    if (MzIdentMLTools.getCvParamEqualsOrNull(param,
                            checkParam)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    failed = true;
                    break;
                }
            }

            if (failed) {
                equal = false;
            }
        } else {
            // not the same size
            equal = false;
        }
        return equal;
	}
	
	
	/**
	 * Checks for the given userParams, whether both are null are equal.
	 */
	public static boolean userParamsEqualOrNull(List<UserParam> x, List<UserParam> y) {
        boolean equal = true;
        if (x.size() == y.size()) {
            boolean failed = false;

            for (UserParam param : x) {
                boolean found = false;

                // look for this param in the other list...
                for (UserParam checkParam : y) {
                    if (MzIdentMLTools.getUserParamEqualsOrNull(param,
                            checkParam)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    failed = true;
                    break;
                }
            }

            if (failed) {
                equal = false;
            }
        } else {
            // not the same size
            equal = false;
        }
        return equal;
	}
	
	
	/**
	 * Checks for the given fileFormats, whether both are null are equal.
	 */
	public static boolean fileFormatsEqualOrNull(ParamGroup x, ParamGroup y) {
		if ((x != null) && (y != null)) {
			// both are not null
			return MzIdentMLTools.cvParamsEqualOrNull(x.getCvParams(),
					y.getCvParams());
		} else {
			// both must be null to be equal
			return (x == null) && (y == null);
		}
	}

    public static boolean fileFormatsEqualOrNull(CvParam x, CvParam y){
        return getCvParamEqualsOrNull(x,y);
    }
	
	
	/**
	 * Checks for the given spectrumIDFormats, whether both are null are equal.
	 */
	public static boolean spectrumIDFormatEqualOrNull(ParamGroup x,
			ParamGroup y) {
		if ((x != null) && (y != null)) {
			// both are not null
			return MzIdentMLTools.cvParamsEqualOrNull(x.getCvParams(),
					y.getCvParams());
		} else {
			// both must be null to be equal
			return (x == null) && (y == null);
		}
	}

}
