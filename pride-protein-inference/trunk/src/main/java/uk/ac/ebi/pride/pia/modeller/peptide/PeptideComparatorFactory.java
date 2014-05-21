package uk.ac.ebi.pride.pia.modeller.peptide;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.pia.modeller.report.SortOrder;
import uk.ac.ebi.pride.pia.modeller.score.ScoreModelEnum;
import uk.ac.ebi.pride.pia.modeller.score.comparator.RankComparator;
import uk.ac.ebi.pride.pia.modeller.score.comparator.ScoreComparator;


/**
 * Handles {@link Comparator}s for {@link Peptide}s.
 * 
 * @author julian
 *
 */
public class PeptideComparatorFactory {
	
	/**
	 * 
	 * @author julian
	 *
	 */
	private enum CompareType {
		/**
		 * sort the report peptides by their sequences
		 */
		SEQUENCE_SORT {
			@Override
			public Comparator<Peptide> getNewInstance() {
				return new Comparator<Peptide>() {
					@Override
					public int compare(Peptide o1, Peptide o2) {
						return o1.getSequence().compareTo(o2.getSequence());
					}
				};
			}

			@Override
			public Comparator<Peptide> getNewInstance(String value) {
				return null;
			}
			
			@Override
			public String toString() {
				return "sequence";
			}
		},
		/**
		 * sort the report peptides by their missed cleavages (only use on single
		 * file!)
		 */
		MISSED_SORT {
			@Override
			public Comparator<Peptide> getNewInstance() {
				return new Comparator<Peptide>() {
					@Override
					public int compare(Peptide o1, Peptide o2) {
                        //Todo: Implement the misscleavage calculation for Peptides - Yasset
						//return Integer.valueOf(o1.getPeptideEvidence().getPeptideSequence().get).compareTo(o2.getMissedCleavages());
                        return 1;
					}
				};
			}

			@Override
			public Comparator<Peptide> getNewInstance(String value) {
				return null;
			}
			
			@Override
			public String toString() {
				return "missed";
			}
		},
		/**
		 * sort the report peptides by their rank
		 */
		RANK_SORT {
			@Override
			public Comparator<Peptide> getNewInstance() {
                return new Comparator<Peptide>() {
                    @Override
                    public int compare(Peptide o1, Peptide o2) {
                        return Integer.valueOf(new Integer(o1.getSpectrumIdentification().getRank()).compareTo(o2.getSpectrumIdentification().getRank()));

                    }
                };
			}

			@Override
			public Comparator<Peptide> getNewInstance(String value) {
				return null;
			}
			
			@Override
			public String toString() {
				return "rank";
			}
		},
		/**
		 * sort the report peptides by their number of PSMs
		 */
		NR_PSMS_SORT {
			@Override
			public Comparator<Peptide> getNewInstance() {
				return new Comparator<Peptide>() {
					@Override
					public int compare(Peptide o1, Peptide o2) {
                        //Todo: Implement the NrPSMs for each Peptide.
						//return Integer.valueOf(o1.getNrPSMs()).compareTo(o2.getNrPSMs());
                        return 1;
					}
				};
			}

			@Override
			public Comparator<Peptide> getNewInstance(String value) {
				return null;
			}
			
			@Override
			public String toString() {
				return "nr_psms";
			}
		},
		/**
		 * sort the report peptides by their number of PSMs
		 */
		NR_SPECTRA_SORT {
			@Override
			public Comparator<Peptide> getNewInstance() {
				return new Comparator<Peptide>() {
					@Override
					public int compare(Peptide o1, Peptide o2) {
                        //Todo: Implement the NrSpectra for each Peptide - Yasset
						//return Integer.valueOf(o1.getNrSpectra()).compareTo(o2.getNrSpectra());
                        return 1;
					}
				};
			}

			@Override
			public Comparator<Peptide> getNewInstance(String value) {
				return null;
			}
			
			@Override
			public String toString() {
				return "nr_spectra";
			}
		},
		
		/**
		 * sort by the score with a given name
		 */
		SCORE_SORT {
			@Override
			public Comparator<Peptide> getNewInstance() {
				return null;
			}
			
			@Override
			public Comparator<Peptide> getNewInstance(final String scoreName) {
				//Todo: We need to refine this in order to use the Peptides whitout extend the ms-data-core-api
                //return new ScoreComparator<Peptide>(scoreName);
                return null;
			}
			
			@Override
			public String toString() {
				return "score_comparator";
			}
		},
		;
		
		
		/**
		 * Returns a new instance of the given type.
		 * 
		 * @return
		 */
		public abstract Comparator<Peptide> getNewInstance();
		
		
		/**
		 * Returns a new instance of the given type and uses the value for
		 * initialization.
		 *  
		 * @param value
		 * @return
		 */
		public abstract Comparator<Peptide> getNewInstance(final String value);
	}
	
	
	/** the prefix before all score tags */
	public final static String score_prefix = "score_";
	
	
	/**
	 * invert the ordering
	 * 
	 * @param other
	 * @return
	 */
	public static Comparator<Peptide> descending(final Comparator<Peptide> other) {
		return new Comparator<Peptide>() {
			@Override
			public int compare(Peptide o1, Peptide o2) {
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
	public static Comparator<Peptide> getComparator(final List<Comparator<Peptide>> multipleOptions) {
		return new Comparator<Peptide>() {
			public int compare(Peptide o1, Peptide o2) {
				int result;
				// check all options, the first not returning 0 (equal) gets returned
				for (Comparator<Peptide> option : multipleOptions) {
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
	public static Comparator<Peptide> getComparatorByName(String name, SortOrder order) {
		
		for (PeptideComparatorFactory.CompareType comp : CompareType.values()) {
			if (name.equals(comp.toString())) {
				return (order.equals(SortOrder.ascending)) ?
						comp.getNewInstance() :
						descending(comp.getNewInstance());
			}
		}
		
		// it still may be a score comparator
		if (name.startsWith(score_prefix)) {
			String scoreName = name.substring(6);
			Comparator<Peptide> comp = PeptideComparatorFactory.CompareType.SCORE_SORT.getNewInstance(scoreName);
			
			return (order.equals(SortOrder.ascending)) ?
					comp :
					descending(comp);
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
		
		for (PeptideComparatorFactory.CompareType comp : CompareType.values()) {
			if (!comp.toString().startsWith(score_prefix)) {
				orders.put(comp.toString(), SortOrder.unsorted);
			}
		}
		
		return orders;
	}
	
	
	/**
	 * Returns the sorting name name of the score with the given shortName.
	 * If no score with the given name exists, null is returned.
	 * @return
	 */
	public static String getScoreSortName(String shortName) {
		if (ScoreModelEnum.getName(shortName) != null) {
			return score_prefix + shortName;
		} else {
			return null;
		}
	}
}
