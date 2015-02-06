package ID3;

import java.util.ArrayList;
import java.util.HashMap;

/** Tree Node data object **/

public class Node {
	private HashMap<Integer, HashMap<Integer, Double>> data;
	private int depth;
	private double entropy;
	private String label;
	private Node left;
	private Node parent;
	private Node right;
	private String rule;
	private final ArrayList<Integer> traitsTested;

	// constructor for root node
	public Node(HashMap<Integer, HashMap<Integer, Double>> ds) {
		setData(ds);
		traitsTested = new ArrayList<Integer>();
	}

	// constructor for new branches
	public Node(HashMap<Integer, HashMap<Integer, Double>> ds,
			ArrayList<Integer> attributes, Node before) {
		setData(ds);
		traitsTested = attributes;
		parent = before;
		depth = before.depth++;
	}

	public HashMap<Integer, HashMap<Integer, Double>> getData() {

		return data;
	}

	public int getDepth() {
		return depth;
	}

	public double getEntropy() {
		return entropy;
	}

	public String getLabel() {
		return label;
	}

	public Node getLeft() {
		return left;
	}

	// All methods below are getters and setters for their respective traits
	public Node getParent() {
		return parent;
	}

	public Node getRight() {
		return right;
	}

	public String getRule() {
		return rule;
	}

	public ArrayList<Integer> getTraits() {
		return traitsTested;
	}

	public void setData(HashMap<Integer, HashMap<Integer, Double>> ds) {
		this.data = ds;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void setEntropy(double e) {
		this.entropy = e;
	}

	public void setLabel(String s) {
		this.label = s;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public void setParent(Node p) {
		this.parent = p;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
}
