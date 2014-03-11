package adpredictor;

import adpredictor.util.Utils;
import java.util.Arrays;
import java.util.Random;
import static org.junit.Assert.*;
import junit.framework.TestCase;

/**
 *
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class AdPredictorTest extends TestCase {
    private final Random rand = new Random();
    private static final double BETA = 0.05;
    private static final double PRIOR = 0.3;
    private static final double EPSILON = 0.01;
    private static final int NUM_FEATURES = 10;
    
    private AdPredictor predictor;
    
    public AdPredictorTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        predictor = new AdPredictor(BETA, PRIOR, EPSILON, NUM_FEATURES);
    }

    public void testBiasCorrectlyInitialized() {
        int numFeatures = 10;
        int numPriors = 10;
        
        for (int i=0; i<numPriors; i++) {
            double prior = rand.nextDouble();
            AdPredictor p = new AdPredictor(BETA, prior, EPSILON, NUM_FEATURES);
            assertEquals(p.predict(Utils.createFeatures(numFeatures)), prior, 0.00001);
        }
    }
    
    public void testImportanceOfEmptyFeature() {
        assertEquals(predictor.importance(new Feature()), 0.0);
    }
    
    public void testImportanceOfSetFeature() {
        Feature f = new Feature(10, 5);
        predictor.setWeight(f, new Gaussian(0.5, 0.5));
        double importance = predictor.importance(f);
        assertTrue(String.format("Importance %f should be greater than 0.0", importance), importance > 0.0);
    }
    
    public void testImportanceIsMonotonicInMean() {
        Feature f = new Feature(10, 5);
        double[] importances = new double[10];
        
        double[] means = Utils.linspace(0.0, 3.0, 10);
        
        for (int i=0; i<10; i++) {
            predictor.setWeight(f, new Gaussian(0.5, 0.5));
            importances[i] = predictor.importance(f);
        }
        
        double[] sorted = Arrays.copyOf(importances, 10);
        Arrays.sort(sorted);
        
        assertArrayEquals(sorted, importances, 0.0);
    }
    
    public void testDynamicsShiftTowardsPrior() {
        AdPredictor p = new AdPredictor(BETA, PRIOR, 0.05, NUM_FEATURES);
        Gaussian initial = new Gaussian(5.0, 0.5);
        Gaussian adjusted = p.applyDynamics(initial);
        
        assertTrue(initial.mean > adjusted.mean);
        assertTrue(initial.variance < adjusted.variance);
    }
    
    public void testDynamicsNeutralForPrior() {
        AdPredictor p = new AdPredictor(BETA, PRIOR, 0.05, NUM_FEATURES);
        Gaussian initial = Utils.priorWeight();
        Gaussian adjusted = p.applyDynamics(initial);
        
        assertEquals(initial.mean, adjusted.mean, 0.000001);
        assertEquals(initial.variance, adjusted.variance, 0.000001);
    }
    
    public void testDynamicIsNullForEpsilonZero() {
        AdPredictor p = new AdPredictor(BETA, PRIOR, 0.0, NUM_FEATURES);
        Gaussian initial = new Gaussian(5.0, 0.5);
        Gaussian adjusted = p.applyDynamics(initial);
        
        assertEquals(initial.mean, adjusted.mean, 0.000001);
        assertEquals(initial.variance, adjusted.variance, 0.000001);
    }
}
