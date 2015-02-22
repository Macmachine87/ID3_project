package group2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AttributeMetaData{
	private int id; //Sequence
	private String name;
	private Map<String, Integer> knownValues;  
	private Set<String> knownValueSet = new HashSet<String>();
	private AttributeType type;
	private double nonZeroCount = 0;
	private double sumValue = 0;
	private double splitPoint;
	private double maxValue;
	private double minValue;
	private boolean inUse = false; //Is this an active attribute for this tree
	private double[] buckets;
	public enum AttributeType{classification, categorical, continuous}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public AttributeType getType() {
		return type;
	}
	public void setType(AttributeType type) {
		this.type = type;
	} 
	public double getSplitPoint() {
		return splitPoint;
	}
	public void setSplitPoint(double splitPoint) {
		this.splitPoint = splitPoint;
	}
	public double getNonZeroCount() {
		return nonZeroCount;
	}
	public void setNonZeroCount(double nonZeroCount) {
		this.nonZeroCount = nonZeroCount;
	}
	public double getSumValue() {
		return sumValue;
	}
	public void setSumValue(double sumValue) {
		this.sumValue = sumValue;
	}
	public boolean isInUse() {
		return inUse;
	}
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	public String toString(){
		String string = "META:" + getName() + " " + getId() + " " + getType();
		if(type.equals(AttributeType.continuous)){
			string += " total " + sumValue + " nonZeroCount " + nonZeroCount + " split " + splitPoint + " max " + maxValue +  " min " + minValue;
		}
		if(isInUse()){
			string += " in use ";
		}
		else{
			string += " not in use ";
		}
                System.out.println(string);
		return string;
	}
	public static Map<Integer, AttributeMetaData> readMetaDataFile(String fileName){
		BufferedReader reader = null;
		Map<Integer,AttributeMetaData> data = new HashMap<Integer, AttributeMetaData>();
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line = "";
			int lineNumber = 0;
			String classifications = reader.readLine(); //Throwing away the classifications at this point, may want them later
			while ((line = reader.readLine()) != null){
				if(line != null && line.length() > 0){
					AttributeMetaData metaData = new AttributeMetaData();
					String[] row = line.split(":");
					metaData.setId(lineNumber);
					metaData.setName(row[0]);
					if(row[1].contains("symbolic")){
						metaData.setType(AttributeType.categorical);
					}
					else{
						//continuous
						metaData.setType(AttributeType.continuous);
					}
					data.put(lineNumber++, metaData);
				}
			}

			AttributeMetaData classification = new AttributeMetaData();
			classification.setId(lineNumber);
			classification.setName("Classification");
			classification.setType(AttributeType.classification);
//			classification.setKnownValues(classifications.split(","));
			data.put(lineNumber, classification);
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
	public double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	public double getMinValue() {
		return minValue;
	}
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}
	public double[] getBuckets() {
		return buckets;
	}
	public void setBuckets(double[] buckets) {
		this.buckets = buckets;
	}
	public Set getKnownValueSet() {
		return knownValueSet;
	}
	public void setKnownValueSet(Set knownValueSet) {
		this.knownValueSet = knownValueSet;
	}
	public Map<String, Integer> getKnownValues() {
		return knownValues;
	}
	public void setKnownValues(Map<String, Integer> knownValues) {
		this.knownValues = knownValues;
	}

}