package group2;
/**
 * Root attribute type
 * @author Scott
 *
 */
@Deprecated
public class Attribute{
	private int id; //attribute sequence in the file
	private String value; //This is the value that will be used to split on in the tree
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}