package group2;



import group2.AttributeMetaData.AttributeType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * Represents a row of data from the source file
 * @author Scott
 *
 */
public class Row{
/*	private Map<Integer, Attribute> attributes = new HashMap<Integer,Attribute>();

	public Map<Integer, Attribute> getAttributes() {
		return attributes;
	}*/
	private Object[] arrayAttributes;
	
	public Object[] getArrayAttributes() {
		return arrayAttributes;
	}
	public void setArrayAttributes(Object[] arrayAttributes) {
		this.arrayAttributes = arrayAttributes;
	}	
	
	public static Object[] readRow(String row,  Map<Integer,AttributeMetaData> attributeMetaData, boolean addScore){
		Row newRow = new Row();
		String[] data = row.split(",");
		Object[] attributes = null;
		if(addScore){
			attributes = new Object[attributeMetaData.size()+1];
		}
		else 
			attributes = new Object[attributeMetaData.size()];
		/*
		 * Going to need a process to fetch the columns we are interested in each time. 
		 */
		for(int i = 0; i < data.length; i++){
			AttributeMetaData metaData = attributeMetaData.get(i);
			if (metaData.isInUse()){
				if(metaData.getType().equals(AttributeType.categorical)){
					/*Categorical attribute = new Categorical();
					attribute.setId(i);
					attribute.setValue(data[i]);
					newRow.getAttributes().put(i, attribute);
					attributes[i]= attribute;*/
					attributes[i]= data[i];
					
					if(metaData.getKnownValues() == null){
						//Training Data
						metaData.getKnownValueSet().add((String)data[i]);
					}
				}
				if(metaData.getType().equals(AttributeType.classification)){
					/*Classification attribute = new Classification();
					attribute.setId(i);
					attribute.setValue(data[i]);			
					newRow.getAttributes().put(i, attribute);
					attributes[i]= attribute;*/	
	
					attributes[i]= data[i];
				}
				if(metaData.getType().equals(AttributeType.continuous)){
					/*Continuous attribute = new Continuous();
					attribute.setId(i);
					attribute.setSourceValue(Double.parseDouble(data[i]));
					newRow.getAttributes().put(i, attribute);
					attributes[i]= attribute;
					*/
					double d = Double.parseDouble(data[i]);
					if(d > metaData.getMaxValue()){
						metaData.setMaxValue(d);
					}
					else if(d < metaData.getMinValue()){
						metaData.setMinValue(d);
					}

					attributes[i] = d;
				}
			}
		}
		//newRow.setArrayAttributes(attributes);
		return attributes;
	}
	
	/**
	 * Read in the dat file, split it into the training set and the test set. 1/3 for testing 2/3 for training
	 * Write the training set back to disk to fetch later in the testing phase
	 * @param fileName
	 * @param attributeMetaData
	 * @param sequence
	 * @return
	 */
	public static List<Object[]> readFile(String fileName, Map<Integer,AttributeMetaData> attributeMetaData, int sequence){
		BufferedReader reader = null;
		BufferedWriter writer = null;
		//List<Row> data = new ArrayList<Row>();
		List<Object[]> data = new ArrayList<Object[]>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)),20000);
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName+"_testData_" + sequence)));
			String line = "";
			double counter = 0;
			double counter2 = 0;
			while ((line = reader.readLine()) != null){
				if(line != null && line.length() > 0){
					int rand = DataModel.randInt(1, 3);
					
					/*
					 * This is vulnerable to missing out on some min and max values, but that will happen
					 * in real world running as well
					 */
					
					if(rand == 3){
						counter2++;
						//write to file, not going to use
						writer.write(line);
						writer.newLine();
					}
					else{
						data.add(Row.readRow(line, attributeMetaData,false));
						counter ++;
					}
				}
				else{
					
				}

		        if(counter%100000 == 0){
		        	System.out.println("read " + counter + " lines" + new Date());
		        }
			}
			System.out.println("Count in readFile = " + counter + " counter2 = " + counter2);
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
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}

	
	/**
	 * Read in the testing data
	 * @param fileName
	 * @param attributeMetaData
	 * @param sequence
	 * @return
	 */
	public static List<Object[]> readTestFile(String fileName, Map<Integer,AttributeMetaData> attributeMetaData, int sequence){
		BufferedReader reader = null;
		List<Object[]> data = new ArrayList<Object[]>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName+"_testData_" + sequence)),20000);
			String line = "";
			double counter = 0;
			double counter2 = 0;
			while ((line = reader.readLine()) != null){
				if(line != null && line.length() > 0){
						data.add(Row.readRow(line, attributeMetaData,true));
						counter ++;
				}
			
		        if(counter%100000 == 0){
		        	System.out.println("read " + counter + " lines" + new Date());
		        }
			}
			System.out.println("Count in readFile = " + counter + " counter2 = " + counter2);
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
		return data;
	}

	
}