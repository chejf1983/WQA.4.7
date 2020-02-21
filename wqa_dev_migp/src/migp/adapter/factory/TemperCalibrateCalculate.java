/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.factory;

/**
 *
 * @author jiche Temperature calibrate calculate is simply, it just need
 * expected temperature - original temperature.
 */
public class TemperCalibrateCalculate {

    public float Calculate(float[] expectedvalue, float[] originalvalue) {       /*  */
        int len = expectedvalue.length < originalvalue.length ? expectedvalue.length : originalvalue.length;
        float exp = 0, org = 0;
        for (int i = 0; i < len; i++) {
            exp +=  expectedvalue[i] / len;
            org += originalvalue[i] / len;
        }
        return exp - org;
    }
}
