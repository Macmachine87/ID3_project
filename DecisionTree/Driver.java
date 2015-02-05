package com.boeing.advBigData.decisionTree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;



/**
 * Starting point for decision tree 
 * @author Scott
 *
 */
public class Driver {
	
	public static void main(String[] args) {
		/*
		 * arg 0 input file
		 * arg 1 output directory
		 */
		Driver iris = new Driver();
		Map<Integer,IrisRow> irisMap = iris.readFile(args[0]);
		if(!irisMap.isEmpty()){
			//Preprocess the data to categorize the attributes
			iris.discretize(irisMap);
			//Split rows into 10 parts
			Map<Integer,Map<Integer,IrisRow>> splitRows = iris.parseIntoMaps(irisMap,10);
			//Using a 80/20 split for initial tree building and testing, this will output the confusion matrix
			List<IrisRow> testData = iris.copyMapToList(splitRows.get(0));
			testData.addAll(iris.copyMapToList(splitRows.get(1)));
			List<IrisRow> trainingData = iris.copyMapToList(splitRows.get(2));
			trainingData.addAll(iris.copyMapToList(splitRows.get(3)));
			trainingData.addAll(iris.copyMapToList(splitRows.get(4)));
			trainingData.addAll(iris.copyMapToList(splitRows.get(5)));
			trainingData.addAll(iris.copyMapToList(splitRows.get(6)));
			trainingData.addAll(iris.copyMapToList(splitRows.get(7)));
			trainingData.addAll(iris.copyMapToList(splitRows.get(8)));
			trainingData.addAll(iris.copyMapToList(splitRows.get(9)));
			//Build the decision tree
			DecisionTree decisionTree = iris.buildDecisionTree(trainingData);
			//Run the test data and determine accuracy
			iris.evaluateData(decisionTree, testData);
			double accuracy = iris.checkAccuracy(testData); 
			
			//Outputing results
			iris.outputData(args[1] + "unprunedTest.txt", testData);
			iris.outputTree(decisionTree, args[1] + "unprunedTree.txt");
			Map<String,Map<String,Integer>> confusion = iris.calculateConfusion(testData);
			iris.outputConfusion(confusion, args[1] + "unprunedConfusion.txt",accuracy);
			
			//Prune tree and output results again
			DecisionTree prunedTree = iris.pruneTree(decisionTree, testData);
			iris.evaluateData(prunedTree, testData);
			accuracy = iris.checkAccuracy(testData); 
			
			//Outputing results
			iris.outputData(args[1] + "prunedTest.txt", testData);
			iris.outputTree(prunedTree, args[1] + "prunedTree.txt");
			confusion = iris.calculateConfusion(testData);
			iris.outputConfusion(confusion, args[1] + "prunedConfusion.txt",accuracy);
			
			//Run the 10 Fold Cross Validation on the model
			double tenFoldAccuracy = iris.performCrossValidation(splitRows);
			Map<String,String> output = new HashMap<String,String>();
			output.put("Confusion Accuracy", Double.toString(tenFoldAccuracy));
			iris.outputData(args[1] + "tenFoldConfusion.txt", output);
		}
	
	}
	/*
	 * Execute cross validation on the model
	 */
	public double performCrossValidation(Map<Integer,Map<Integer,IrisRow>> splitRows){
		double[] accuracy = new double[10];
		for(int i = 0; i < 10; i++){
			List<IrisRow> testData = new ArrayList<IrisRow>();
			List<IrisRow> trainingData = new ArrayList<IrisRow>();
			for(int j = 0; j < 10; j++){
				if (j == i){
					testData.addAll(this.copyMapToList(splitRows.get(j)));
				}
				else{
					trainingData.addAll(this.copyMapToList(splitRows.get(j)));
				}
			}
			DecisionTree decisionTree = this.buildDecisionTree(trainingData);
			DecisionTree prunedTree = this.pruneTree(decisionTree, testData);
			
			this.evaluateData(prunedTree, testData);
			accuracy[i] = this.checkAccuracy(testData);
		}
		//Determine accuracy of the model
		double total =0.0;
		for (int i = 0; i < 10; i ++){
			total = total + accuracy[i];
		}
		return total/10.0;
	}
	
