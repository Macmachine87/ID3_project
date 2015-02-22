package group2;


import group2.AttributeMetaData.AttributeType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;




public class Main {
	public static void main(String[] args) {
		/*
		 * arg 0 input file
		 * arg 1 attribute metaData file
		 *
		 */
		 Map<Integer,Node> bagOfTrees = new HashMap<Integer,Node>();
         
		int sequence = 0; //Which tree are we building, will be used in tempfile naming as well
                    Node root = new Node();
                    Tree t = new Tree();
                    System.out.println(new Date());
                    Main main = new Main();
                    DataModel dataModel = new DataModel();
                    dataModel.setAttributeMetaData(AttributeMetaData.readMetaDataFile(args[1]));
                    dataModel.chooseColumns();
                    dataModel.setoRows(Row.readFile(args[0], dataModel.getAttributeMetaData(), sequence)); //My computer 17 seconds to read the 10% data set and the metadata
                    System.out.println(new Date() + " After reading data before bucket");
                    dataModel.generateBuckets();
                    dataModel.bucketData(dataModel.getoRows());

                    System.out.println(new Date() + " After bucket Data ");
                    System.out.println(dataModel.getInUseAttributeMetaData());
                    Node tree = t.buildTree(root, dataModel.getoRows(), dataModel.getInUseAttributeMetaData());
                    bagOfTrees.put(sequence,tree);
                    System.out.println("Tree = " + tree.toString());
                    
                    dataModel.setTestData(Row.readTestFile(args[0],dataModel.getAttributeMetaData(),sequence));
                    dataModel.bucketData(dataModel.getTestData());
		System.out.println(new Date() + " After reading test data");

		List<Object[]> testResults = t.testData(tree, dataModel.getTestData());
		AttributeMetaData classificationAttribute = null;
	    Iterator<Integer> metaDataIterator = dataModel.getAttributeMetaData().keySet().iterator();
	    while(metaDataIterator.hasNext()){
	    	  AttributeMetaData metaData = dataModel.getAttributeMetaData().get(metaDataIterator.next());
	    	  if(metaData.getType().equals(AttributeType.classification)){
	    		  classificationAttribute = metaData;
	    	  }
	    }
	    long count = 0;
	    long wrongCount = 0;
	    
	    Iterator<Object[]> rows = testResults.iterator();
	    while(rows.hasNext()){
	    	Object[] row = rows.next();
	    	count++;
	    	if(!row[classificationAttribute.getId()].equals(row[classificationAttribute.getId()+1])){
	    		wrongCount++;
	    	}
	    	
	    }
	    System.out.println("Totoal = " + count + "  wrong " + wrongCount);

		
	}
        
        public void serializeBagToFile(String filePath,List<Node> bagOfTrees) {
            try {
                FileOutputStream fout = new FileOutputStream(filePath);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(bagOfTrees);
                oos.flush();
                fout.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        public ArrayList<Node> readBagFromFile(String filePath) {
            ArrayList<Node> bagOfTrees = new ArrayList<>();
            try {
                FileInputStream fin = new FileInputStream(filePath);
                ObjectInputStream ois = new ObjectInputStream(fin);
                
                bagOfTrees = (ArrayList<Node>) ois.readObject();
                ois.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
            return bagOfTrees;
        }
}
