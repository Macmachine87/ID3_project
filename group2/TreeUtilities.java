/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package group2;

import group2.Node.RuleType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Functions used in the building of the trees
 * @author sf250d
 */
public class TreeUtilities {
	
	/**
	 * Primary tree building routine
	 * @param root
	 * @param data
	 * @param attrMD
	 * @return
	 */
	public static Node buildTree(Node root, List<int[]> data,	Map<Integer, AttributeMetaData> attrMD) {
		Map<Integer, Map<Integer, Integer>> mapAl = new HashMap<>();
		// dataIdx are the attributes that are in use
		ArrayList<Integer> dataIdx = new ArrayList<>();
		AttributeMetaData classificationAttribute = null;
		// Collect in use attributes into the list
		for (Integer i : attrMD.keySet()) {
			if (attrMD.get(i).isInUse()) {
				dataIdx.add(i);
			}
			if (attrMD.get(i).getType().equals(AttributeType.classification)) {
				classificationAttribute = attrMD.get(i);
			}
		}
		dataIdx.remove(dataIdx.size() - 1);// Remove the classification
		if (dataIdx.size() == 0) {
			// Out of attributes to measure
			if (data != null && data.size() > 0 && data.get(0) != null) {
				root.setClassification(classificationAttribute.getReverseValues().get(data.get(0)[classificationAttribute.getId()]));
				//System.out.println("CLASSIFICATION:"
				//		+ data.get(0)[classificationAttribute.getId()]);
			} else {
				root.getParent();
			}
			return root;
		}
		AttributeMetaData bestAttr = null;
		double bestGain = 0.00;
		root.setEntropy(Entropy.calculateWholeDSEntropy(data));
		if (root.getEntropy() == 0) {
			if (data == null || data.isEmpty()) {
				System.out.println("NO DATA IN BIN!");
			} else {
				root.setClassification(classificationAttribute.getReverseValues().get(data.get(0)[classificationAttribute.getId()]));
			}
			return root;
		}
		// Determine which attribute has the best information gain.
		Iterator<Integer> attributes = attrMD.keySet().iterator();
		while (attributes.hasNext()) {
			AttributeMetaData attribute = attrMD.get(attributes.next());
			if (!attribute.getType().equals(AttributeType.classification)) {
				Map<Integer, Map<Integer, Integer>> map = TreeUtilities.formClassCounts(data, attribute);
				double gain = InformationGain.calculateInfoGain(
						root.getEntropy(), map);
				if (gain >= bestGain) {
					bestAttr = attribute;
					bestGain = gain;
					mapAl = map;
				}
				// System.out.println("ATTR:"+i+" gain:" + gain);
			}
		}
		/*
		 * 
		 * Iterate through each category for the best attribute determine the
		 * the category with the lowest entropy store that value locally
		 * 
		 * Create a new map with cat 1 and 2 and another map with 3+ calculate
		 * entropy for both continue this with adding a category from the right
		 * to the left. Use this data to find the lowest entropy for the set
		 * 
		 * Compare this to the entropy above per category
		 * 
		 * Choose the rule that is the lowest
		 */
		// determine best split rule and build child nodes
		if (bestAttr != null) {
			Iterator<Integer> categories = mapAl.keySet().iterator();
			double minCategoryEntropy = -1;
			Integer minCategoryName = null;
			while (categories.hasNext()) {
				Integer category = categories.next();
				double entropy = Entropy.calculateSubEntropy(mapAl
						.get(category));
				if (minCategoryEntropy == -1) {
					minCategoryEntropy = entropy;
					minCategoryName = category;
				}
				if (entropy < minCategoryEntropy) {
					minCategoryEntropy = entropy;
					minCategoryName = category;
				}
			}
			if (minCategoryName != null) {
			//	System.out.println("Min Category " + minCategoryName + " "
			//			+ minCategoryEntropy + " min cat size = "
			//			+ mapAl.get(minCategoryName).size());
			}
			/*
			 * GOing to go with max from above, can refine later, but that will
			 * give an answer, I can build the rest of the code and come back.
			 * if (mapAl.keySet().size() > 2){ //If only 2 categories, then we
			 * pick one and move on. Map<String, Integer> leftSide = new
			 * HashMap<String,Integer>(); leftSide.put(arg0, arg1) for (int i =
			 * 1; i < mapAl.keySet().size() -1; i ++){ } }
			 */
			root.setAttribute(bestAttr.getId());
			root.setRuleType(RuleType.equals);
			root.setSplitPoint(minCategoryName);
			Map<String, List<int[]>> splitData = root.splitData(data);
			Node left = new Node();
			left.setParent(root);
			Node right = new Node();
			right.setParent(root);
			Map<Integer, AttributeMetaData> nextAttrs = new HashMap<Integer, AttributeMetaData>();
			Iterator<Integer> attrs = attrMD.keySet().iterator();
			while (attrs.hasNext()) {
				AttributeMetaData metaData = attrMD.get(attrs.next());
				if (metaData.getId() != bestAttr.getId()) {
					nextAttrs.put(metaData.getId(), metaData);
				}
			}
			if (splitData.get(Node.rightSplit).size() == 0
					|| splitData.get(Node.leftSplit).size() == 0) {
				root.setClassification( classificationAttribute.getReverseValues().get(findMajorityClass(data, classificationAttribute)));
			} else {
				root.setRight(TreeUtilities.buildTree(right,
						splitData.get(Node.rightSplit), nextAttrs));
				root.setLeft(TreeUtilities.buildTree(left,
						splitData.get(Node.leftSplit), nextAttrs));
			}
		} else {
			root.setClassification( classificationAttribute.getReverseValues().get(findMajorityClass(data, classificationAttribute)));
			return root;
		}
		return root;
	}


