package group2;

import group2.AttributeMetaData.AttributeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
	private List<Object[]> testData;
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
	public Map<Integer, AttributeMetaData> getInUseAttributeMetaData(){
		Iterator<Integer> i = attributeMetaData.keySet().iterator();
		Map<Integer,AttributeMetaData> inUse = new HashMap<Integer,AttributeMetaData>();
		while(i.hasNext()){
			AttributeMetaData metaData = attributeMetaData.get(i.next());
			if(metaData.isInUse()){
				inUse.put(metaData.getId(), metaData);
			}
		}
		return inUse;
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
		Iterator<Integer> j = attributeMetaData.keySet().iterator();
		int bucketCount = 10;
		while(j.hasNext()){
			int pos = j.next();
			AttributeMetaData metaData = attributeMetaData.get(pos);
			if(metaData.isInUse()){
				if(metaData.getType().equals(AttributeType.continuous)){
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
				else if(metaData.getType().equals(AttributeType.categorical)){
					Set<String> knownValueSet = metaData.getKnownValueSet();
					Map<String,Integer> knownValues = new HashMap<String,Integer>();
					Iterator<String> i = knownValueSet.iterator();
					int k = 0;
					while(i.hasNext()){
						String value = i.next();
						knownValues.put(value, k++);
					}
					knownValues.put("unknown", k);
					metaData.setKnownValues(knownValues);
				}
			}
		}
		
	}
	/**
	 * Place each continuous value into a predetermined bucket.
	 */
	public void bucketData(List<Object[]> data){
		Iterator<Object[]> rowIterator = data.iterator();
		while(rowIterator.hasNext()){
			Object[] row = rowIterator.next();
			Iterator<Integer> j = attributeMetaData.keySet().iterator();
			while(j.hasNext()){
				int pos = j.next();
				AttributeMetaData metaData = attributeMetaData.get(pos);
				if(metaData.isInUse()){
					if(metaData.getType().equals(AttributeType.continuous)){
						double[] buckets = metaData.getBuckets();
						double value = (Double)row[pos];
						boolean setBucket = false;
						int i = 0;
						while (!setBucket && i < buckets.length){
							if(value <= buckets[i]){
								row[pos] = i;
								setBucket=true;
							}
							i++;
						}
						if(!setBucket){
							//In the event we see a value that is larger than the previously determined max, put it in the max bucket
							//Would probably want to log this event 
							row[pos] = i-1;
						}
					}
					else if(metaData.getType().equals(AttributeType.categorical)){
						String value = (String) row[pos];
						int cat;
						if(metaData.getKnownValues().containsKey(value)){
							cat = metaData.getKnownValues().get(value);
						}
						else {
							cat = metaData.getKnownValues().get("unknown");
						}
						row[pos] = cat;
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
	public List<Object[]> getTestData() {
		return testData;
	}
	public void setTestData(List<Object[]> testData) {
		this.testData = testData;
	}
	
}