	/*
	 * Evaluate the list of rows, this sets the resulting answer back into the original row.
	 */
	public void evaluateData(DecisionTree decisionTree, List<IrisRow> data){
		Iterator<IrisRow> rows = data.iterator();
		while(rows.hasNext()){
			IrisRow row = rows.next();
			decisionTree.evaluate(row);
		}	
	}
	/*
	 * Prepare the iris data, discretize the data
	 * Starting very simple, 0/1  above or below average value
	 * with a binary tree, seems we want to make this a simple decision, 
	 * when I understand it better this can change, ideally leaving the rest untouched.
	 */
	public void discretize(Map<Integer,IrisRow> data){
		float petalLength = 0, petalWidth = 0, sepalLength = 0, sepalWidth = 0;
		//Loop over the data, get the total for each column, convert to average, second loop with then load the values into each row.
		Iterator<Integer> rows = data.keySet().iterator();
		int rowCount = 0;
		while (rows.hasNext()){
			Integer i = rows.next();
			IrisRow row = data.get(i);
			petalLength += row.getPetalLength();
			petalWidth += row.getPetalWidth();
			sepalLength += row.getSepalLength();
			sepalWidth += row.getSepalWidth();
			rowCount++;
		}
		float avgPetalLength = petalLength/rowCount;
		float avgPetalWidth = petalWidth/rowCount;
		float avgSepalLength = sepalLength/rowCount;
		float avgSepalWidth = sepalWidth/rowCount;
		rows = data.keySet().iterator();
		while (rows.hasNext()){
			Integer i = rows.next();
			IrisRow row = data.get(i);
			if(row.getSepalLength()<=avgSepalLength){
				row.setSepalLengthInt(0);
			}
			else{
				row.setSepalLengthInt(1);
			}
			row.setSepalWidthInt(row.getSepalWidth()<=avgSepalWidth?0:1);
			row.setPetalLengthInt(row.getPetalLength()<=avgPetalLength?0:1);
			row.setPetalWidthInt(row.getPetalWidth()<=avgPetalWidth?0:1);
		}
	}
	/*
	 * Base of building the decision tree
	 */
	public DecisionTree buildDecisionTree(List<IrisRow> data){
		DecisionTree tree = new DecisionTree();
		int[] attributes = {0,1,2,3};
		tree.setRootNode(processDecisionTree(null, data, attributes));
		return tree;
	}
	/*
	 * Recursive function to build the tree
	 */
	public Node processDecisionTree(TreeNode parent, List<IrisRow> data, int[] attributes){
		Iterator<IrisRow> rows = data.iterator();
		
		Map<Integer,Evaluation> evaluations = new HashMap<Integer,Evaluation>();
		//Set up a map of evaluations, one for each remaining attribute
		for(int attribute:attributes){
			evaluations.put(attribute, new Evaluation());
		}
		while (rows.hasNext()){
			IrisRow row = rows.next();
			//Do the evaluation
			int[] vals = row.getAttributes();
			for (int attribute:attributes){
				if(vals[attribute] == 0){
					evaluations.get(attribute).getLeft().add(row);
				}
				else{
					evaluations.get(attribute).getRight().add(row);
				}
			}
		}
		//once the split is done, I need to count how many of each are in each list
		//I can explicitly calculate the entropy of a list wihtout any other data
		double currentEntropy = calculateEntropy(data);
		Iterator<Integer> i = evaluations.keySet().iterator();
		while(i.hasNext()){
			Integer key = i.next();
			Evaluation eval = evaluations.get(key);
			eval.calculateEntropy(currentEntropy);
		}
		//Determine the winning attribute, found there are a number of conditions here where this does not find a winner
		//Need to make that a smother operation.
		double highInfoGain = 0;
		i = evaluations.keySet().iterator();
		int highGainAttribute = -1;
		while(i.hasNext()){
			Integer key = i.next();
			Evaluation eval = evaluations.get(key);
			if(eval.getInfoGain() > highInfoGain){
				highInfoGain = eval.getInfoGain();
				highGainAttribute = key;
			}
		}
		Evaluation highGainEvaluation = evaluations.get(highGainAttribute);
		//Need to establish exit conditions for the recursion
		//Only evaluating the first two of three conditions
		//Every element in the subset belongs to the same class
		//There are no more attributes to be selected
		if(currentEntropy == 0 || attributes.length == 1 || highGainEvaluation == null || (highGainEvaluation != null && (highGainEvaluation.getLeft().size() == 0 || 
				highGainEvaluation.getRight().size() == 0 )) ){
			//Ran into a case where highGainEvaluation was null because the above selector logic did not find a highest
			//information gain, that was due to the fact that two attributes were evaluated and they both put
			//all data down one branch, therefore the entropy of both children was identical to the parent.
			//In this case we gain nothing by branching the data and we build a leaf. This is not one of the algorithm listed rules, but it seems valid
			//CurrentEntropy == 0 means that all elements of the data passed in are of the same type, build a leaf
			//If all elements are in one set then we build a leaf and return it
			Leaf leaf = new Leaf();
			leaf.setParent(parent);
			List<IrisRow> elements = new ArrayList<IrisRow>();
			if(highGainEvaluation != null && (highGainEvaluation.getLeft().size() == 0 || highGainEvaluation.getRight().size() == 0)){
				if(highGainEvaluation.getLeft().size() == 0){
					elements.addAll(highGainEvaluation.getLeft());
				}
				if(highGainEvaluation.getRight().size() == 0){
					elements.addAll(highGainEvaluation.getRight());
				}
			}
			else {//out of attributes or currentEntropy == 0 or highGainEvaluation was null for another reason
				elements.addAll(data);
			}
			
			leaf.setCategory(getMostCommonSpecies(elements));
			return leaf;
		}
		//There are no examples in the subset. This would appear to only happen in a case where we are evaluating a tree with more than 2 branches per node.
		
		//Build a TreeNode recursively call for left and right
		int[] newAttributes = new int[attributes.length-1];
		int count = 0;
		for(int attribute:attributes){
			if(attribute != highGainAttribute){
				newAttributes[count] = attribute;
				count++;
			}
		}
		TreeNode node = new TreeNode();
		node.setParent(parent);
		node.setEvaluatedAttribute(highGainAttribute);
		node.setThreshold(1);
		node.setLeft(processDecisionTree(node, highGainEvaluation.getLeft(),newAttributes));
		node.setRight(processDecisionTree(node, highGainEvaluation.getRight(),newAttributes));
		return node;
	}
	
