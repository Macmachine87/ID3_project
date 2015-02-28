package group2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * Container to represent the forest of trees
 * 
 * @author Scott
 *
 */
public class Forest  implements java.io.Serializable{

	private List<Node> trees = new ArrayList<Node>();
	private Attributes attributes;
	public List<Node> getTrees() {
		return trees;
	}
	public void setTrees(List<Node> trees) {
		this.trees = trees;
	}
	public Attributes getAttributes() {
		return attributes;
	}
	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public String classify(String line){
		String[] row = line.split(",");
		int[] data = attributes.bucketRow(row);
		Iterator<Node> nodes = getTrees().iterator();
		Map<String, Integer> scores = new HashMap<String, Integer>();
		while(nodes.hasNext()){
			Node tree = nodes.next();
			String score = TreeUtilities.testRow(tree, data);
			if(!scores.containsKey(score)){
				scores.put(score, 1);
			}
			else{
				scores.put(score, scores.get(score)+1);
			}
		}
		int maxScoreCount = 0;
		String maxScore = "";
		Iterator<String> scoreIterator = scores.keySet().iterator();
		while(scoreIterator.hasNext()){
			String currentScore = scoreIterator.next();
			if(scores.get(currentScore) > maxScoreCount){
				maxScore = currentScore;
				maxScoreCount = scores.get(currentScore);
			}
		}
		
		return maxScore;
	}
	
	public static void serializeRandomForestToFile(String filePath, Forest forest) {

		try {
			FileOutputStream fout = new FileOutputStream(filePath);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(forest);
			oos.flush();
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Forest deserializeRandomForestFromFile(String filePath) {
		Forest randomForest = new Forest();
		try {
			FileInputStream fin = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fin);
			randomForest = (Forest) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return randomForest;
	}

	
}
