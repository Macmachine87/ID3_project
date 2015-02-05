package com.boeing.advBigData.decisionTree;

import java.io.IOException;
import java.io.Writer;

/**
 * Data object to represent the Iris data that also contains static functions to deal with data specific actions.
 * @author Scott
 *
 */
	public class IrisRow {
		private float sepalLength;
		private float sepalWidth;
		private float petalLength;
		private float petalWidth;
		private String species;
		private String calculatedSpecies;
		
		/*
		 * The way we need to process the data iteratively presenting it as an array seems to provide the simplest interface.
		 */
		public static final int sepalLength_i = 0;
		public static final int sepelWidth_i = 1;
		public static final int petalLength_i = 2;
		public static final int petalWidth_i = 3;
		
		private int[] attributes = new int[4];
		public IrisRow(){
			
		}
		/*
		 * Copy Constructor
		 */
		public IrisRow (IrisRow copyRow){
			this.setSepalLength(copyRow.getSepalLength());
			this.setSepalWidth(copyRow.getSepalWidth());
			this.setPetalLength(copyRow.getPetalLength());
			this.setPetalWidth(copyRow.getPetalWidth());
			this.setSpecies(copyRow.getSpecies());
			int copyAttributes[] = copyRow.getAttributes();
			for(int i = 0; i < copyAttributes.length; i++){
				attributes[i] = copyAttributes[i];
			}
		}
		
		public float getSepalLength() {
			return sepalLength;
		}
		public void setSepalLength(float sepalLength) {
			this.sepalLength = sepalLength;
		}
		public void setSepalLengthInt(int length){
			attributes[sepalLength_i] = length;
		}
		public float getSepalWidth() {
			return sepalWidth;
		}
		public void setSepalWidth(float sepalWidth) {
			this.sepalWidth = sepalWidth;
		}
		public void setSepalWidthInt(int width){
			attributes[sepelWidth_i] = width;
		}
		public float getPetalLength() {
			return petalLength;
		}
		public void setPetalLength(float petalLength) {
			this.petalLength = petalLength;
		}
		public void setPetalLengthInt(int length){
			attributes[petalLength_i]=length;
		}
		public float getPetalWidth() {
			return petalWidth;
		}
		public void setPetalWidth(float petalWidth) {
			this.petalWidth = petalWidth;
		}
		public void setPetalWidthInt(int width){
			attributes[petalWidth_i] = width;
		}
		public String getSpecies() {
			return species;
		}
		public void setSpecies(String species) {
			this.species = species;
		}
		public String getCalculatedSpecies() {
			return calculatedSpecies;
		}
		public void setCalculatedSpecies(String calculatedSpecies) {
			this.calculatedSpecies = calculatedSpecies;
		}
		/**
		 * Static function to read data into an IrisRow object
		 * @param row
		 * @return
		 */
		public static IrisRow readIrisRow(String row){
			String[] data = row.split(",");
			IrisRow iris = new IrisRow();
			iris.setSepalLength(Float.parseFloat(data[0]));
			iris.setSepalWidth(Float.parseFloat(data[1]));
			iris.setPetalLength(Float.parseFloat(data[2]));
			iris.setPetalWidth(Float.parseFloat(data[3]));
			iris.setSpecies(data[4]);
			return iris;
		}
		/**
		 * Static function to print the result data for the calculation for this row.
		 * @param iris
		 * @param writer
		 * @throws IOException
		 */
		public static void writeIrisRowResult(IrisRow iris, Writer writer) throws IOException{
			writer.append(iris.getSpecies());
			writer.append(",");
			writer.append(iris.getCalculatedSpecies());
			writer.append("\n");
		}
		public int[] getAttributes() {
			return attributes;
		}
		public String toString(){
			return getSpecies() + " SW " + getSepalWidth() + " SL " + getSepalLength() + " PW " + getPetalWidth() + " PL " + getPetalWidth();
		}
		
	}
