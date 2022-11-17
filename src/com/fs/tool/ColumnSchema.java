package com.fs.tool;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

@SuppressWarnings({"serial","unchecked"})
public class ColumnSchema implements java.io.Serializable {
	public static final int COLUMN_EQUAL = 0;
	public static final int COLUMN_LENGTH = 4;
	public static final int COLUMN_PRECISION = 8;
	public static final int COLUMN_TYPE = 2;
	public static final int COLUMN_UNKNOWN = -1;
	private int check_colunm = COLUMN_UNKNOWN;
	private String columnname;
	private int datatype;
	private int length;
	private int precision;
	
	public ColumnSchema() {
		super();
	}	
	
	public ColumnSchema(ResultSet crs) throws SQLException {		
		fetchResult(crs);	
	}
	
	public ColumnSchema(ResultSet crs, Boolean bool) throws SQLException {
		this.columnname = crs.getString("columnname");
		this.datatype = crs.getInt("columntype");
		this.length = crs.getInt("columnlength");
		this.precision = crs.getInt("columnprecision");
	}
	
	public ColumnSchema(String columnname, int datatype, int length, int precision) {
		super();
		this.columnname = columnname;
		this.datatype = datatype;
		this.length = length;
		this.precision = precision;
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;
		this.check_colunm = COLUMN_UNKNOWN;
		if(object instanceof ColumnSchema) {
			ColumnSchema table = (ColumnSchema)object;
			if(getDatatype() == table.getDatatype()) {
				this.check_colunm = COLUMN_EQUAL;
				if(DBUtils.getColumnTypeCheck(getDatatype()) && getLength() != table.getLength()) {
					this.check_colunm += COLUMN_LENGTH;
				}
				if(DBUtils.isNumericType(getDatatype()) && getPrecision() != table.getPrecision()) {
					this.check_colunm += COLUMN_PRECISION;
				}
				if(this.check_colunm == COLUMN_EQUAL) {
					return true;
				}
			} else {
				this.check_colunm += COLUMN_TYPE;				
			}
			return false;
		}
		return false;
	}
	
	public void fetchResult(ResultSet crs) throws SQLException {
		this.columnname = crs.getString("COLUMN_NAME");
		this.datatype = crs.getInt("DATA_TYPE");
		this.length = crs.getInt("COLUMN_SIZE");
		this.precision = crs.getInt("DECIMAL_DIGITS");
	}	
	
	public int getCheckColunm() {
		return check_colunm;
	}

	public String getColumnname() {
		return columnname;
	}

	public int getDatatype() {
		return datatype;
	}

	public int getLength() {
		return length;
	}
	
	public int getPrecision() {
		return precision;
	}
	
	public String serialize() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getColumnname());
		sb.append(" ");
		sb.append(DBUtils.getColumnType(getDatatype()));
		switch(getDatatype()) {
			case java.sql.Types.VARCHAR:
			case java.sql.Types.CHAR: 
			case java.sql.Types.NCHAR:
			case java.sql.Types.NVARCHAR:
				sb.append("(").append(getLength()).append(")");
				break;
			case java.sql.Types.NUMERIC:
			case java.sql.Types.DECIMAL:
				sb.append("(").append(getLength());
				sb.append(",").append(getPrecision()).append(")");
				break;			
		}
		return sb.toString();
	}
	
	public void setCheckColunm(int check_colunm) {
		this.check_colunm = check_colunm;
	}
	
	public void setColumnname(String columnname) {
		this.columnname = columnname;
	}
	
	public void setDatatype(int datatype) {
		this.datatype = datatype;
	}
		
	public void setLength(int length) {
		this.length = length;
	}
	
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public JSONObject toJSONObject() {
		return toJSONObject(true);
	}
	
	public JSONObject toJSONObject(boolean showColumn) {		
		JSONObject json = new JSONObject();
		if(showColumn) json.put("columnname", this.columnname);
		json.put("type", this.datatype);
		json.put("length",this.length);
		json.put("precision", this.precision);		
		return json;
	}
	
	public String toJSONString() {
		return toJSONObject().toJSONString();
	}

	@Override
	public String toString() {
		return super.toString()+"ColumnSchema [check_colunm=" + check_colunm + ", columnname=" + columnname + ", datatype=" + datatype
				+ ", length=" + length + ", precision=" + precision + "]";
	}
	
	public String toXML() {
		return toXML(true);
	}

	public String toXML(boolean showColumn) {
		StringBuilder buf = new StringBuilder();
		if(showColumn) buf.append("<columnname>").append(getColumnname()).append("/columnname>");
		buf.append("<type>").append(this.datatype).append("</type>");
		buf.append("<length>").append(this.length).append("</length>");
		buf.append("<precision>").append(this.precision).append("</precision>");		
		return buf.toString();
	}	
}
