/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package group2;

import group2.Node.RuleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sf250d
 */
public class Tree {
	
	public Node buildTree(Node root, List<int[]> data,	Map<Integer, AttributeMetaData> attrMD) {
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
		int bestBinCt = 0;
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
				Map<Integer, Map<Integer, Integer>> map = DataUtilities.formClassCounts(data, attribute);
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
				root.setRight(this.buildTree(right,
						splitData.get(Node.rightSplit), nextAttrs));
				root.setLeft(this.buildTree(left,
						splitData.get(Node.leftSplit), nextAttrs));
			}
		} else {
			root.setClassification( classificationAttribute.getReverseValues().get(findMajorityClass(data, classificationAttribute)));
			return root;
		}
		return root;
	}



	public int findMajorityClass(List<int[]> data,
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
	
	
	public static  List<int[]> testData(Node tree, List<int[]> data, Attributes attributes) {
		Iterator<int[]> rows = data.iterator();
		AttributeMetaData classification = attributes.getClassificationAttribute();
		while (rows.hasNext()) {
			int[] row = rows.next();
			row[row.length - 1] = classification.getKnownValues().get(testRow(tree, row));
			String x = ";";
		}
		return data;
	}
	
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
	 * @param irisMap
	 * @return
	 */
	public Map<String,Map<String,Integer>> calculateConfusion(List<int[]> data, Attributes attributes){
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
	
}
