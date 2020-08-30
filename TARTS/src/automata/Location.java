package automata;
/**
 * automaton location object.
 * @author Torben Friedrich Goerner
 */
public class Location {

	/**
	 * id of the location
	 */
	private String id;
	/**
	 * name of the location
	 */
	private String name;
	/**
	 * flag for inital location / start location
	 */
	private boolean initial;
	private boolean urgent;
	private boolean committed;
	/**
	 * invariant label for the location
	 */
	private Label label;
	
	private int posX;
	private int posY;
	private String color;
	private int posXName;
	private int posYName;
	
	
	public Location() {
		this.id = "";
		this.name = "";
		this.initial = false;
		this.urgent = false;
		this.committed = false;
		this.label = null;
		this.posX = 0;
		this.posY = 0;
		this.posXName = 0;
		this.posYName = 0;
		this.color = "#0000ff";
	}
	
	@Override
	public String toString() {
		String label = this.label != null ? this.label.toString() + "\r\n" : "" ;
		String name = this.name.equals("") ? "" : "	<name x=\"" + this.posXName + "\" y=\"" + this.posYName + "\">" + this.name + "</name>\r\n";
		return "	<location id=\"" + this.id + "\" x=\"" + this.posX + "\" y=\"" + this.posY + "\" color=\"" + this.color + "\">\r\n" +
				name + label + "	</location>";
	}
	
	//-----------Getter and Setter-------------
	
	public String getId() {return this.id;}
	public void setId(String id) {this.id = id;}
	
	public String getName() {return name != null ? name : "";}
	public void setName(String name) {this.name = name;}
	
	public boolean isInitial() {return this.initial;}
	public void setInitial(boolean initial) {this.initial = initial;}
	
	public boolean isUrgent() {return this.urgent;}
	public void setUrgent(boolean urgent) {this.urgent = urgent;}
	
	public boolean isCommitted() {return this.committed;}
	public void setCommitted(boolean committed) {this.committed = committed;}

	public Label getLabel() {return this.label;}
	public void setLabel(Label label) {this.label = label;}

	public int getPosX() {return posX;}
	public void setPosX(int posX) {this.posX = posX;}

	public int getPosY() {return posY;}
	public void setPosY(int posY) {this.posY = posY;}

	public String getColor() {return color;}
	public void setColor(String color) {this.color = color;}

	public int getPosXName() {return posXName;}
	public void setPosXName(int posXName) {this.posXName = posXName;}

	public int getPosYName() {return posYName;}
	public void setPosYName(int posYName) {this.posYName = posYName;}

}
