package com.fs.tool;

import org.json.simple.JSONObject;

@SuppressWarnings({"serial","unchecked"})
public class ColumnDifferSchema implements java.io.Serializable  {
	private Boolean lengthflag = false;
	private Boolean precisionflag = false;
	private ColumnSchema source;
	private ColumnSchema target;
	private Boolean typeflag = false;
	
	public ColumnDifferSchema() {
		super();
	}

	public ColumnDifferSchema(ColumnSchema source, ColumnSchema target) {
		this.source = source;
		this.target = target;		
		differSchemaFlag(source,target);
	}
	public ColumnDifferSchema(ColumnSchema source, ColumnSchema target, Boolean lengthflag, Boolean precisionflag, Boolean typeflag) {
		this.lengthflag = lengthflag;
		this.precisionflag = precisionflag;
		this.typeflag = typeflag;
		
		this.source = source;
		this.target = target;		

	}

	public void differSchemaFlag(ColumnSchema source, ColumnSchema target) {		
		if(source != null && target!=null) {
			source.equals(target);
			int checker = source.getCheckColunm();
			if(checker == ColumnSchema.COLUMN_TYPE || checker==ColumnSchema.COLUMN_TYPE+ColumnSchema.COLUMN_LENGTH || checker==ColumnSchema.COLUMN_TYPE+ColumnSchema.COLUMN_PRECISION  || checker==ColumnSchema.COLUMN_TYPE+ColumnSchema.COLUMN_LENGTH+ColumnSchema.COLUMN_PRECISION) {
				setTypeflag(true);				
			}
			if(checker == ColumnSchema.COLUMN_LENGTH || checker==ColumnSchema.COLUMN_LENGTH+ColumnSchema.COLUMN_PRECISION || checker==ColumnSchema.COLUMN_LENGTH+ColumnSchema.COLUMN_TYPE || checker==ColumnSchema.COLUMN_TYPE+ColumnSchema.COLUMN_LENGTH+ColumnSchema.COLUMN_PRECISION) {
				setLengthflag(true);				
			}
			if(checker == ColumnSchema.COLUMN_PRECISION || checker==ColumnSchema.COLUMN_PRECISION+ColumnSchema.COLUMN_TYPE || checker==ColumnSchema.COLUMN_PRECISION+ColumnSchema.COLUMN_LENGTH || checker==ColumnSchema.COLUMN_TYPE+ColumnSchema.COLUMN_LENGTH+ColumnSchema.COLUMN_PRECISION) {
				setPrecisionflag(true);				
			}
		} else {
			setTypeflag(true);				
			setLengthflag(true);
			setPrecisionflag(true);
		}
	}

	public Boolean getLengthflag() {
		return lengthflag;
	}

	public Boolean getPrecisionflag() {
		return precisionflag;
	}

	public ColumnSchema getSource() {
		return source;
	}
	
	public int getSourceLength() {
		return getSource()==null?-1:getSource().getLength();
	}
	
	public int getSourcePrecision() {
		return getSource()==null?-1:getSource().getPrecision();
	}

	public int getSourceType() {
		return getSource()==null?-1:getSource().getDatatype();
	}

	public ColumnSchema getTarget() {
		return target;
	}

	public int getTargetLength() {
		return getTarget()==null?-1:getTarget().getLength();
	}

	public int getTargetPrecision() {
		return getTarget()==null?-1:getTarget().getPrecision();
	}

	public int getTargetType() {
		return getTarget()==null?-1:getTarget().getDatatype();
	}

	public Boolean getTypeflag() {
		return typeflag;
	}
	
	public String serialize(String type) {
		return "s".equalsIgnoreCase(type)?serializeSource():serializeTarget();
	}
	
	public String serializeSource() {
		return getSource()==null?"":getSource().serialize();
	}
	
	public String serializeTarget() {
		return getTarget()==null?"":getTarget().serialize();
	}
		
	public void setLengthflag(Boolean lengthflag) {
		this.lengthflag = lengthflag;
	}

	public void setPrecisionflag(Boolean precisionflag) {
		this.precisionflag = precisionflag;
	}

	public void setSource(ColumnSchema source) {
		this.source = source;
	}

	public void setTarget(ColumnSchema target) {
		this.target = target;
	}

	public void setTypeflag(Boolean typeflag) {
		this.typeflag = typeflag;
	}
	
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		json.put("source", getSource()==null?null:getSource().toJSONObject());
		json.put("targete", getTarget()==null?null:getTarget().toJSONObject());
		json.put("typeflag", typeflag);
		json.put("lenghtflag", lengthflag);
		json.put("precisionflag", precisionflag);
		return json;
	}
	
	@Override
	public String toString() {
		return super.toString()+"ColumnDifferSchema [lengthflag=" + lengthflag + ", precisionflag=" + precisionflag + ", typeflag="
				+ typeflag + ", source=" + source + ", target=" + target + "]";
	}
	
	public String toXML(String columnname) {
		StringBuilder s = new StringBuilder();		
		s.append("\t\t\t<").append(columnname).append(">\n");
		s.append("\t\t\t\t<source>").append(getSource().toXML()).append("</source> \n");
		s.append("\t\t\t\t<target>").append(getTarget().toXML()).append("</target> \n");
		s.append("\t\t\t\t<lengthflag>").append(getLengthflag()).append("</lengthflag> \n");
		s.append("\t\t\t\t<precisionflag>").append(getPrecisionflag()).append("</precisionflag> \n");
		s.append("\t\t\t\t<typeflag>").append(getTypeflag()).append("</typeflag> \n");
		s.append("\t\t\t</").append(columnname).append(">\n");
		return s.toString();
	}
	
}
