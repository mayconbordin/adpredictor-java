package adpredictor.util;

/**
 * From: https://code.google.com/p/mcc-pfm/
 * @author Alximik
 */
public class NormalDistribution {
    /**
     * This is the inverse normal algorithm developed by P. J. Acklam. 
     * It is accurate to 1.15E-9.
     * @param p
     * @param mean
     * @param scale
     * @return 
     */
    public static double ppf(double p, double mean, double scale) {
        double p_low, p_high, q,r,x,sub_result=0;
        double a1,a2, a3, a4, a5, a6;
        double b1, b2, b3, b4, b5;
        double c1, c2, c3, c4, c5, c6;
        double d1, d2, d3, d4;
        double result = 0;
        
        a1 = -39.6968302866538+0.00000000000004;
        a2 = 220.946098424521-0.0000000000005;
        a3 = -275.928510446969 + 0.0000000000003;
        a4 = 138.357751867269;
        a5 = -30.6647980661472 +0.00000000000004;
        a6 = 2.50662827745924 - 0.000000000000001;

        b1 = -54.4760987982241 + 0.00000000000004;
        b2 = 161.585836858041 - 0.0000000000001;
        b3 = -155.698979859887 + 0.0000000000004;
        b4 = 66.8013118877197 + 0.00000000000002;
        b5 = -13.280681552885 - 0.00000000000002;

        c1 = -7.78489400243029E-03 - 3E-18;
        c2 = -0.322396458041136 - 5E-16;
        c3 = -2.40075827716184 + 0.000000000000002;
        c4 = -2.54973253934373 - 0.000000000000004;
        c5 = 4.37466414146497 - 0.000000000000002;
        c6 = 2.93816398269878 + 0.000000000000003;

        d1 = 7.78469570904146E-03 + 2E-18;
        d2 = 0.32246712907004 - 2E-16;
        d3 = 2.445134137143 - 0.000000000000004;
        d4 = 3.75440866190742 - 0.000000000000004;

        //   Define break points

        p_low = 0.02425;
        p_high = 1 - p_low;
        //   Rational approximation for lower region

        if (0 < p && p < p_low) {
            q = Math.sqrt(-2*Math.log(p));
            sub_result = (((((c1 * q + c2) * q + c3) * q + c4) * q + c5) * q + c6) / ((((d1 * q + d2) * q + d3) * q + d4) * q + 1);
        }
        //   Rational approximation for central region
        if (p_low <= p && p <= p_high) {
            q = p - 0.5;
            r = q * q;
            sub_result = (((((a1 * r + a2) * r + a3) * r + a4) * r + a5) * r + a6) * q / (((((b1 * r + b2) * r + b3) * r + b4) * r + b5) * r + 1);
        }
        //   Rational approximation for upper region
        if (p_high < p && p < 1) {
            q = Math.sqrt(-2*Math.log(1-p));
            sub_result = -(((((c1 * q + c2) * q + c3) * q + c4) * q + c5) * q + c6) / ((((d1 * q + d2) * q + d3) * q + d4) * q + 1);
        }
        result = mean + scale*sub_result;
        return result;
    }

    public static double cdf(double val, double mean, double scale){
        double L,k,x;
        final double pi = 4*Math.atan(1);
        final double a1 = 0.31938153;
        final double a2 = -0.356563782;
        final double a3 = 1.781477937;
        final double a4= -1.821255978;
        final double a5 =  1.330274429;
        x= (val - mean) / scale;
        L= Math.abs(x);
        k = 1 / (1 + 0.2316419 * L);
        double result = 0;
        result = 1-1/Math.sqrt(2*pi)*Math.exp(-L*L/2)* (a1 * k + a2 * Math.pow(k,2) + a3 * Math.pow(k,3) + a4 * Math.pow(k,4) + a5 * Math.pow(k,5));
        if (x<0)
            result = 1- result;
        return result;
    }
    
    /**
     * Calculate value of Normal distribution function on the given value of 
     * random variable.
     * @param val probability
     * @param mean average (location parametr)
     * @param scal deviation (scale parametr)
     * @return value of function
     */
    public static double pdf(double val, double mean, double scal){
        double result = 0;
        double pi = 4*Math.atan(1);
        result =(1/(scal*Math.sqrt(2*pi)))*Math.exp(-0.5*Math.pow((val-mean)/scal,2));
        return result;

    }
}