package adpredictor.util;

import adpredictor.Feature;
import adpredictor.Gaussian;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class Utils {
    private static final double MAX_ABS_SURPRISE = 5.0;
    
    public static Feature[] createFeatures(int numFeatures) {
        Feature[] features = new Feature[numFeatures];
        features[0] = Utils.biasFeature();
        
        for (int i=1; i<numFeatures; i++)
            features[i] = new Feature();
        return features;
    }
    
    public static Feature[] createFeatures(int numFeatures, Feature last) {
        Feature[] features = new Feature[numFeatures];
        features[0] = Utils.biasFeature();
        
        for (int i=1; i<numFeatures-1; i++)
            features[i] = new Feature();
        
        features[features.length-1] = last;
        return features;
    }
    
    /**
     * The prior weight on the bias such that on initialization of a model with 
     * the given parameters, P(y | x, initial_weights) = prior.
     * @param priorProbability
     * @param beta
     * @param numFeatures
     * @return 
     */
    public static Gaussian priorBiasWeight(double priorProbability, double beta, int numFeatures) {
        double biasMean = NormalDistribution.PPF(priorProbability, 0, 1) * (Math.pow(beta, 2) + numFeatures);
        return new Gaussian(biasMean, 1.0);
    }
    
    public static Feature biasFeature() {
        return new Feature(0, 0);
    }
    
    /**
     * The global prior on non-bias weights
     * @return 
     */
    public static Gaussian priorWeight() {
        return new Gaussian(0.0, 1.0);
    }
    
    public static double labelToDouble(boolean label) {
        return (label == true) ? 1.0 : -1.0;
    }
    
    /**
     * Returns the additive and multiplicative corrections for the mean and 
     * variance of a trunctated Gaussian random variable.
     * 
     * In Trueskill/AdPredictor papers, denoted
     * - V(t)
     * - W(t) = V(t) * (V(t) + t)
     * 
     * Returns (v(t), w(t))
     * @param t
     * @return 
     */
    public static double[] gaussianCorrections(double t) {
        if (t < -MAX_ABS_SURPRISE)
            t = -MAX_ABS_SURPRISE;
        if (t > MAX_ABS_SURPRISE)
            t = MAX_ABS_SURPRISE;
        
        double v = NormalDistribution.PDF(t, 0, 1) / NormalDistribution.CDF(t, 0, 1);
        double w = v * (v + t);
        return new double[]{v, w};
    }
    
    /**
     * Computes the Kullback-Liebler divergence between two Bernoulli random 
     * variables with probability p and q. Algebraically, KL(p || q)
     * @param p
     * @param q 
     * @return  
     */
    public static double klDivergence(double p, double q) {
        return p * Math.log(p / q) + (1.0 - p) * Math.log((1.0 - p) / (1.0 - q));
    }
    
    public static double[] linspace(double start, double stop, int n) {
        double[] result = new double[n];
        double step = (stop-start)/(n-1);

        for(int i = 0; i <= n-2; i++)
            result[i] = start + (i * step);
        result[n-1] = stop;

        return result;
    }
}
