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
		System.out.println(new Date());
		Main main = new Main();
		DataModel dataModel = new DataModel();
		dataModel.setAttributeMetaData(AttributeMetaData.readMetaDataFile(args[1]));
		dataModel.setRows(Row.readFile(args[0], dataModel.getAttributeMetaData())); //My computer 17 seconds to read the 10% data set and the metadata
		System.out.println(new Date() + " After reading data before bucket");
		dataModel.bucketData();
		System.out.println(new Date() + " After bucket Data");
		
		
		
	}
	
	
	
}
