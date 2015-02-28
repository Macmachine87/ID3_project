package group2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Store the data model this includes a separate object for the attribute meta
 * data and a list of the rows of data
 * 
 * @author Scott
 * 
 */
public class DataModel {
	private List<int[]> rows;
	private List<int[]> testData;
	private Attributes attributes;

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public Map<Integer, AttributeMetaData> getInUseAttributeMetaData() {
		Iterator<Integer> i = attributes.getAttributeMetaData().keySet().iterator();
		Map<Integer, AttributeMetaData> inUse = new HashMap<Integer, AttributeMetaData>();
		while (i.hasNext()) {
			AttributeMetaData metaData = attributes.getAttributeMetaData().get(i.next());
			if (metaData.isInUse()) {
				inUse.put(metaData.getId(), metaData);
			}
		}
		return inUse;
	}
	/**
	 * Place each value into a predetermined bucket represented by an Integer.
	 */


	public void printArray(double[] data) {
		String comma = "";
		for (double d : data) {
			System.out.print(comma + d);
			comma = ",";
		}
		System.out.println("");
	}

	public List<int[]> getRows() {
		return rows;
	}

	public void setRows(List<int[]> rows) {
		this.rows = rows;
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

	public List<int[]> getTestData() {
		return testData;
	}

	public void setTestData(List<int[]> testData) {
		this.testData = testData;
	}
}
