package com.fs.tool;

public class DBObjectSchema {
	java.util.Map<String, ColumnDifferSchema> columnDifferSchema;
	java.util.Map<String, ColumnSchema> source;
	public DBObjectSchema(java.util.Map<String, ColumnSchema> source, java.util.Map<String, ColumnDifferSchema> columnDifferSchema) {
		this.source = source;
		this.columnDifferSchema = columnDifferSchema;
	}
	public java.util.Map<String, ColumnDifferSchema> getColumnDifferSchema() {
		return columnDifferSchema;
	}
	public java.util.Map<String, ColumnSchema> getSource() {
		return source;
	}
	public void setColumnDifferSchema(java.util.Map<String, ColumnDifferSchema> columnDifferSchema) {
		this.columnDifferSchema = columnDifferSchema;
	}
	
	
	public void setSource(java.util.Map<String, ColumnSchema> source) {
		this.source = source;
	}
}