	/**
	 * Pruning job
	 * returns the pruned tree
	 * @param tree
	 * @param data
	 * @return
	 */
	public DecisionTree pruneTree(DecisionTree tree, List<IrisRow> data){
		//Get the starting accuracy
		double accuracy = this.checkAccuracy(data); 
		//We will not let the accuracy drop below 90% of the initial
		double accuracy90 = accuracy*.9;
		DecisionTree prunedTree = tree;
		DecisionTree previousTree = tree;
		while(accuracy > accuracy90){
			previousTree = prunedTree;
			//Copy the tree and prune the copy
			prunedTree = new DecisionTree();
			prunedTree.setRootNode(previousTree.getRootNode().copyNode());
			accuracy = doPrune(prunedTree, data);
		}
		this.evaluateData(previousTree, data);
		
		return previousTree;
	}
	/*
	 * Do the pruning work
	 */
	public double doPrune(DecisionTree tree, List<IrisRow> data){
		
		/*
		 * prune copy
		 * get queue and retrieve the furthest node(leaf)
		 * get parent TreeNode of leaf
		 * now need to run test data set through tree
		 * determine which leaf ends up with more data
		 * 		add a counter to the leaf nodes, when one gets a hit, increment it
		 * 
		 * replace the node with a leaf that has that category
		 * determine accuracy of the new tree, if not significantly worse, keep the tree and try again
		 */
	
		//Do a breadth first enquing of the tree to find the deepest node
		LinkedList<Node> queue = DecisionTree.enqueueTree(tree);
		Leaf leaf = (Leaf)queue.peekLast(); //Deepest leaf in the tree;
		TreeNode pruneNode = leaf.getParent();
		Leaf newLeaf = new Leaf();
		//Replace the parent node with a new leaf
		Leaf leftLeaf = (Leaf)pruneNode.getLeft();
		Leaf rightLeaf = (Leaf)pruneNode.getRight();
		if(leftLeaf.getHitCount() > rightLeaf.getHitCount()){
			newLeaf.setCategory(leftLeaf.getCategory());
		}
		else{
			newLeaf.setCategory(rightLeaf.getCategory());
		}
		TreeNode parent = pruneNode.getParent();
		if(parent.getRight().equals(pruneNode)){
			parent.setRight(newLeaf);
		}
		else{
			parent.setLeft(newLeaf);
		}
		newLeaf.setParent(parent);
		
		//reset hitcount
		Iterator<Leaf> leaves = ((TreeNode)tree.getRootNode()).getLeaves().iterator();
		while(leaves.hasNext()){
			leaves.next().setHitCount(0);
		}
		this.evaluateData(tree, data);
		
		double afterAccuracy = this.checkAccuracy(data); 

		return afterAccuracy;
	}
	