	public static  int findMajorityClass(List<int[]> data,
			AttributeMetaData classificationAttribute) {
		Iterator<int[]> rows = data.iterator();
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		while (rows.hasNext()) {
			int[] row = rows.next();
			int value = row[classificationAttribute.getId()];
			if (!counts.containsKey(value)) {
				counts.put(value, 1);
			} else {
				counts.put(value, counts.get(value) + 1);
			}
		}
		Integer max = -1;
		long count = 0;
		Iterator<Integer> classifications = counts.keySet().iterator();
		while (classifications.hasNext()) {
			Integer classification = classifications.next();
			if (counts.get(classification) > count) {
				max = classification;
				count = counts.get(classification);
			}
		}
		return max;
	}
	
	/**
	 * Testing routine to run through a complete data set and set the result to the last column.
	 * @param tree
	 * @param data
	 * @param attributes
	 * @return
	 */
	public static  List<int[]> testData(Node tree, List<int[]> data, Attributes attributes) {
		Iterator<int[]> rows = data.iterator();
		AttributeMetaData classification = attributes.getClassificationAttribute();
		while (rows.hasNext()) {
			int[] row = rows.next();
			row[row.length - 1] = classification.getKnownValues().get(testRow(tree, row));
		}
		return data;
	}
	
	/**
	 * The scoring routine.
	 * Pass in a set of data and a tree and this will return the classification.
	 * @param node
	 * @param row
	 * @return
	 */
	public static String testRow(Node node, int[] row) {
		if (node.getClassification() != null)
			return node.getClassification();
		String branch = node.splitValue(row);
		if (branch.equals(Node.rightSplit)) {
			return testRow(node.getRight(), row);
		} else {
			return testRow(node.getLeft(), row);
		}
	}
	
	/**
	 * Calculate the confusion matrix 
	 * Loops over all of the data and creates a map of the accuracy of the calculations.
	 * @param 
	 * @return
	 */
	public static Map<String,Map<String,Integer>> calculateConfusion(List<int[]> data, Attributes attributes){
		/*
		 * matrix of how many accurate hits we made in the predictions.
		 */
		AttributeMetaData classificationAttribute = attributes.getClassificationAttribute();
		Map<String,Map<String,Integer>> confusion = new HashMap<String,Map<String,Integer>>();
		Iterator<int[]> iterator = data.iterator();
		while (iterator.hasNext()){
			int[] row = iterator.next();
			int index = classificationAttribute.getId();
			String classification = classificationAttribute.getReverseValues().get(row[index]);
			String calculated = classificationAttribute.getReverseValues().get(row[index +1]);
			Map<String,Integer> hit = confusion.get(classification);
			if(hit == null){
				hit = new HashMap<String,Integer>();
				confusion.put(classification, hit);
			}
			if(!hit.containsKey(calculated)){
				hit.put(calculated, 0);
			}
			hit.put(calculated, hit.get(calculated)+1);
		}
		return confusion;
	}

