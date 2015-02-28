package group2;

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
 * 
 * @author Scott
 * 
 */
public class Row {
	/*
	 * private Map<Integer, Attribute> attributes = new
	 * HashMap<Integer,Attribute>(); public Map<Integer, Attribute>
	 * getAttributes() { return attributes; }
	 */
	private Object[] arrayAttributes;

	public static enum ReadType {
		allRows, trainingRows, testingRows
	};

	public Object[] getArrayAttributes() {
		return arrayAttributes;
	}

	public void setArrayAttributes(Object[] arrayAttributes) {
		this.arrayAttributes = arrayAttributes;
	}

	public static int[] readRow(String row, Attributes attributeMetaData) {
		String[] data = row.split(",");
		int[] attributes = new int[attributeMetaData.getAttributeMetaData().size() + 1];
		/*
		 * Going to need a process to fetch the columns we are interested in
		 * each time.
		 */
			attributes = attributeMetaData.bucketRow(data);
		// newRow.setArrayAttributes(attributes);
		return attributes;
	}

	/*
	 * This will collect all the data required to build the data bins
	 */
	public static Object[] readRowForBinning(String row, Attributes attributeMetaData) {
		String[] data = row.split(",");
		Object[] attributes = null;
		attributes = new Object[attributeMetaData.getAttributeMetaData().size()];
		
		for (int i = 0; i < data.length; i++) {
			AttributeMetaData metaData = attributeMetaData.getAttributeMetaData().get(i);
			if (metaData.getType().equals(AttributeType.categorical) || metaData.getType().equals(AttributeType.classification)) {
				//Only need to collect the unique values, dont need to store each row.
				Map<String, Integer> knownValues = metaData.getKnownValueMap();
				String value = (String) data[i];
				if(!knownValues.containsKey(value)){
					knownValues.put(value,0);
				}
				knownValues.put(value, knownValues.get(value) + 1);
			}
			if (metaData.getType().equals(AttributeType.classification)) {
				attributes[i] = data[i];//Dont know if I need the classification for binning
			}
			if (metaData.getType().equals(AttributeType.continuous)) {
				double d = Double.parseDouble(data[i]);
				if (d > metaData.getMaxValue()) {
					metaData.setMaxValue(d);
				} else if (d < metaData.getMinValue()) {
					metaData.setMinValue(d);
				}
				//At this time we are only capturing the min and max values, if we want the full dataset to do more sophisticated binning we would capture the continuous values here.
				//attributes[i] = d;
			}
		}
		return attributes;
	}

	/**
	 * Read in the testing data
	 * 
	 * @param fileName
	 * @param attributeMetaData
	 * @param sequence
	 * @return
	 */
	public static List<Object[]> readFileForBinning(String fileName, Attributes attributes) {
		BufferedReader reader = null;
		List<Object[]> data = new ArrayList<Object[]>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)), 20000);
			String line = "";
			double counter = 0;
			while ((line = reader.readLine()) != null) {
				if (line != null && line.length() > 0) {
					data.add(Row.readRowForBinning(line, attributes));
					counter++;
				}
				/*if (counter % 100000 == 0) {
					 System.out.println("read binning run " + counter + " lines" + new Date());
				}*/
			}
			System.out.println("Count in binning file = " + counter );
			// + " counter2 = " + counter2);
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

	/**
	 * Read in the dat file, split it into the training set and the test set.
	 * 1/3 for testing 2/3 for training Write the training set back to disk to
	 * fetch later in the testing phase
	 * 
	 * @param fileName
	 * @param attributeMetaData
	 * @param sequence
	 * @return
	 */
	public static List<int[]> readFileForTraining(String fileName, Attributes attributes, int sequence) {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		List<int[]> data = new ArrayList<int[]>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)), 20000);
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName + "_testData_" + sequence)));
			String line = "";
			double counter = 0;
			while ((line = reader.readLine()) != null) {
				int[] row = null;
				if (line != null && line.length() > 0) {
					int rand = DataModel.randInt(1, 3);
					if (rand == 3) {
						// write to file, not going to use until testing
						writer.write(line);
						writer.newLine();
					} else {
						row = Row.readRow(line, attributes);
						data.add(row);
						counter++;
					}
				} else {
				}
				if (counter % 100000 == 0) {
//					 System.out.println("read training data " + counter + " lines"  + new Date());
				}
			}
			// System.out.println("Count in readFile = " + counter
			// + " counter2 = " + counter2);
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
	 * 
	 * @param fileName
	 * @param attributeMetaData
	 * @param sequence
	 * @return
	 */
	public static List<int[]> readTestFile(String fileName, Attributes attributeMetaData, int sequence) {
		BufferedReader reader = null;
		List<int[]> data = new ArrayList<int[]>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName + "_testData_" + sequence)), 20000);
			String line = "";
			double counter = 0;
			double counter2 = 0;
			while ((line = reader.readLine()) != null) {
				if (line != null && line.length() > 0) {
					data.add(Row.readRow(line, attributeMetaData));
					counter++;
				}
				/*if (counter % 100000 == 0) {
					System.out.println("read test file " + sequence + " " + counter + " lines" + new Date());
				}*/
			}
			// System.out.println("Count in readFile = " + counter
			// + " counter2 = " + counter2);
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
}