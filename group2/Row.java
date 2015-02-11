package group2;

import group2.AttributeMetaData.AttributeType;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Represents a row of data from the source file
 * @author Scott
 *
 */
public class Row{
	private Map<Integer, Attribute> attributes = new HashMap<Integer,Attribute>();

	public Map<Integer, Attribute> getAttributes() {
		return attributes;
	}
	
	
	public static Row readRow(String row,  Map<Integer,AttributeMetaData> attributeMetaData){
		Row newRow = new Row();
		String[] data = row.split(",");
		for(int i = 0; i < data.length; i++){
			AttributeMetaData metaData = attributeMetaData.get(i);
			if(metaData.getType().equals(AttributeType.categorical)){
				Categorical attribute = new Categorical();
				attribute.setId(i);
				attribute.setValue(data[i]);
				newRow.getAttributes().put(i, attribute);
			}
			if(metaData.getType().equals(AttributeType.classification)){
				Classification attribute = new Classification();
				attribute.setId(i);
				attribute.setValue(data[i]);			
				newRow.getAttributes().put(i, attribute);	
			}
			if(metaData.getType().equals(AttributeType.continuous)){
				Continuous attribute = new Continuous();
				attribute.setId(i);
				attribute.setSourceValue(Double.parseDouble(data[i]));
				newRow.getAttributes().put(i, attribute);
			}
		}
		
		return newRow;
	}
	public static List<Row> readFile(String fileName, Map<Integer,AttributeMetaData> attributeMetaData){
		BufferedReader reader = null;
		List<Row> data = new ArrayList<Row>();
		try {
			reader = new BufferedReader(new FileReader(fileName));
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