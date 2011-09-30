package uk.ac.ebi.pride.mzgraph.chart.data.annotation;


import uk.ac.ebi.pride.mol.NeutralLoss;
import uk.ac.ebi.pride.mol.ion.FragmentIonType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * IonAnnotationInfo contains all the meta data about the annotation.
 * <p/>
 * User: rwang
 * Date: 15-Jun-2010
 * Time: 09:07:06
 */
public class IonAnnotationInfo implements PeakAnnotationInfo {

    private final List<Item> items;

    public IonAnnotationInfo() {
        this.items = new ArrayList<Item>();
    }

    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Can't add null item to IonAnnotationInfo");
        } else {
            items.add(item);
        }
    }

    public Item addItem(int charge,
                        FragmentIonType type,
                        int location,
                        NeutralLoss neutralLoss) {
        Item item = null;
        // FragmentIonType should not be null
        if (type != null) {
            item = new Item(charge, type, location, neutralLoss);
            items.add(item);
        }
        return item;
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public int getNumberOfItems() {
        return items.size();
    }

    public Item getItem(int index) {
        Item item = null;
        if (index >= 0 && index < items.size()) {
            item = items.get(index);
        }
        return item;
    }

    public Iterator<Item> iterator() {
        return items.iterator();
    }

    public static class Item {
        /**
         * list is required for ambiguous annotations
         */
        private int charge;
        private FragmentIonType type;
        private int location;
        private NeutralLoss neutralLoss;

        public Item(int charge,
                    FragmentIonType type,
                    int location,
                    NeutralLoss neutralLoss) {
            this.charge = charge;
            this.type = type;
            this.location = location;
            this.neutralLoss = neutralLoss;
        }

        public int getCharge() {
            return charge;
        }

        public void setCharge(int charge) {
            this.charge = charge;
        }

        public FragmentIonType getType() {
            return type;
        }

        public void setType(FragmentIonType type) {
            this.type = type;
        }

        public int getLocation() {
            return location;
        }

        public void setLocation(int location) {
            this.location = location;
        }

        public NeutralLoss getNeutralLoss() {
            return neutralLoss;
        }

        public void setNeutralLoss(NeutralLoss neutralLoss) {
            this.neutralLoss = neutralLoss;
        }
    }
}
