package group2;

import java.util.Date;
import java.util.Iterator;




public class Main {
	public static void main(String[] args) {
		/*
		 * arg 0 input file
		 * arg 1 attribute metaData file
		 *
		 */
		int sequence = 0; //Which tree are we building, will be used in tempfile naming as well
		System.out.println(new Date());
		Main main = new Main();
		DataModel dataModel = new DataModel();
		dataModel.setAttributeMetaData(AttributeMetaData.readMetaDataFile(args[1]));
		dataModel.chooseColumns();
		dataModel.setoRows(Row.readFile(args[0], dataModel.getAttributeMetaData(), sequence)); //My computer 17 seconds to read the 10% data set and the metadata
		System.out.println(new Date() + " After reading data before bucket");
		dataModel.generateBuckets();
		dataModel.bucketData();
		System.out.println(new Date() + " After bucket Data");
		
		
		dataModel.setTestData(Row.readTestFile(args[0],dataModel.getAttributeMetaData(),sequence));
		

		System.out.println(new Date() + " After reading test data");
	}
	
	
	
}
