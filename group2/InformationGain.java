/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package group2;

import java.util.Map;

/**
 *
 * @author sf250d
 */
public class InformationGain {
    
    public static double calculateInfoGain(double totEnt, Map<Integer, Map<String, Integer>> classCounts) {
        double gain = 0.0;
        int total = 0;
        double subEnt = 0.0;
        double prob = 0.0;
        
        for (Map.Entry<Integer,Map<String, Integer>> m : classCounts.entrySet()) {
            for (Map.Entry<String,Integer> innerMap : m.getValue().entrySet()) {
                    total += innerMap.getValue();
            }
        }
        
        for (Map.Entry<Integer,Map<String, Integer>> m : classCounts.entrySet()) {
            subEnt = Entropy.calculateSubEntropy(m.getValue());
            int count = 0;
            for (int i : m.getValue().values()) {
                count += i;
            }
            prob = ((double)count/total);
           // System.out.println("VALUES:" +m.getValue()+ " sub entropy:" + subEnt + " sub gain:" + prob*subEnt);
            gain += prob*subEnt;
        }
        
        gain = totEnt - gain;
        return gain;
    }
}
