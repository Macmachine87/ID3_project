package group2;

import group2.AttributeMetaData.AttributeType;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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
	
	public static Object[] readRow(String row,  Map<Integer,AttributeMetaData> attributeMetaData){
		Row newRow = new Row();
		String[] data = row.split(",");
		Object[] attributes = new Object[attributeMetaData.size()];
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
	public static List<Object[]> readFile(String fileName, Map<Integer,AttributeMetaData> attributeMetaData){
		BufferedReader reader = null;
		//List<Row> data = new ArrayList<Row>();
		List<Object[]> data = new ArrayList<Object[]>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)),20000);
			String line = "";
			double counter = 0;
			double counter2 = 0;
			while ((line = reader.readLine()) != null){
				if(line != null && line.length() > 0){
					data.add(Row.readRow(line, attributeMetaData));
					counter ++;
				}
				else{
					counter2++;
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
/*	public static List<Row> readFile2(String fileName, Map<Integer,AttributeMetaData> attributeMetaData){
		BufferedReader reader = null;
		List<Row> data = new ArrayList<Row>();
		FileInputStream inputStream = null;
		Scanner sc = null;
		double counter = 0;
		try {
		    inputStream = new FileInputStream(fileName);
		    sc = new Scanner(inputStream, "UTF-8");
		    while (sc.hasNextLine()) {
		        String line = sc.nextLine();
		        // System.out.println(line);
		        if(line != null && line.length() > 0){
					data.add(Row.readRow(line, attributeMetaData));
					counter ++;
				}
		        if(counter%500000 == 0){
		        	System.out.println("read " + counter + " lines" + new Date());
		        }
		    }
		    // note that Scanner suppresses exceptions
		    if (sc.ioException() != null) {
		        throw sc.ioException();
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		finally {
		    if (inputStream != null) {
		        try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    if (sc != null) {
		        sc.close();
		    }
		}
		
		
		
		
		System.out.println("Count in readFile = " + counter );
		return data;
	}
	*/
}