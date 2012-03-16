package uk.ac.ebi.pride.db;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideDBAccessControllerImpl;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import java.util.Collection;

/**
 * Test to check each accessions in PRIDE public database instance.
 * <p/>
 * Note: This is not a unit test
 * <p/>
 * User: rwang
 * Date: 13/12/10
 * Time: 10:57
 */
public class DBControllerBatchTest {

    public static void main(String[] args) throws DataAccessException {
        int start = -1;
        int stop = -1;

        if (args.length > 0) {
            start = Integer.parseInt(args[0]);
            if (args.length > 1) {
                stop = Integer.parseInt(args[1]);
            }
        }

            PrideDBAccessControllerImpl c = new PrideDBAccessControllerImpl();

            Collection<Comparable> expAccs = c.getExperimentAccs();
            c.close();

            int size = expAccs.size();
            for (int i = size - 1; i >= 0; i--) {
                Comparable expAcc = CollectionUtils.getElement(expAccs, i);
                try {
                    if ((start == -1 || Integer.parseInt(expAcc.toString()) >= start)
                            && (stop == -1 || Integer.parseInt(expAcc.toString()) <= stop)) {
                        System.out.println("PRIDE Experiment: " + expAcc);
                        PrideDBAccessControllerImpl controller = new PrideDBAccessControllerImpl(expAcc);

                        // read the meta data
                        controller.getExperimentMetaData();

                        // iterate over 50 spectra
                        if (controller.hasSpectrum()) {
                            Collection<Comparable> ids = controller.getSpectrumIds();
                            int cnt = 0;
                            for (Comparable id : ids) {
                                // read the spectrum object
                                controller.getSpectrumById(id);
                                cnt++;
                                if (cnt >= 50) {
                                    break;
                                }
                            }
                        }

                        // iterate over 50 chromatograms
                        if (controller.hasChromatogram()) {
                            Collection<Comparable> ids = controller.getChromatogramIds();
                            int cnt = 0;
                            for (Comparable id : ids) {
                                controller.getChromatogramById(id);
                                cnt++;
                                if (cnt >= 50) {
                                    break;
                                }
                            }
                        }

                        // iterate over 50 identifications
                        if (controller.hasIdentification()) {
                            Collection<Comparable> ids = controller.getIdentificationIds();
                            int cnt = 0;
                            for (Comparable id : ids) {
                                controller.getIdentificationById(id);
                                // read peptide details
                                Collection<Comparable> pepIds = controller.getPeptideIds(id);
                                for (Comparable pepId : pepIds) {
                                    controller.getPeptideByIndex(id, pepId);
                                }
                                cnt++;
                                if (cnt >= 50) {
                                    break;
                                }
                            }
                        }

                        // close the data access controller
                        controller.close();
                        System.out.println("-------------------------------------------------------------------------------");
                    }
                } catch (DataAccessException e) {
                    e.printStackTrace();
                    System.out.println("------------------------------------------------------------------------");
                }

            }
        }
}