	public static List<Integer> getUnique(List<Integer> data) {
		Set<Integer> set = new HashSet<>(data);
		List<Integer> classArr = new ArrayList<Integer>();
		for (Integer s : set) {
			classArr.add(s);
		}
		return classArr;
	}

	public static int getFreqOfIntInData(ArrayList<Integer> data,int searchStr) {
		int i = Collections.frequency(data, searchStr);
		return i;
	}

	/*
	 * for a given attribute return data is a map indexed by bin (0-9 for
	 * continuous variables, discrete values for the categorical attributes) The
	 * value of the map is each of the classifications and the count of those
	 * classifications for this data set and attribute.
	 */
	public static Map<Integer, Map<Integer, Integer>> formClassCounts(List<int[]> data, AttributeMetaData attribute) {
		if (data.isEmpty()) {
			System.out.println("no data");
		}
		List<Integer> classData = new ArrayList<Integer>();
		List<Integer> attrBinData = new ArrayList<Integer>();
		Map<Integer, Map<Integer, Integer>> classMapsPerBin = new HashMap<Integer, Map<Integer, Integer>>();
		for (int[] o : data) {
			classData.add( o[o.length - 1]);
			attrBinData.add((Integer) o[attribute.getId()]);
		}
		// gets uniqueClass
		List<Integer> uniqueClass = TreeUtilities.getUnique(classData);
		// gets uniqueAttrBinVals
		List<Integer> uniqueAttrBin = TreeUtilities.getUnique(attrBinData);
		// for each unique attrBin setup a map of maps where the key is the val
		// of the bin and the inner map has a key of the classification and a
		// count for each
		for (Integer bin : uniqueAttrBin) {
			Map<Integer, Integer> classMap = new HashMap<>();
			for (Integer classification : uniqueClass) {
				classMap.put(classification, 0);
			}
			classMapsPerBin.put(bin, classMap);
		}
		for (int i = 0; i < attrBinData.size(); i++) {
			// get inner map of classMapsPerBin
			Map<Integer, Integer> classMap = classMapsPerBin.get(attrBinData.get(i));
			// get value (count) of that inner map for the specified
			// classification
			int val = classMap.get(classData.get(i));
			// increase inner map value by 1 for the specified classification
			// and set it to the classmaps per bin
			classMapsPerBin.get(attrBinData.get(i)).put(classData.get(i),val + 1);
		}
		return classMapsPerBin;
	}

	public static void outputConfusion(Map<String,Map<String,Integer>> confusion, String path, double accuracy){
		try {
			FileWriter writer = new FileWriter(path);
			/*
			 * pull the keys from confusion, sort them into a list
			 * use this sorted list to control the display data into 
			 */
			TreeSet<String> keys = new TreeSet<String>();
			keys.addAll(confusion.keySet());
			
			//Print out the first row, first column will be the actual values, remaining columns will be the calculated headers 
			writer.append("Actual Value,");
			Iterator<String> keyIterator = keys.iterator();
			while(keyIterator.hasNext()){
				writer.append(keyIterator.next());
				writer.append(",");
			}
			writer.append("Accuracy");
			writer.append("\n");
			//Write out the data rows
			keyIterator = keys.iterator();
			while(keyIterator.hasNext()){
				String key = keyIterator.next();
				Map<String,Integer> hits = confusion.get(key);
				if(hits != null && hits.containsKey(key)){
					writer.append(key);
					float hitCount = hits.get(key);
					float missCount = 0;
					Iterator<String> hitIterator = keys.iterator();
					while(hitIterator.hasNext()){
						String hitKey = hitIterator.next();
						if(!hitKey.equals(key) && hits.containsKey(hitKey)){
							missCount += hits.get(hitKey);
						}
						writer.append(",");
						if(hits.containsKey(hitKey)){
							writer.append(hits.get(hitKey).toString());
						}
						else{
							writer.append("0");
						}
					}
					writer.append(",");
					writer.append("" + (1.0f-missCount/hitCount));
					writer.append("\n");
				}
			}
			writer.append("\n");
			writer.append("Accuracy = " + accuracy);
		    writer.flush();
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