	/*
	 * Calculate the entropy of a given list of data
	 */
	public static double calculateEntropy(List<IrisRow> data ){
		
		double size = data.size();
		double setosa = 0, versicolor = 0, virginica = 0;
		Iterator<IrisRow> rows = data.iterator();
		while(rows.hasNext()){
			IrisRow row = rows.next();
			if(row.getSpecies().equals("Iris-virginica")){
				virginica++;
			}
			else if(row.getSpecies().equals("Iris-versicolor")){
				versicolor++;
			}
			else if(row.getSpecies().equals("Iris-setosa")){
				setosa++;
			}
		}
		double entVi = 0, entVe = 0, entS = 0;
		if(virginica > 0)
			entVi = (-(virginica/size)*log2(virginica/size));
		if(versicolor > 0)
			entVe = (-(versicolor/size)*log2(versicolor/size));
		if(setosa > 0)
			entS = (-(setosa/size)*log2(setosa/size));
		return entVi+entVe+entS;
	}
/**
 * There are some more efficient log2 methods available, may sub one of them in later	
 * @param n
 * @return
 */
	public static double log2(double n){
		return (Math.log(n) / Math.log(2));
	}
	/*
	 * Split out most common species calculation, covered by test
	 * Determine which species shows up most often.
	 * If there is a tie, it will select the first one
	 */
	public String getMostCommonSpecies(List<IrisRow> elements){
		Map<String,Integer> counting = new HashMap<String,Integer>();
		Iterator<IrisRow> irisRows = elements.iterator();
		while(irisRows.hasNext()){
			IrisRow row = irisRows.next();
			if(!counting.containsKey(row.getSpecies()))
				counting.put(row.getSpecies(), 0);
			counting.put(row.getSpecies(),counting.get(row.getSpecies())+1);
		}
		Iterator<String> keys = counting.keySet().iterator();
		String category = "";
		int max = 0;
		while(keys.hasNext()){
			String key = keys.next();
			if(counting.get(key) > max){
				max = counting.get(key);
				category = key;
			}
		}
		return category;
	}
	
	
	/**
	 * Calculate the confusion matrix 
	 * Loops over all of the data and creates a map of the accuracy of the calculations.
	 * @param irisMap
	 * @return
	 */
	private Map<String,Map<String,Integer>> calculateConfusion(List<IrisRow> irisList){
		/*
		 * matrix of how many accurate hits we made in the predictions.
		 */
		Map<String,Map<String,Integer>> confusion = new HashMap<String,Map<String,Integer>>();
		Iterator<IrisRow> irisIterator = irisList.iterator();
		while (irisIterator.hasNext()){
			IrisRow irisRow = irisIterator.next();
			Map<String,Integer> hit = confusion.get(irisRow.getSpecies());
			if(hit == null){
				hit = new HashMap<String,Integer>();
				confusion.put(irisRow.getSpecies(), hit);
			}
			if(!hit.containsKey(irisRow.getCalculatedSpecies())){
				hit.put(irisRow.getCalculatedSpecies(), 0);
			}
			hit.put(irisRow.getCalculatedSpecies(), hit.get(irisRow.getCalculatedSpecies())+1);
		}
		return confusion;
	}
	private void outputConfusion(Map<String,Map<String,Integer>> confusion, String path, double accuracy){
		try {
			FileWriter writer = new FileWriter(path);
			/*
			 * pull the keys from confusion, sort them into a list
			 * use this sorted list to control the display data into 
			 */
			TreeSet<String> keys = new TreeSet<String>();
			keys.addAll(confusion.keySet());
			
			//Print out the first row, first column will be the actual values, remaining columns will be the calculated headers 
			writer.append("Actual Value,");
			Iterator<String> keyIterator = keys.iterator();
			while(keyIterator.hasNext()){
				writer.append(keyIterator.next());
				writer.append(",");
			}
			writer.append("Accuracy");
			writer.append("\n");
			//Write out the data rows
			keyIterator = keys.iterator();
			while(keyIterator.hasNext()){
				String key = keyIterator.next();
				Map<String,Integer> hits = confusion.get(key);
				if(hits != null && hits.containsKey(key)){
					writer.append(key);
					float hitCount = hits.get(key);
					float missCount = 0;
					Iterator<String> hitIterator = keys.iterator();
					while(hitIterator.hasNext()){
						String hitKey = hitIterator.next();
						if(!hitKey.equals(key) && hits.containsKey(hitKey)){
							missCount += hits.get(hitKey);
						}
						writer.append(",");
						if(hits.containsKey(hitKey)){
							writer.append(hits.get(hitKey).toString());
						}
						else{
							writer.append("0");
						}
					}
					writer.append(",");
					writer.append("" + (1.0f-missCount/hitCount));
					writer.append("\n");
				}
			}
			writer.append("\n");
			writer.append("Accuracy = " + accuracy);
		    writer.flush();
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/*
	 * Generic map output method
	 */
	public void outputData(String outputFile, Map<String,String> values){
		try {
			FileWriter writer = new FileWriter(outputFile);
			Iterator<String> i = values.keySet().iterator();
			while(i.hasNext()){
				String key = i.next();
				writer.append(key + " = " + values.get(key) + "\n");
			}
		    writer.flush();
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * Output all of the rows, this prints out the original classification and the predicted classification
	 */
	public void outputData(String outputFile, List<IrisRow> irisList){
		try {
			FileWriter writer = new FileWriter(outputFile);
			Iterator<IrisRow> irisIterator = irisList.iterator();
			while(irisIterator.hasNext()){
				IrisRow.writeIrisRowResult(irisIterator.next(),writer);
			}

		    writer.flush();
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	/**
	 * Printed output of the decision tree, just uses the nodes toString method
	 * @param tree
	 * @param outputFile
	 */
	public void outputTree(DecisionTree tree, String outputFile){
		try {
			FileWriter writer = new FileWriter(outputFile);
			writer.append(tree.getRootNode().toString());

		    writer.flush();
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	/**
	 * read in the input file
	 * The row interpretation is offloaded to the IrisRow class.
	 * @param fileName 
	 * @return map of IrisRow data
	 */
	private Map<Integer, IrisRow> readFile(String fileName){
		BufferedReader reader = null;
		Map<Integer,IrisRow> irisMap = new HashMap<Integer, IrisRow>();
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line = "";
			int lineNumber = 0;
			while ((line = reader.readLine()) != null){
				if(line != null && line.length() > 0){
					irisMap.put(lineNumber++, IrisRow.readIrisRow(line));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return irisMap;
	}
		
	/**
	 * method to split the input data into a set of equally sized maps
	 * The split is done by adding the row to the map by taking rowNum mod numMaps and putting hte row into that map.
	 * @param data
	 * @param numMaps
	 * @return  data split into maps
	 */
	public Map<Integer,Map<Integer,IrisRow>> parseIntoMaps(Map<Integer,IrisRow> data, int numMaps){
		Map<Integer,Map<Integer,IrisRow>> returnMap = new HashMap<Integer,Map<Integer,IrisRow>>();
		for (int i = 0; i < numMaps; i++){
			returnMap.put(i, new HashMap<Integer,IrisRow>());
		}
		Iterator<Integer> keys = data.keySet().iterator();
		while(keys.hasNext()){
			Integer key = keys.next();
			returnMap.get(key%numMaps).put(key, data.get(key));
		}
		
		return returnMap;
	}
	/*
	 * Make a complete copy of the map into a single array list.
	 */
	public List<IrisRow> copyMapToList(Map<Integer,IrisRow> irisMap){
		Iterator<Integer> rows = irisMap.keySet().iterator();
		List<IrisRow> copiedRows = new ArrayList<IrisRow>();
		while(rows.hasNext()){
			copiedRows.add(new IrisRow(irisMap.get(rows.next())));
		}
		return copiedRows;
	}
	
	/*
	 * Calculate the accuracy for a List of data
	 */
	public double checkAccuracy(List<IrisRow> data){
		Iterator<IrisRow> rows = data.iterator();
		double incorrect = 0.0;
		while(rows.hasNext()){
			IrisRow row = rows.next();
			if(!row.getSpecies().equals(row.getCalculatedSpecies())){
				incorrect++;
			}
		}
		return 1.0 - incorrect/data.size();
	}
}
