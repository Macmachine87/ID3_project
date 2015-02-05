package com.boeing.advBigData.decisionTree;

import java.util.ArrayList;
import java.util.List;

/*
 * Class to manage the data used while evaluating the data for entropy and information gain
 */
public class Evaluation{
	private List<IrisRow> left = new ArrayList<IrisRow>();
	private List<IrisRow> right = new ArrayList<IrisRow>();
	private double leftEntropy;
	private double rightEntropy;
	private double newEntropy;
	private double infoGain;
	public List<IrisRow> getLeft() {
		return left;
	}public List<IrisRow> getRight() {
		return right;
	}
	public double getLeftEntropy(){
		return leftEntropy;
	}
	public double getRightEntropy() {
		return rightEntropy;
	}
	public double getNewEntropy(){
		return newEntropy;
	}
	public double getInfoGain(){
		return infoGain;
	}
	/*
	 * Internally determine entropy and gain
	 */
	public void calculateEntropy(double beforeEntropy){
		double att1LeftEntropy = Driver.calculateEntropy(left);
		double att1RightEntropy = Driver.calculateEntropy(right);
		int leftSize = left.size();
		int rightSize = right.size();
		int size = leftSize + rightSize;
		newEntropy = (leftSize/size)*att1LeftEntropy + (rightSize/size)*att1RightEntropy;
		infoGain = beforeEntropy - newEntropy;
		
	}
}