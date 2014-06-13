package uk.ac.ebi.pride.pia.modeller.fdr;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * This class calculates the FDR on a given, sorted list of
 * {@link FDRComputableByDecoys}s.
 * 
 * @author julian
 *
 */
public class FDRCalculator {
	
	
	/**
	 * Calculate the FDR on the given score sorted List of
	 * {@link FDRComputableByDecoys} objects.
	 * 
	 * The item with the best score must be the first in the list, the worst
	 * score the last.
	 * 
	 * @param reportItems
	 */
	public static <T extends FDRComputableByDecoys> void calculateFDR(List<T> items, String scoreAccession) {
		double fdr;
		
		Double rankScore = Double.NaN;
		List<T> rankItems = new ArrayList<T>();
		
		int nrTargets = 0;
		int nrDecoys = 0;
		
		for (T item : items) {
			if (!rankScore.equals(item.getScore(scoreAccession))) {
				// this is a new rank, calculate FDR
				if (!rankScore.equals(Double.NaN) && (nrTargets < 1)) {
					// only decoys until now -> set FDR to infinity
					fdr = Double.POSITIVE_INFINITY;
				} else {
					fdr = (double)nrDecoys / nrTargets;
				}
				
				for (T rankItem : rankItems) {
					rankItem.setFDR(fdr);
				}
				
				rankScore = item.getScore(scoreAccession);
				rankItems.clear();
			}
			
			// check for decoy
			if (item.getIsDecoy()) {
				nrDecoys++;
			} else {
				nrTargets++;
			}
			
			rankItems.add(item);
		}
		
		// calculate the last rank
		if (nrTargets < 1) {
			// only decoys until now -> set FDR to infinity
			fdr = Double.POSITIVE_INFINITY;
		} else {
			fdr = (double)nrDecoys / nrTargets;
		}
		
		for (T rankItem : rankItems) {
			rankItem.setFDR(fdr);
		}
		
		
		// at last calculate the q-values
		// for this, iterate backwards through the list
		ListIterator<T> it  = items.listIterator(items.size());
		Double qValue = Double.NaN;
		
		while (it.hasPrevious()) {
			T item = it.previous();
			
			if ((qValue.compareTo(Double.NaN) == 0) ||
					(item.getFDR() < qValue)) {
				qValue = item.getFDR();
			}
			
			item.setQValue(qValue);
		}
	}
}
