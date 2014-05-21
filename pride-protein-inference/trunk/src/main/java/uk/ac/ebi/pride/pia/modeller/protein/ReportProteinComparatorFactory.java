package uk.ac.ebi.pride.pia.modeller.protein;

import uk.ac.ebi.pride.data.core.Protein;
import uk.ac.ebi.pride.pia.modeller.report.SortOrder;
import uk.ac.ebi.pride.pia.modeller.score.comparator.RankComparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class ReportProteinComparatorFactory {
	
	/**
	 * 
	 * @author julian
	 *
	 */
	public enum CompareType {
		/**
		 * sort the report proteins by their rank
		 */
		RANK_SORT {
			@Override
			public Comparator<Protein> getNewInstance() {
				//return new RankComparator<Protein>();
                //Todo: It would be great to know what is a Rank for a Protein, because this is not an information provided in the file - For Julian
                return null;
			}
			
			@Override
			public String toString() {
				return "rank";
			}
		},
		
		/**
		 * sort the report proteins by their number of spectra
		 */
		NR_SPECTRA_SORT {
			@Override
			public Comparator<Protein> getNewInstance() {
				return new Comparator<Protein>() {
					@Override
					public int compare(Protein o1, Protein o2) {
                        //Todo: Implement this at Protein level also: Yasset
						//return o1.getNrSpectra().compareTo(o2.getNrSpectra());
                        return 1;
					}
				};
			}
			
			@Override
			public String toString() {
				return "nr_spectra";
			}
		},
		/**
		 * sort the report proteins by their number of PSMs
		 */
		NR_PSMS_SORT {
			@Override
			public Comparator<Protein> getNewInstance() {
				return new Comparator<Protein>() {
					@Override
					public int compare(Protein o1, Protein o2) {
                        //Todo: Implement this at Protein level also: Yasset
						//return o1.getNrPSMs().compareTo(o2.getNrPSMs());
                        return 1;
					}
				};
			}
			
			@Override
			public String toString() {
				return "nr_psms";
			}
		},
		/**
		 * sort the report proteins by their number of PSMs
		 */
		NR_PEPTIDES_SORT {
			@Override
			public Comparator<Protein> getNewInstance() {
				return new Comparator<Protein>() {
					@Override
					public int compare(Protein o1, Protein o2) {
						// Todo: Implement this a Protein level also: Yasset
                        //return o1.getNrPeptides().compareTo(o2.getNrPeptides());
                        return 1;
					}
				};
			}
			
			@Override
			public String toString() {
				return "nr_peptides";
			}
		},
		
		/**
		 * sort by the score with a given name, interpreting a higher score as better
		 */
		SCORE_SORT {
			@Override
			public Comparator<Protein> getNewInstance() {
				return new Comparator<Protein>() {
					@Override
					public int compare(Protein o1, Protein o2) {
						if (o1.getScore().equals(Double.NaN) &&
								o2.getScore().equals(Double.NaN)) {
							return 0;
						} else if (o1.getScore().equals(Double.NaN)) {
							return 1;
						} else if (o2.getScore().equals(Double.NaN)) {
							return -1;
						}
						// Todo: Implement this in ms-data-core-api Rigth now the scores in ms-data-core-api are store using
						//return o1.getScore().compareTo(o2.getScore());
                        return 1;
					}
				};
			}
			
			@Override
			public String toString() {
				return "protein_score";
			}
		},
		
		/**
		 * sort by the score with a given name, interpreting a lower score as better
		 */
		SCORE_SORT_HIGHERSCOREBETTER {
			@Override
			public Comparator<Protein> getNewInstance() {
				return new Comparator<Protein>() {
					@Override
					public int compare(Protein o1, Protein o2) {
						if (o1.getScore().equals(Double.NaN) &&
								o2.getScore().equals(Double.NaN)) {
							return 0;
						} else if (o1.getScore().equals(Double.NaN)) {
							return 1;
						} else if (o2.getScore().equals(Double.NaN)) {
							return -1;
						}
                        // Todo: Implement this in ms-data-core-api Rigth now the scores in ms-data-core-api are store using
						//return -o1.getScore().compareTo(o2.getScore());
                        return 1;
					}
				};
			}
			
			@Override
			public String toString() {
				return "protein_score_lowerscorebetter";
			}
		},
		;
		
		
		/**
		 * Returns a new instance of the given type.
		 * 
		 * @return
		 */
		public abstract Comparator<Protein> getNewInstance();
		
	}
	
	
	/**
	 * invert the ordering
	 * 
	 * @param other
	 * @return
	 */
	public static Comparator<Protein> descending(final Comparator<Protein> other) {
		return new Comparator<Protein>() {
			@Override
			public int compare(Protein o1, Protein o2) {
				return -1 * other.compare(o1, o2);
			}
		};
    }
	
	
	/**
	 * returns a Comparator for multiple options.
	 * 
	 * @param multipleOptions
	 * @return
	 */
	public static Comparator<Protein> getComparator(final List<Comparator<Protein>> multipleOptions) {
		return new Comparator<Protein>() {
			public int compare(Protein o1, Protein o2) {
				int result;
				// check all options, the first not returning 0 (equal) gets returned
				for (Comparator<Protein> option : multipleOptions) {
					result = option.compare(o1, o2);
					if (result != 0) {
						return result;
					}
				}
				return 0;
			}
		};
	}
	
	
	/**
	 * returns the comparator given by its name using the given order.
	 * 
	 * @param name
	 * @param order
	 * @return
	 */
	public static Comparator<Protein> getComparatorByName(String name, SortOrder order) {
		
		for (ReportProteinComparatorFactory.CompareType comp : CompareType.values()) {
			if (name.equals(comp.toString())) {
				return (order.equals(SortOrder.ascending)) ? 
						comp.getNewInstance() :
						descending(comp.getNewInstance());
			}
		}
		
		return null;
	}
	
	
	/**
	 * returns a mapping from the description strings of all the available
	 * sortings to SortOrder.unsorted, except for the scores
	 * @return
	 */
	public static Map<String, SortOrder> getInitialSortOrders() {
		Map<String, SortOrder> orders = new HashMap<String, SortOrder>();
		
		for (ReportProteinComparatorFactory.CompareType comp : CompareType.values()) {
			orders.put(comp.toString(), SortOrder.unsorted);
		}
		
		return orders;
	}
}
