package automata;
/**
 * Label object
 * @author Torben Friedrich Goerner
 */
public class Label {

	/**
	 * kind of the label
	 */
	private String kind;
	/**
	 * label content
	 */
	private String content;
	
	private int posX;
	private int posY;
	
	
	public Label() {
		this.kind = "";
		this.content = "";
		this.setPosX(0);
		this.setPosY(0);
	}
	
	@Override
	public String toString() {
		String contentXML = content.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		return "	<label kind=\"" + this.kind + "\" x=\"" + this.posX + "\" y=\"" + this.posY + "\">" + contentXML + "</label>\r\n";
	}
	
	//--------------Getter and Setter-------------
	
	public String getKind() {return this.kind;}
	public void setKind(String kind) {this.kind = kind;}
	
	public String getContent() {return this.content;}
	public void setContent(String content) {this.content = content;}

	public int getPosX() {return posX;}
	public void setPosX(int posX) {this.posX = posX;}

	public int getPosY() {return posY;}
	public void setPosY(int posY) {this.posY = posY;}

}
