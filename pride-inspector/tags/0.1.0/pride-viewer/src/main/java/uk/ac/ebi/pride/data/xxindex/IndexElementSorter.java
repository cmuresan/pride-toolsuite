package uk.ac.ebi.pride.data.xxindex;

import psidev.psi.tools.xxindex.index.IndexElement;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 30-Mar-2010
 * Time: 16:44:08
 */
public class IndexElementSorter {

    /**
     * this method sort map of IndexElement by ascending order
     * @param map map of unsorted contents   
     * @return Map<String, IndexElement> return a new map of sorted contents
     */
    public static Map<String, IndexElement> sortMap(Map<String, IndexElement> map) {
        // sort
        List<Map.Entry<String, IndexElement>> entries = new LinkedList<Map.Entry<String, IndexElement>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, IndexElement>>() {

            @Override
            public int compare(Map.Entry<String, IndexElement> o1, Map.Entry<String, IndexElement> o2) {
                boolean c = o1.getValue().getStart() <= o2.getValue().getStart();
                return c ? 0 : 1;
            }
        });

        // create a newly sort map
        Map<String, IndexElement> result = new LinkedHashMap<String, IndexElement>();
        for(Map.Entry<String, IndexElement> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    /**
     * this method sort list of IndexElement by ascending order
     * @param list  list of unsorted IndexElement
     * @return  List<IndexElement>  list of sorted IndexElement
     */
    public static List<IndexElement> sortList(List<IndexElement> list) {
        // sort
        Collections.sort(list, new Comparator<IndexElement>() {

            @Override
            public int compare(IndexElement o1, IndexElement o2) {
                boolean c = o1.getStart() <= o2.getStart();
                return c ? 0 : 1;
            }
        });

        return list;
    }
}
