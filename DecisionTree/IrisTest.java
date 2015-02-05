package com.boeing.advBigData.decisionTree;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;


public class IrisTest {
	public static final String[] species = {"Iris-versicolor","Iris-setosa","Iris-virginica"};
	@Test
	public void entropyTest(){
		
		Driver driver = new Driver();
		List<IrisRow> data = new ArrayList<IrisRow>();
		
		IrisRow row = new IrisRow();
		row.setSpecies(species[0]);
		data.add(row);
		IrisRow row1 = new IrisRow();
		row1.setSpecies(species[1]);
		data.add(row1);
		IrisRow row2 = new IrisRow();
		row2.setSpecies(species[2]);
		data.add(row2);
		double e = driver.calculateEntropy(data);
		assertEquals(1.5849,e,DELTA);
		String category = driver.getMostCommonSpecies(data);
		assertEquals(species[0], category);//They are all the same, I expect the first one to come back
		IrisRow row3 = new IrisRow();
		row3.setSpecies(species[2]);
		data.add(row3);
		IrisRow row4 = new IrisRow();
		row4.setSpecies(species[2]);
		data.add(row4);
		e = driver.calculateEntropy(data);
		assertEquals(1.3709,e,DELTA);
		category = driver.getMostCommonSpecies(data);
		assertEquals(species[2], category);
	}
	@Test
	public void log2Test(){
		assertEquals (2,Driver.log2(4),DELTA);
		assertEquals(-1,Driver.log2(.5),DELTA);
		assertEquals(-1.5849,Driver.log2(1.0/3.0),DELTA);
	}
	private static final double DELTA = 1e-4;
	
	/*
	 * Test the accuracy calculation 
	 */
	@Test
	public void accuracyTest(){
		Driver driver = new Driver();
		List<IrisRow> data = new ArrayList<IrisRow>();
		
		IrisRow row = new IrisRow();
		row.setSpecies(species[0]);
		row.setCalculatedSpecies(species[0]);
		data.add(row);
		IrisRow row1 = new IrisRow();
		row1.setSpecies(species[1]);
		row1.setCalculatedSpecies(species[1]);
		data.add(row1);
		IrisRow row2 = new IrisRow();
		row2.setSpecies(species[2]);
		row2.setCalculatedSpecies(species[2]);
		data.add(row2);
		double e = driver.checkAccuracy(data);
		assertEquals(1,e,DELTA);
		IrisRow row3 = new IrisRow();
		row3.setSpecies(species[2]);
		row3.setCalculatedSpecies(species[0]);
		data.add(row3);
		IrisRow row4 = new IrisRow();
		row4.setSpecies(species[2]);
		row4.setCalculatedSpecies(species[0]);
		data.add(row4);
		e = driver.checkAccuracy(data);
		assertEquals(.6,e,DELTA);
		
		
	}
	/*
	 * Test the breadth first enqueueing of the tree
	 * n1
	 * 		n2
	 * 			n4
	 * 				n5
	 * 					l1
	 * 					l2
	 * 				l3
	 * 			n6
	 * 				l4
	 * 				n7
	 * 					n8
	 * 						l5
	 * 						l6
	 * 					l7
	 * 		n3	
	 * 			n9
	 * 				l8
	 * 			n10
	 * 				l9
	 * 
	 */
	@Test
	public void queueTest(){
		DecisionTree tree = new DecisionTree();
		TreeNode n1 = new TreeNode(0,0);
		TreeNode n2 = new TreeNode(0,0);
		TreeNode n3 = new TreeNode(0,0);
		TreeNode n4 = new TreeNode(0,0);
		TreeNode n5 = new TreeNode(0,0);
		TreeNode n6 = new TreeNode(0,0);
		TreeNode n7 = new TreeNode(0,0);
		TreeNode n8 = new TreeNode(0,0);
		TreeNode n9 = new TreeNode(0,0);
		TreeNode n10 = new TreeNode(0,0);
		Leaf l1 = new Leaf("l1");
		Leaf l2 = new Leaf("l2");
		Leaf l3 = new Leaf("l3");
		Leaf l4 = new Leaf("l4");
		Leaf l5 = new Leaf("l5");
		Leaf l6 = new Leaf("l6");
		Leaf l7 = new Leaf("l7");
		Leaf l8 = new Leaf("l8");
		Leaf l9 = new Leaf("l9");

		tree.setRootNode(n1);
		n1.setLeft(n2);
		n2.setLeft(n4);
		n4.setLeft(n5);
		n5.setLeft(l1);
		n5.setRight(l2);
		n4.setRight(l3);
		n2.setRight(n6);
		n6.setLeft(l4);
		n6.setRight(n7);
		n7.setLeft(n8);
		n8.setLeft(l5);
		n8.setRight(l6);
		n7.setRight(l7);
		n1.setRight(n3);
		n3.setLeft(n9);
		n9.setLeft(l8);
		n3.setRight(n10);
		n10.setLeft(l9);

		LinkedList<Node> list = DecisionTree.enqueueTree(tree);
		assertEquals(l6, list.peekLast());
	}
	

	
	
	
	
	
	
	
	
	
	
}

