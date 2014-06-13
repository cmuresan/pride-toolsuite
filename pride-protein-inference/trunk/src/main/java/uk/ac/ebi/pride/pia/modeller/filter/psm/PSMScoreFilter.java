package uk.ac.ebi.pride.pia.modeller.filter.psm;

import uk.ac.ebi.pride.pia.intermediate.IntermediatePeptideSpectrumMatch;
import uk.ac.ebi.pride.pia.modeller.filter.AbstractFilter;
import uk.ac.ebi.pride.pia.modeller.filter.FilterComparator;
import uk.ac.ebi.pride.pia.modeller.filter.FilterType;
import uk.ac.ebi.pride.pia.modeller.protein.scores.ScoreUtilities;
import uk.ac.ebi.pride.term.CvTermReference;


/**
 * Filters for a score on PSM level.
 * 
 * @author julian
 *
 */
public class PSMScoreFilter extends AbstractFilter {
	
	protected final String shortName;
	
	protected final String name;
	
	protected final String filteringName;
	
	public static final FilterType filterType = FilterType.numerical;
	
	private Double value;
	
	public static String prefix = "psm_score_filter_";
	
	private String cvAccession;
	
	
	public PSMScoreFilter(FilterComparator arg, Double value, boolean negate,
			String scoreAccession, boolean oboLookup) {
		cvAccession = null;
		
		CvTermReference cvTerm = CvTermReference.getCvRefByAccession(scoreAccession);
		if (cvTerm == null) {
			// try with "MS" enhenced name
			cvTerm = CvTermReference.getCvRefByAccession("MS:" + scoreAccession);
		}
		if (cvTerm != null) {
			cvAccession = cvTerm.getAccession();
		} else if (oboLookup) {
			cvAccession = ScoreUtilities.findAccessionInObo(scoreAccession);
		}
		
		if (cvAccession != null) {
			this.comparator = arg;
			this.value = value;
			this.negate = negate;
			
			this.name = cvTerm.getName() + " Filter for PSM";
			this.filteringName = cvTerm.getName() + " (PSM)";
			this.shortName = prefix + cvTerm.getAccession();
		} else {
			this.comparator = null;
			this.value = null;
			
			this.name = null;
			this.filteringName = null;
			this.shortName = null;
		}
	}
	
	
	@Override
	public String getShortName() {
		return shortName;
	}
	
	
	@Override
	public String getName() {
		return name;
	}
	
	
	@Override
	public String getFilteringName() {
		return filteringName;
	}
	
	
	@Override
	public Object getFilterValue() {
		return value;
	}
	
	
	@Override
	public FilterType getFilterType() {
		return filterType;
	}
	
	
	@Override
	public Object getObjectsValue(Object o) {
		if (o instanceof IntermediatePeptideSpectrumMatch) {
			return ((IntermediatePeptideSpectrumMatch) o).getScore(cvAccession);
		} else if (o instanceof Number) {
			return ((Number) o).doubleValue();
		} else {
			return null;
		}
	}
	
	
	@Override
	public boolean supportsClass(Object c) {
		// only support SpectrumIdentification and Number
		if (c instanceof IntermediatePeptideSpectrumMatch) {
			return true;
		} else if (c instanceof Number) {
			return true;
		}
		
		return false;
	}

}
