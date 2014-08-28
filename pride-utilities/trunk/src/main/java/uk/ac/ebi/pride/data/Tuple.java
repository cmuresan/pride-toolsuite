package uk.ac.ebi.pride.data;


import java.io.Serializable;

/**
 * Tuple stores two elements.
 *
 * User: rwang
 * Date: 08-Sep-2010
 * Time: 11:17:04
 */
public class Tuple <K, V> implements Serializable {
    private K key;
    private V value;

    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;

        Tuple tuple = (Tuple) o;

        if (key != null ? !key.equals(tuple.key) : tuple.key != null) return false;
        return !(value != null ? !value.equals(tuple.value) : tuple.value != null);

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}