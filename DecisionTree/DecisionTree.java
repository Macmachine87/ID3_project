package com.boeing.advBigData.decisionTree;

import java.util.LinkedList;


/**
 * Base class for the decision tree
 * @author Scott
 *
 */
public class DecisionTree {
	private Node rootNode;

	public Node getRootNode() {
		return rootNode;
	}

	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}
	/*
	 * Evaluate a row of data through this tree
	 * Sets the answer back into the row structure
	 */
	public IrisRow evaluate(IrisRow row){
		row.setCalculatedSpecies(this.evaluate(row,this.getRootNode()));
		return row;			
	}
	
	/*
	 * Recursively process the row through the tree
	 * When we hit a leaf return the value
	 */
	private String evaluate(IrisRow row, Node node){
		if(node instanceof Leaf){ //Leaf instance is exit criteria
			Leaf leaf = (Leaf)node;
			int hitCount = leaf.getHitCount();
			leaf.setHitCount(++hitCount);
			return ((Leaf)node).getCategory();
		}
		else{
			int[] attributes = row.getAttributes();
			TreeNode treeNode = (TreeNode)node;
			if(attributes[treeNode.getEvaluatedAttribute()] < treeNode.getThreshold()){
				return this.evaluate(row, treeNode.getLeft());
			}
			else{
				return this.evaluate(row, treeNode.getRight());
			}
		}
	}
	/*
	 * Breadth first enqueueing of the tree structure
	 */
	public static LinkedList<Node> enqueueTree(DecisionTree tree){
		LinkedList<Node> list = new LinkedList<Node>();
		LinkedList<Node> queue = new LinkedList<Node>();
		Node root = tree.getRootNode();
		list.add(root);
		while(!list.isEmpty()){
			Node node = list.remove();
			queue.add(node);
			if(node instanceof TreeNode){
				TreeNode treeNode = (TreeNode)node;
				if(treeNode.getLeft() != null){
					list.add(treeNode.getLeft());
				}
				if(treeNode.getRight() != null){
					list.add(treeNode.getRight());
				}
			}
		}
		return queue;
	}
}