package cn.edu.cqupt.rubic_hadoop.config;

public class Attribute {

	private Integer sequence;
	private String name;
	private String type;
	private boolean isLabel;
	public Attribute() {
	}
	public Attribute(Integer seq,String name,String type,boolean isLabel) {
		this.sequence = seq;
		this.name = name;
		this.type = type;
		this.isLabel = isLabel;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isLabel() {
		return isLabel;
	}
	public void setLabel(boolean isLabel) {
		this.isLabel = isLabel;
	}
	


}
