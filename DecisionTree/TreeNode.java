package com.boeing.advBigData.decisionTree;

import java.util.ArrayList;
import java.util.List;
/**
 * Node in a decision tree with possibility of two children
 * @author Scott
 *
 */
public class TreeNode implements Node{
	private int evaluatedAttribute;
	private int threshold; //Above or equal to the threshold go right
	private TreeNode parent;
	private Node left;
	private Node right;
	public TreeNode(){
		
	}
	/*
	 * Constructor used for simplifying testing code
	 */
	public TreeNode(int attribute, int threshold){
		this.setEvaluatedAttribute(attribute);
		this.setThreshold(threshold);
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
	public TreeNode getParent() {
		return parent;
	}
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	public int getEvaluatedAttribute() {
		return evaluatedAttribute;
	}
	public void setEvaluatedAttribute(int evaluatedAttribute) {
		this.evaluatedAttribute = evaluatedAttribute;
	}
	public int getThreshold() {
		return threshold;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	public String toString(){
		String leftNode = left!=null?left.toString():"None";
		String rightNode = right!=null?right.toString():"None";
		return "Node," + evaluatedAttribute + "," + threshold + ",Left("+leftNode+"),right("+rightNode+")";
	}
	/*
	 * Copy this node
	 * @see com.boeing.advBigData.decisionTree.Node#copyNode()
	 */
	public Node copyNode(){
		TreeNode newNode = new TreeNode();
		newNode.setEvaluatedAttribute(evaluatedAttribute);
		if(left != null){
			Node newLeft = left.copyNode();
			newLeft.setParent(newNode);
			newNode.setLeft(newLeft);
		}
		if(right != null){
			Node newRight = right.copyNode();
			newRight.setParent(newNode);
			newNode.setRight(newRight);
		}
		newNode.setThreshold(threshold);
		return newNode;
	}
	/*
	 * Recursively fetch all leaves that are a child of this node
	 */
	public List<Leaf> getLeaves(){
		List<Leaf> leaves = new ArrayList<Leaf>();
		if(right != null){
			if(right instanceof Leaf){
				leaves.add((Leaf)right);
			}
			else{
				leaves.addAll(((TreeNode)right).getLeaves());
			}
		}
		if(left != null){
			if(left instanceof Leaf){
				leaves.add((Leaf)left);
			}
			else{
				leaves.addAll(((TreeNode)left).getLeaves());
			}
		}
		return leaves;
	}
}