package automata;

/**
 * Query object for a UPPAAL verification.
 * @author Torben Friedrich Goerner
 */
public class Query {

	private String formula;
	private String comment;
	
	public Query() {
		this.setFormula("");
		this.setComment("");
	}
	
	
	public String toString() {
		return "\t<query>\r\n" + 
				"\t\t<formula>" + this.formula.replaceAll("\n", "").replaceAll("\r", "") + "</formula>\r\n" + 
				"\t\t<comment>" + this.comment.replaceAll("\n", "").replaceAll("\r", "") + "</comment>\r\n" + 
				"\t</query>\r\n";
	}
	
	//-------------------------Getter and Setter----------------------------
	
	public String getFormula() {return formula;}
	public void setFormula(String formula) {this.formula = formula;}

	public String getComment() {return comment;}
	public void setComment(String comment) {this.comment = comment;}
	
}
