package group2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * This represents a node in the tree. 
 * A node will contain tree references to parents and two children.
 * As well as data parsing information like the split point and the attribute that will be split on.
 * Also if this node represents a leaf it will have the classification of that leaf.
 * @author Scott
 *
 */
public class Node implements java.io.Serializable {
	private Node parent;
	private Node left;
	private Node right;
	private double entropy;
	private String classification; // If this is a leaf node then it will list  its classification

	//Rules equals is the only one currently in use
	public enum RuleType {
		equals, greaterE, lessE
	};

	private RuleType ruleType;
	private int splitPoint;
	private int attribute;

	public String toString() {
		String string = "\nAttribute " + attribute;
		if (classification != null) {
			string += " classification " + classification;
		}
		if (ruleType != null) {
			string += " RuleType = " + ruleType.toString();
			string += " splitPoint = " + splitPoint;
		}
		if (left != null) {
			string += " Left = " + left.toString();
		}
		if (right != null) {
			string += " right " + right.toString();
		}
		return string;
	}

	public int getSplitPoint() {
		return splitPoint;
	}

	public void setSplitPoint(int splitPoint) {
		this.splitPoint = splitPoint;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getParent() {
		return parent;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public double getEntropy() {
		return entropy;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public RuleType getRuleType() {
		return ruleType;
	}

	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}

	public int getAttribute() {
		return attribute;
	}

	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}

	public static final String rightSplit = "right";
	public static final String leftSplit = "left";

	/**
	 * Splits the data according to the rules that the node holds
	 * @param data
	 * @return a map of two keys left and right containing the split data
	 */
	public Map<String, List<int[]>> splitData(List<int[]> data) {
		Map<String, List<int[]>> splitData = new HashMap<String, List<int[]>>();
		List<int[]> left = new ArrayList<int[]>();
		List<int[]> right = new ArrayList<int[]>();
		splitData.put(Node.rightSplit, right);
		splitData.put(Node.leftSplit, left);
		Iterator<int[]> dataIterator = data.iterator();
		while (dataIterator.hasNext()) {
			int[] row = dataIterator.next();
			String split = this.splitValue(row);
			if (split.equals(leftSplit)) {
				left.add(row);
			} else {
				right.add(row);
			}
		}
		return splitData;
	}
	
	/**
	 * Determine the split for a value
	 * @param row
	 * @return right or left
	 */
	public String splitValue(int[] row) {
		int value = (Integer) row[this.getAttribute()];
		if (ruleType.equals(RuleType.equals)) {
			if (value == splitPoint) {
				return leftSplit;
			} else {
				return rightSplit;
			}
		} 
		else if (ruleType.equals(RuleType.lessE)) {
		} 
		else if (ruleType.equals(RuleType.greaterE)) {
		}
		return leftSplit;
	}
}
