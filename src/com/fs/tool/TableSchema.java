package com.fs.tool;

import java.text.Collator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

@SuppressWarnings({"serial","unchecked"})
public class TableSchema implements java.io.Serializable, Comparable<TableSchema>{
	
	private java.util.Map<String, ColumnSchema> columns;
	private java.util.Map<String, ColumnDifferSchema> columnsdiff = new LinkedHashMap<String, ColumnDifferSchema>();
	private String tablekind;
	private String tablename;

	public TableSchema() {
		super();
	}

	public TableSchema (java.util.Map<String, ColumnSchema> columns, java.util.Map<String, ColumnDifferSchema> columnsdiff, String tablename, String tablekind) {
		this.columns = columns;
		this.columnsdiff = columnsdiff;
		this.tablekind = tablekind;
		this.tablename = tablename;
	}
	public TableSchema(String tablename, java.util.Map<String, ColumnSchema> columns) {
		this.tablename = tablename;
		this.columns = columns;
		
	}

	public void addColumnsDiff(String column, ColumnDifferSchema dbdifferSchema) {
		this.columnsdiff.put(column, dbdifferSchema);
	}

	@Override
	public int compareTo(TableSchema o) {
		
		return Collator.getInstance().compare(getTablename(), o.getTablename());
	}
	
	public java.util.Map<String, ColumnSchema> getColumns() {
		return columns;
	}
	

	
	public java.util.Map<String, ColumnDifferSchema> getColumnsdiff() {
		return columnsdiff;
	}
	
	public String getTablekind() {
		return tablekind;
	}
	
	public String getTablename() {
		return tablename;
	}
	
	public void setColumns(java.util.Map<String, ColumnSchema> columns) {
		this.columns = columns;
	}
	
	public void setColumnsdiff(java.util.Map<String, ColumnDifferSchema> columnsdiff) {
		this.columnsdiff = columnsdiff;
	}
	
	public void setColumnsDiff(java.util.Map<String, ColumnDifferSchema> columnsdiff) {
		this.columnsdiff = columnsdiff;
	}
	
	public void setTablekind(String tablekind) {
		this.tablekind = tablekind;
	}
	
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	

	public org.json.simple.JSONObject toJSONObject() {
		org.json.simple.JSONObject json = new org.json.simple.JSONObject();
		for(java.util.Map.Entry<String, ColumnDifferSchema> dif : columnsdiff.entrySet()) {
			json.put(dif.getKey(), dif.getValue().toJSONObject());
		}
		return json;
		
	}

	@Override
	public String toString() {
		return super.toString()+"TableSchema [columns=" + columns + ", columnsdiff=" + columnsdiff + ", tablekind=" + tablekind
				+ ", tablename=" + tablename + "]";
	}

	public String toXML() {
		StringBuilder s = new StringBuilder();
		s.append("\t\t<columns>\n");
		for(Entry<String, ColumnDifferSchema> dif : columnsdiff.entrySet()) {	
			s.append("\t"+dif.getValue().toXML(dif.getKey()));
		}
		s.append("\t\t\t</columns>\n");	
		return s.toString();
	}
	
}
