package group2;

import group2.AttributeMetaData.AttributeType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * Store the data model
 * this includes a separate object for the attribute meta data and a list of the rows of data
 *  
 * @author Scott
 *
 */
public class DataModel {
	private List<Row> rows;
	private Map<Integer,AttributeMetaData> attributeMetaData;
	public List<Row> getRows() {
		return rows;
	}
	public void setRows(List<Row> rows) {
		this.rows = rows;
	}
	public Map<Integer, AttributeMetaData> getAttributeMetaData() {
		return attributeMetaData;
	}
	public void setAttributeMetaData(
			Map<Integer, AttributeMetaData> attributeMetaData) {
		this.attributeMetaData = attributeMetaData;
	}
	/*
	 * This process calculates the category for the continuous variables for all data in the set
	 * Could be rewritten to only do a subset of attributes that are of interest at the point of analysis
	 */
	public void bucketData(){
		calculateTotals();
		List<Integer> continuousCols = getContinuousKeys();
		Iterator<Row> rowIterator = rows.iterator();
		while(rowIterator.hasNext()){
			Row row = rowIterator.next();
			Map<Integer,Attribute> attributes = row.getAttributes();
			Iterator<Integer> j = continuousCols.iterator();
			while(j.hasNext()){
				int pos = j.next();
				Continuous attribute = (Continuous)attributes.get(pos);
				AttributeMetaData metaData = attributeMetaData.get(pos);
				if(attribute.getSourceValue()>=metaData.getSplitPoint()){
					attribute.setValue("1");
				}
				else{
					attribute.setValue("0");
				}
			}
		}
		
	}
	public void printArray(double[] data){
		String comma = "";
		for (double d : data){
			System.out.print(comma + d);
			comma = ",";
		}
		System.out.println("");
	}
	public List<Integer> getContinuousKeys(){
		//Get a list containing the index of all of the continuous attributes
		List<Integer> continuousCols = new ArrayList<Integer>();
		Iterator <Integer> metaDataKeys = attributeMetaData.keySet().iterator();
		while(metaDataKeys.hasNext()){
			Integer k = metaDataKeys.next();
			AttributeMetaData metaData = attributeMetaData.get(k);
			if(metaData.getType().equals(AttributeType.continuous))
				continuousCols.add(k);
	
		}
		return continuousCols;
	}
	private void calculateTotals(){
		List<Integer> continuousCols = getContinuousKeys();
		Iterator<Row> rowIterator = rows.iterator();
		while(rowIterator.hasNext()){
			Row row = rowIterator.next();
			Map<Integer,Attribute> attributes = row.getAttributes();
			Iterator<Integer> j = continuousCols.iterator();
			while(j.hasNext()){
				int pos = j.next();
				Continuous attribute = (Continuous)attributes.get(pos);
				double sourceValue = attribute.getSourceValue();
				if(sourceValue > 0.0){
					//totals[attribute.getId()] = totals[attribute.getId()] + attribute.getSourceValue();
					AttributeMetaData metaData = attributeMetaData.get(pos);
					metaData.setSumValue(metaData.getSumValue() + sourceValue);
					metaData.setNonZeroCount(metaData.getNonZeroCount() + 1);
				}
			}
		}
		Iterator<Integer> j = continuousCols.iterator();
		while(j.hasNext()){
			int pos = j.next();
			AttributeMetaData metaData = attributeMetaData.get(pos);
			if(metaData.getNonZeroCount() > 0){
				metaData.setSplitPoint(metaData.getSumValue()/metaData.getNonZeroCount());
				System.out.println(metaData.toString());
			}
		}
	}
}










