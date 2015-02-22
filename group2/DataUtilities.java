/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package group2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sf250d
 */
public class DataUtilities {
    
    public static ArrayList<Integer> usedAttributes = new ArrayList<>();
    
    public static List<String> getUniqueStr(List<String> data) {
        Set<String> set = new HashSet<>(data);
        List<String> classArr = new ArrayList();
        for (String s: set){
            classArr.add(s);
        }
        return classArr;
    }
    public static List<Integer> getUnique(List<Integer> data) {
        Set<Integer> set = new HashSet<>(data);
        List<Integer> classArr = new ArrayList<Integer>();
        for (Integer s: set){
            classArr.add(s);
        }
        return classArr;
    }
    
    public static int getFreqOfStrInData(ArrayList<String> data, String searchStr) {
        int i = Collections.frequency(data, searchStr);
        return i;
    }
    /*
     * for a given attribute
     * return data is a map indexed by bin (0-9 for continuous variables, discrete values for the categorical attributes)
     * The value of the map is each of the classifications and the count of those classifications for this data set and attribute.
     */
    public static Map<Integer,Map<String, Integer>> formClassCounts(List<Object[]> data, AttributeMetaData attribute) {
        if (data.isEmpty()){
            System.out.println("no data");
        }
        List<String> classData = new ArrayList<String>();
        List<Integer> attrBinData = new ArrayList<Integer>();
        Map<Integer,Map<String,Integer>> classMapsPerBin = new HashMap<Integer,Map<String,Integer>>();
        for (Object[] o : data) {
            classData.add((String)o[o.length-1]);
            attrBinData.add((Integer)o[attribute.getId()]);
        }
        //gets uniqueClass
        List<String> uniqueClass = DataUtilities.getUniqueStr(classData);
        //gets uniqueAttrBinVals
        List<Integer> uniqueAttrBin = DataUtilities.getUnique(attrBinData);
        //for each unique attrBin setup a map of maps where the key is the val of the bin and the inner map has a key of the classification and a count for each
        for (Integer bin : uniqueAttrBin) {
            Map<String,Integer> classMap = new HashMap<>();
            for (String classification: uniqueClass) {
                classMap.put(classification, 0);
            }
            classMapsPerBin.put(bin, classMap);
        }
        
        for (int i = 0; i < attrBinData.size(); i++) {
            //get inner map of classMapsPerBin
            Map<String, Integer> classMap = classMapsPerBin.get(attrBinData.get(i));
            //get value (count) of that inner map for the specified classification
            int val = classMap.get(classData.get(i));
            //increase inner map value by 1 for the specified classification and set it to the classmaps per bin
            classMapsPerBin.get(attrBinData.get(i)).put(classData.get(i), val + 1);
        }
  
        return classMapsPerBin;
    
        
    }
    
    public static Map<String, Integer> createBinMap(int binSize) {
        Map<String, Integer> binMap = new HashMap<>();
        for (int i = 0; i < binSize; i++) {
            binMap.put(Integer.toString(i), 0);
        }
        return binMap;
    }
    
    public static Map<String, Integer> createClassMap(ArrayList<String> uniqueClassList) {
        Map<String, Integer> classMap = new HashMap<>();
        for (int i = 0; i < uniqueClassList.size(); i++) {
            classMap.put(uniqueClassList.get(i), 0);
        }
        return classMap;
    }
    
}
