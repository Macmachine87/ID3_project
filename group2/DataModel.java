package group2;

import group2.AttributeMetaData.AttributeType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * Store the data model
 * this includes a separate object for the attribute meta data and a list of the rows of data
 *  
 * @author Scott
 *
 */
public class DataModel {
	private List<Row> rows;
	private List<Object[]> oRows;
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
	public void chooseColumns(){
		int size = attributeMetaData.size()-1;  //Last column is the classification
		attributeMetaData.get(size).setInUse(true); //Always use the classification
		size--;
		//Round up from the square root of the number of columns to get the number of columns we will process
		double numCols = Math.ceil(Math.sqrt(size));
		int chosenCount = 0;
		List<Integer> used = new ArrayList<Integer>();
		while(chosenCount < numCols){
			int rand = DataModel.randInt(0, size);
			if(!attributeMetaData.get(rand).isInUse()){
				chosenCount++;
				attributeMetaData.get(rand).setInUse(true);
				used.add(rand);
			}
		}
		String t = ":";
	}
	/*
	 * Generate the buckets for the continuous columns we are using
	 */
	public void generateBuckets(){
		List<Integer> continuousCols = getContinuousKeys();
		Iterator<Integer> j = continuousCols.iterator();
		int bucketCount = 10;
		while(j.hasNext()){
			int pos = j.next();
			AttributeMetaData metaData = attributeMetaData.get(pos);
			if(metaData.isInUse()){
				double max = metaData.getMaxValue();
				double min = metaData.getMinValue();
				double span = max-min;
				double interval = span/bucketCount;
				double[] buckets = new double[bucketCount];
				buckets[0] = min+interval;
				for (int i = 1; i < bucketCount -1; i++){
					buckets[i] = buckets[i-1]+interval;
				}
				buckets[bucketCount-1] = max;
				metaData.setBuckets(buckets);
			}
		}
		
	}
	
	public void bucketData(){
		List<Integer> continuousCols = getContinuousKeys();
		Iterator<Object[]> rowIterator = oRows.iterator();
		while(rowIterator.hasNext()){
			Object[] row = rowIterator.next();
			Iterator<Integer> j = continuousCols.iterator();
			while(j.hasNext()){
				int pos = j.next();
				AttributeMetaData metaData = attributeMetaData.get(pos);
				if(metaData.isInUse()){
					double[] buckets = metaData.getBuckets();
					double value = (Double)row[pos];
					boolean setBucket = false;
					int i = 0;
					while (!setBucket){
						if(value <= buckets[i]){
							row[pos] = i;
							setBucket=true;
						}
						i++;
					}
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
	
	public List<Object[]> getoRows() {
		return oRows;
	}
	public void setoRows(List<Object[]> oRows) {
		this.oRows = oRows;
	}
	

	public static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		final Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		final int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}
	
}










