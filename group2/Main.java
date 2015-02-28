package group2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
/**
 * Generate a ensemble of trees for data classification
 * @author Scott
 *
 */
public class Main {
	public static void main(String[] args) {
		/*
		 * arg 0 path root
		 * arg 1 attribute metaData file name 
		 * arg 2 training file name
		 * arg 3 forest output name
		 */

		Forest forest = new Forest();

		System.out.println("Starting Random Forest Execution");
		//Read in the metadata for the attributes and set up the attribute objects
		Attributes attributes = new Attributes(args[0]+args[1]);
		forest.setAttributes(attributes);		

		long totalCount = 0;
		long totalWrong = 0;
		//Set up the attribute buckets based on the training data
		attributes.generateBuckets(args[0]+args[2]);
		
		Map<Integer,Double> outOfBagError = new HashMap<Integer,Double>();
		Map<Integer, Map<String,Map<String,Integer>>> confusion = new HashMap<Integer, Map<String,Map<String,Integer>>> ();
		
		int numberOfTrees = attributes.getAttributeMetaData().size() * 3;
		numberOfTrees = 3;
		for (int sequence = 0; sequence < numberOfTrees; sequence++) {

			Node root = new Node();
			//Set up the attributes for a new tree, clear them out and select the next random set.
			attributes.clearSelectedAttributes();
			attributes.chooseColumns();
			//System.out.println("chosen columns " + attributes.getInUseColumnNames());
			
			//Read in the training data
			List<int[]> data = RowUtilities.readFileForTraining(args[0]+args[2], attributes, sequence);
			
			//Build the tree
			Node tree = TreeUtilities.buildTree(root, data, attributes.getInUseAttributeMetaData());

			//Read in the test data
			List<int[]> testData = RowUtilities.readTestFile(args[0]+args[2], attributes, sequence);
			
			//Test the tree on the test data
			List<int[]> testResults = TreeUtilities.testData(tree, testData,attributes);
			AttributeMetaData classificationAttribute = attributes.getClassificationAttribute();
			
			//Count the misses
			long count = 0;
			long wrongCount = 0;
			Iterator<int[]> rows = testResults.iterator();
			while (rows.hasNext()) {
				int[] row = rows.next();
				count++;
				if (row[classificationAttribute.getId()] != row[classificationAttribute.getId() + 1]) {
					wrongCount++;
				}
			}
			double oobError = (((double) wrongCount / count) * 100);
			System.out.println("Sequence " + sequence + " " + new Date() +  " Total instances processed = " + count + " with " + wrongCount + " error(s), Out of Bag Error Rate: " + oobError + "%");
			outOfBagError.put(sequence, oobError);
			forest.getTrees().add(tree);
			totalCount += count;
			totalWrong += wrongCount;
			confusion.put(sequence, TreeUtilities.calculateConfusion(testData, attributes));
			TreeUtilities.outputConfusion(confusion.get(sequence), args[0]+"confusoin" + sequence + ".txt", oobError);
		}

		System.out.println("OBB " + ((double) totalWrong / totalCount) * 100);
		Iterator<Integer> i = outOfBagError.keySet().iterator();
	
		int count = 0;
		double sum = 0.0;
		while(i.hasNext()){
			count ++;
			sum = outOfBagError.get(i.next());
		}
		System.out.println("OBB2 = " + (sum/count)*100);
		
		//Serialize the forest out to disk for later usage
		Forest.serializeRandomForestToFile(args[0] + args[3], forest);
	}
}