package adpredictor;

/**
 *
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class Gaussian {
    public double mean = 1;
    public double variance = 2;

    public Gaussian(double mean, double variance) {
        this.mean = mean;
        this.variance = variance;
    }

    @Override
    public String toString() {
        return "Gaussian{" + "mean=" + mean + ", variance=" + variance + '}';
    }
}
