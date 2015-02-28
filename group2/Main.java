package group2;

import group2.Node.RuleType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

public class Main {
	public static void main(String[] args) {
		/*
		 * arg 0 path root
		 * arg 1 attribute metaData file name 
		 * arg 2 training file name
		 * arg 3 forest output name
		 */

		Forest forest = new Forest();
		//System.out.println("Started Training");

		System.out.println("Starting Random Forest Execution");

		Attributes attributes = new Attributes(args[0]+args[1]);
		long totalCount = 0;
		long totalWrong = 0;
		attributes.generateBuckets(args[0]+args[2]);
		System.out.println(new Date());
		Map<Integer,Double> outOfBagError = new HashMap<Integer,Double>();
		Map<Integer, Map<String,Map<String,Integer>>> confusion = new HashMap<Integer, Map<String,Map<String,Integer>>> ();
		for (int sequence = 0; sequence < attributes.getAttributeMetaData().size() * 3; sequence++) {

			//System.out.println("Building Tree: " + sequence + " " + new Date());

			Node root = new Node();
			Tree t = new Tree();

			DataModel dataModel = new DataModel();
			attributes.clearSelectedAttributes();
			attributes.chooseColumns();
			//System.out.println("chosen columns " + attributes.getInUseColumnNames());
			dataModel.setAttributes(attributes);

			dataModel.setRows(Row.readFileForTraining(args[0]+args[2], attributes, sequence));

			//System.out.println(new Date() +  " After reading data before building tree");

			Node tree = t.buildTree(root, dataModel.getRows(), dataModel.getInUseAttributeMetaData());

//			System.out.println(new Date() +  " After building tree " + tree.toString());
			
			dataModel.setTestData(Row.readTestFile(args[0]+args[2], attributes, sequence));
			

			List<int[]> testResults = t.testData(tree, dataModel.getTestData(),attributes);
			AttributeMetaData classificationAttribute = attributes.getClassificationAttribute();
			
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
			confusion.put(sequence, t.calculateConfusion(dataModel.getTestData(), attributes));
			outputConfusion(confusion.get(sequence), args[0]+"confusoin" + sequence + ".txt", oobError);
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
		forest.setAttributes(attributes);		
		
		Forest.serializeRandomForestToFile(args[0] + args[3], forest);
	}
	public static String getLastElement(final Collection<Entry<String, Integer>> c) {
		final Iterator<Entry<String, Integer>> itr = c.iterator();
		Object lastElement = itr.next();
		while (itr.hasNext()) {
			lastElement = itr.next();
		}
		return lastElement.toString().substring(0, lastElement.toString().indexOf("="));
	}

	public static String treeTraversal(Node node, String[] row) {

		String guessedClassification = null;

		if (node.getClassification() != null)
			return node.getClassification();
		else if (node.getRuleType() == RuleType.equals) {
			int cat;
			try {
				cat = (int) Double.parseDouble(row[node.getAttribute()]);
			} catch (NumberFormatException e) {
				// TODO:For categorical need to get actual value
				cat = 2;
			}

			if (cat == node.getSplitPoint()) {
				return treeTraversal(node.getLeft(), row);
			} else {
				return treeTraversal(node.getRight(), row);
			}

		} else {
			return treeTraversal(node.getLeft(), row);
		}
	}

	public static void outputConfusion(Map<String,Map<String,Integer>> confusion, String path, double accuracy){
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
}