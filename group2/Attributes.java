package group2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
/**
 * Container that handles the attributes
 * @author Scott
 *
 */
public class Attributes  implements java.io.Serializable{
	private Map<Integer, AttributeMetaData> attributeMetaData;
	private int classificationKey = -1;
	public Attributes(String fileName){
		attributeMetaData = Attributes.readMetaDataFile(fileName);
	}
	public Map<Integer, AttributeMetaData> getAttributeMetaData() {
		return attributeMetaData;
	}

	public void setAttributeMetaData(Map<Integer, AttributeMetaData> attributeMetaData) {
		this.attributeMetaData = attributeMetaData;
	}
	public int getClassificationKey(){
		if(classificationKey == -1){
			Iterator<Integer> iterator = attributeMetaData.keySet().iterator();
			while(iterator.hasNext()){
				AttributeMetaData metaData = attributeMetaData.get(iterator.next());
				if(metaData.getType().equals(AttributeType.classification)){
					classificationKey = metaData.getId();
				}
			}
		}
		return classificationKey;			
	}
	public AttributeMetaData getClassificationAttribute(){
		return attributeMetaData.get(getClassificationKey());
	}
	public static Map<Integer, AttributeMetaData> readMetaDataFile(String fileName) {
		BufferedReader reader = null;
		Map<Integer, AttributeMetaData> data = new HashMap<Integer, AttributeMetaData>();
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line = "";
			int lineNumber = 0;
			String classifications = reader.readLine(); 
			while ((line = reader.readLine()) != null) {
				if (line != null && line.length() > 0) {
					AttributeMetaData metaData = new AttributeMetaData();
					String[] row = line.split(":");
					metaData.setId(lineNumber);
					metaData.setName(row[0]);
					if (row[1].contains("symbolic")) {
						metaData.setType(AttributeType.categorical);
					} else {
						// continuous
						metaData.setType(AttributeType.continuous);
					}
					data.put(lineNumber++, metaData);
				}
			}
			AttributeMetaData classification = new AttributeMetaData();
			classification.setId(lineNumber);
			classification.setName("Classification");
			classification.setType(AttributeType.classification);
			data.put(lineNumber, classification);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
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

	/*
	 * Generate the buckets for the continuous columns we are using
	 */
	public void generateBuckets(String trainingFile) {
		//This call will populate the attributeMetaData object with unique values and the min max values for all data.
		List<Object[]> data = RowUtilities.readFileForBinning(trainingFile, this);
		Iterator<Integer> j = attributeMetaData.keySet().iterator();
		int bucketCount = 10;
		while (j.hasNext()) {
			int pos = j.next();
			AttributeMetaData metaData = attributeMetaData.get(pos);
			if (metaData.getType().equals(AttributeType.continuous)) {
				double max = metaData.getMaxValue();
				double min = metaData.getMinValue();
				double span = max - min;
				double interval = span / bucketCount;
				double[] buckets = new double[bucketCount];
				buckets[0] = min + interval;
				for (int i = 1; i < bucketCount - 1; i++) {
					buckets[i] = buckets[i - 1] + interval;
				}
				buckets[bucketCount - 1] = max;
				metaData.setBuckets(buckets);
			} else if (metaData.getType().equals(AttributeType.categorical) || metaData.getType().equals(AttributeType.classification)) {
				Set<String> knownValueSet = metaData.getKnownValueMap().keySet();
				Map<String, Integer> knownValues = new HashMap<String, Integer>();
				Map<Integer,String> reverseValues = new HashMap<Integer,String>();
				Iterator<String> i = knownValueSet.iterator();
				int k = 0;
				while (i.hasNext()) {
					String value = i.next();
					knownValues.put(value, k);
					reverseValues.put(k, value);
					k++;
				}
				knownValues.put("unknown", k);
				reverseValues.put(k, "unknown");
				metaData.setKnownValues(knownValues);
				metaData.setReverseValues(reverseValues);
			}
		}
	}

	public void clearSelectedAttributes(){
		clearSelectedAttributes(true);
	}
	public void clearSelectedAttributes(boolean leaveClassification){
		Iterator<Integer> iterator = attributeMetaData.keySet().iterator();
		while(iterator.hasNext()){
			AttributeMetaData metaData = attributeMetaData.get(iterator.next());
			if(!leaveClassification || (leaveClassification && !metaData.getType().equals(AttributeType.classification)))
				metaData.setInUse(false);
		}
	}
	public void setAllAttribuesAsUsed(){
		Iterator<Integer> iterator = attributeMetaData.keySet().iterator();
		while(iterator.hasNext()){
			AttributeMetaData metaData = attributeMetaData.get(iterator.next());
			metaData.setInUse(true);
		}
	}
	public String getInUseColumnNames(){
		String answer = "";
		Iterator<Integer> iterator = attributeMetaData.keySet().iterator();
		while(iterator.hasNext()){
			AttributeMetaData metaData = attributeMetaData.get(iterator.next());
			if(metaData.isInUse())
				answer += metaData.getName() + ", ";
		}
		return answer;
		
	}
	public void chooseColumns() {
		int size = attributeMetaData.size() - 1; // Last column is the
													// classification
		attributeMetaData.get(size).setInUse(true); // Always use the
													// classification
		size--;
		// Round up from the square root of the number of columns to get the
		// number of columns we will process
		double numCols = Math.ceil(Math.sqrt(size));
		int chosenCount = 0;
		List<Integer> used = new ArrayList<Integer>();
		while (chosenCount < numCols) {
			int rand = Attributes.randInt(0, size);
			if (!attributeMetaData.get(rand).isInUse()) {
				chosenCount++;
				attributeMetaData.get(rand).setInUse(true);
				used.add(rand);
			}
		}
	}
	public int[] bucketRow(Object[] row) {
		int[] newRow = new int[row.length+1];
		Iterator<Integer> j = this.getAttributeMetaData().keySet().iterator();
		while (j.hasNext()) {
			int pos = j.next();
			AttributeMetaData metaData = this.getAttributeMetaData().get(pos);
			if (metaData.isInUse()) {
				if (metaData.getType().equals(AttributeType.continuous)) {
					double[] buckets = metaData.getBuckets();
					double value = Double.parseDouble(row[pos].toString());
					boolean setBucket = false;
					int i = 0;
					while (!setBucket && i < buckets.length) {
						if (value <= buckets[i]) {
							newRow[pos] = i;
							setBucket = true;
						}
						i++;
					}
					if (!setBucket) {
						// In the event we see a value that is larger than
						// the previously determined max, put it in the max
						// bucket
						// Would probably want to log this event
						newRow[pos] = i - 1;
					}
				} else if (metaData.getType().equals(AttributeType.categorical)||metaData.getType().equals(AttributeType.classification)) {
					if(row.length > pos){
						String value = (String) row[pos];
						int cat;
						if (metaData.getKnownValues().containsKey(value)) {
							cat = metaData.getKnownValues().get(value);
						} else {
							cat = metaData.getKnownValues().get("unknown");
						}
						newRow[pos] = cat;
					}
				} else if (metaData.getType().equals(AttributeType.classification)) {
					String value = (String) row[pos];
					int cat;
					if (metaData.getKnownValueMap().containsKey(value)) {
						cat = metaData.getKnownValueMap().get(value);
					} else {
						cat = metaData.getKnownValueMap().get("unknown");
					}
					newRow[pos] = cat;
				}
			}
		}
		return newRow;
	}

	public Map<Integer, AttributeMetaData> getInUseAttributeMetaData() {
		Iterator<Integer> i = this.getAttributeMetaData().keySet().iterator();
		Map<Integer, AttributeMetaData> inUse = new HashMap<Integer, AttributeMetaData>();
		while (i.hasNext()) {
			AttributeMetaData metaData = this.getAttributeMetaData().get(i.next());
			if (metaData.isInUse()) {
				inUse.put(metaData.getId(), metaData);
			}
		}
		return inUse;
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
