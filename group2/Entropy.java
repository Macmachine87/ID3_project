/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package group2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sf250d
 */
public class Entropy {
    
    public static double calculateWholeDSEntropy(List<Object[]> data) {
        ArrayList<String> classData = new ArrayList<>();
        double ent = 0.0;
        if (data.isEmpty()) {
            return ent;
        }
        for (Object[] o : data) {
            classData.add((String)o[o.length-1]);
        }
        
        List<String> uniqueClass = DataUtilities.getUniqueStr(classData);
        int numOfClass = uniqueClass.size();
        int classCnt;
        for (int i = 0; i < numOfClass; i++ ) {
           classCnt = DataUtilities.getFreqOfStrInData(classData, uniqueClass.get(i));
           double prob = ((double)classCnt) / (double)classData.size();
           ent += -prob * (Math.log(prob) / Math.log(2));
           //ERASE AFTER PROGRAM WORKS      System.out.println("class count:" + classCnt + " probability"+ prob + " entropy:" + ent);
        }
        return ent;
    }
    
    public static double calculateSubEntropy(Map<String, Integer> classCounts) {
        double ent = 0.0;
        int total = 0;
        for (int j : classCounts.values()) {
            total += j;
        }
        for (int j : classCounts.values()) {
            if (j != 0) {
                double prob = ((double)j) / (double)total;
                ent += -prob * (Math.log(prob) / Math.log(2));
            }
        }
        return ent;
    }
}
