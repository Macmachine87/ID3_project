package group2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Node implements java.io.Serializable{
    private Node parent;
    private Node left;
    private Node right;
    private double entropy;

    private String classification; //If this is a leaf node then it will list its classification
    
    public enum RuleType{equals, greaterE, lessE};
    private RuleType ruleType;
    private int splitPoint;
    private int attribute;


	public String toString(){
		String string = "\nAttribute " + attribute;
		if(classification != null){
			string += " classification " + classification;
		}
		if(ruleType != null){
			string += " RuleType = " + ruleType.toString();
			string += " splitPoint = " + splitPoint;
			
		}
		if(left != null){
			string += " Left = " + left.toString();
		}
		if(right != null){
			string += " rigth " + right.toString();
		}
		return string;
	}
    
    
    public Node() {
        setEntropy(0.0);
        setParent(null);
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
	public Map<String, List<Object[]>> splitData(List<Object[]> data){
		Map<String,List<Object[]>> splitData = new HashMap<String,List<Object[]>>();
		List<Object[]> left = new ArrayList<Object[]>();
		List<Object[]> right = new ArrayList<Object[]>();
		splitData.put(Node.rightSplit, right);
		splitData.put(Node.leftSplit, left);
		
		Iterator<Object[]> dataIterator = data.iterator();
		while(dataIterator.hasNext()){
			Object[] row = dataIterator.next();
			String split = this.splitValue(row);
			if(split.equals(leftSplit)){
				left.add(row);
			}
			else{
				right.add(row);
			}
		}
		return splitData;
	}
	public String splitValue(Object[] row){
		int value = (Integer)row[this.getAttribute()];
		if(ruleType.equals(RuleType.equals)){
			if(value == splitPoint){
				return leftSplit;
			}
			else{
				return rightSplit;
			}
		}
		else if (ruleType.equals(RuleType.lessE)){
			
		}
		else if( ruleType.equals(RuleType.greaterE)){
			
		}
		return leftSplit;
	}
	
}
















