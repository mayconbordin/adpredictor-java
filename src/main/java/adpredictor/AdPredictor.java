package adpredictor;

import adpredictor.util.Utils;
import adpredictor.util.NormalDistribution;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Implementation of Microsoft's AdPredictor algorithm.
 * 
 * Translated from: https://github.com/ajtulloch/adpredictor
 * Original author: Andrew Tulloch
 * 
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class AdPredictor {
    private static final Logger LOG = Logger.getLogger(AdPredictor.class);
    
    private double beta = 0.05;
    private double priorProbability = 0.5;
    private double epsilon = 0.05;
    private int numFeatures = 8;
    private Map<Feature, Gaussian> weights = new HashMap<Feature, Gaussian>();
    
    
    public AdPredictor(double beta, double priorProbability, double epsilon, int numFeatures) {
        this.beta = beta;
        this.priorProbability = priorProbability;
        this.epsilon = epsilon;
        this.numFeatures = numFeatures;
        
        Gaussian biasWeight = Utils.priorBiasWeight(priorProbability, beta, numFeatures);
        setWeight(Utils.biasFeature(), biasWeight);
    }
    
    public double predict(Feature[] features) {
        LOG.info(String.format("Predicting: %d features", features.length));
        
        if (features.length != numFeatures)
            throw new IllegalArgumentException(String.format("Number of features should be %d", numFeatures));
        
        double[] totalMeanVar = activeMeanVariance(features);
        return NormalDistribution.CDF(totalMeanVar[0] / totalMeanVar[1], 0, 1);
    }
    
    public void train(Feature[] features, boolean label) {
        if (features.length != numFeatures)
            throw new IllegalArgumentException(String.format("Number of features should be %d", numFeatures));
        
        LOG.info(String.format("Training: %s, %s features", label, features.length));
        
        double y = Utils.labelToDouble(label);
        double[] totalMeanVar = activeMeanVariance(features);
        double totalMean = totalMeanVar[0];
        double totalVar = totalMeanVar[1];
        
        double[] vw = Utils.gaussianCorrections(y * totalMean / Math.sqrt(totalVar));
        double v = vw[0];
        double w = vw[1];
        
        for (Feature feature : features) {
            Gaussian weight = getWeight(feature);
            double meanDelta = y * weight.variance / Math.sqrt(totalVar) * v;
            double varMult = 1.0 - weight.variance / totalVar * w;
            Gaussian updated = new Gaussian(weight.mean + meanDelta, weight.variance + varMult);
            
            setWeight(feature, applyDynamics(weight));
        }
    }

    public Map<Feature, Gaussian> getWeights() {
        return weights;
    }
    
    protected double[] activeMeanVariance(Feature[] features) {
        double sumMean = 0.0, sumVar = 0.0;
        
        for (Feature f : features) {
            Gaussian w = getWeight(f);
            sumMean += w.mean;
            sumVar += w.variance;
        }
        
        return new double[]{sumMean, (sumVar + Math.pow(beta, 2))};
    }
    
    protected Gaussian applyDynamics(Gaussian weight) {
        Gaussian prior = Utils.priorWeight();
        double adjVar = weight.variance * prior.variance / ((1.0 - epsilon) * prior.variance + epsilon * weight.variance);
        double ajdMean = adjVar * ((1.0 - epsilon) * weight.mean / weight.variance + epsilon * prior.mean / prior.variance);
        
        Gaussian adjusted = new Gaussian(ajdMean, adjVar);
        LOG.info(String.format("Adjusting weight %s to %s", weight.toString(), adjusted.toString()));
        
        return adjusted;
    }
    
    protected final void setWeight(Feature feature, Gaussian weight) {
        if (Double.isNaN(weight.mean) || weight.variance < 0)
            throw new IllegalArgumentException("Mean should be a number and variance should be > 0");
        
        LOG.info(String.format("Setting feature: %s frow weight: %s to weight: %s", feature.toString(), getWeight(feature).toString(), weight.toString()));
        
        weights.put(feature, weight);
    }
    
    protected Gaussian getWeight(Feature feature) {
        Gaussian weight = weights.get(feature);
        
        if (weight == null) return Utils.priorWeight();
        return weight;
    }
    
    protected double importance(Feature feature) {
        double priorPred = predict(Utils.createFeatures(numFeatures));
        double withWeightPred = predict(Utils.createFeatures(numFeatures, feature));
        
        return Utils.klDivergence(withWeightPred, priorPred);
    }
}
