package adpredictor;

/**
 *
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class Feature {
    public long feature = 1;
    public long value = 2;
    
    public Feature() {
    }

    public Feature(long feature, long value) {
        this.feature = feature;
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (int) (this.feature ^ (this.feature >>> 32));
        hash = 37 * hash + (int) (this.value ^ (this.value >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Feature other = (Feature) obj;
        if (this.feature != other.feature) {
            return false;
        }
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Feature{" + "feature=" + feature + ", value=" + value + '}';
    }

}
