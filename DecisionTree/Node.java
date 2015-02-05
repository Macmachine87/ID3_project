package com.boeing.advBigData.decisionTree;
/**
 * Interface for nodes in a decision tree
 * @author Scott
 *
 */
public interface Node {
	public TreeNode getParent();
	public void setParent(TreeNode parent);
	public Node copyNode();
}