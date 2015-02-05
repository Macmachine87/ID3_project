package com.boeing.advBigData.decisionTree;

/**
 * Leaf node in a decision tree.
 * This contains the category for the leaf, the parent node and for pruning purposes the hit count when testing.
 * @author Scott
 *
 */
public class Leaf implements Node{
	private TreeNode parent;
	private String category;
	private int hitCount = 0; //Hit counter for pruning purposes only
	public Leaf(){
		
	}
	public Leaf(String category){
		this.setCategory(category);
	}
	public TreeNode getParent() {
		return (TreeNode)parent;
	}
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String toString(){
		return "leaf,"+category + " hit " + hitCount;
	}
	public Node copyNode(){
		Leaf newLeaf = new Leaf();
		newLeaf.setCategory(category);
		newLeaf.setHitCount(hitCount);
		return newLeaf;
	}
	public int getHitCount() {
		return hitCount;
	}
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}
}