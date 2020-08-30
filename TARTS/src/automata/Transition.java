package automata;

import java.util.LinkedList;
import java.util.List;
/**
 * automaton transition object.
 * @author Torben Friedrich Goerner
 */
public class Transition {

	/**
	 * source location id
	 */
	private String source;
	/**
	 * target location id
	 */
	private String target;
	/**
	 * labels / actions of the transition
	 */
	private List<Label> labels;
	private List<Nail> nails;
	
	
	public Transition() {
		this.source = "";
		this.target = "";
		this.labels = new LinkedList<>();
		this.nails = new LinkedList<>();
	}
	
	@Override
	public String toString() {
		return "	<transition>\r\n" +
		"	<source ref=\"" + this.source + "\"/>\r\n" + 
		"	<target ref=\"" + this.target + "\"/>\r\n" +
		"	" + this.labels.stream().map(e -> e.toString()).reduce("", String::concat) +
		"	" + this.nails.stream().map(e -> e.toString()).reduce("", String::concat) +
		"	</transition>";
	}
	
	public void addLabel(Label label) {
		this.labels.add(label);
	}
	
	//------------Getter and Setter------------
	
	public String getSource() {return this.source;}
	public void setSource(String source) {this.source = source;}
	
	public String getTarget() {return this.target;}
	public void setTarget(String target) {this.target = target;}
	
	public List<Label> getlabels(){return this.labels;}
	public void setLabels(List<Label> labels) {this.labels = labels;}

	public List<Nail> getNails() {return nails;}
	public void setNail(Nail nail) {this.nails.add(nail);}

}
