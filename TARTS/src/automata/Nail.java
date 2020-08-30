package automata;
/**
 * automaton nail object
 * @author Torben Friedrich Görner
 */
public class Nail {
	
	private int posX;
	private int posY;
	
	
	public Nail() {
		this.posX = 0;
		this.posY = 0;
	}
	
	@Override
	public String toString() {
		return "	<nail x=\"" + this.posX + "\" y=\"" + this.posY + "\"/>\r\n";
	}
	
	
	//---------------Getter and Setter--------------
	
	public int getPosX() {return posX;}
	public void setPosX(int posX) {this.posX = posX;}
	
	public int getPosY() {return posY;}
	public void setPosY(int posY) {this.posY = posY;}

}
