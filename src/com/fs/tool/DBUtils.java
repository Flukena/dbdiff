package com.fs.tool;

import java.sql.Types;

public class DBUtils {
	public static String getColumnType(int type) {
		switch(type) {
			case Types.BIGINT: 
				return "BIGINT";
			case Types.DOUBLE: 
				return "Double";
			case Types.REAL: 
				return "Float";
			case Types.INTEGER:
				return "INT";
			case Types.SMALLINT:
				return "Smallint";
			case Types.TINYINT: 
				return "Tinyint";
			case Types.DECIMAL: 
				return "Decimal";
			case Types.NUMERIC: 
				return "Numeric";
			case Types.VARCHAR:
				return "Varchar";
			case Types.CHAR:
				return "Char";
			case Types.LONGVARCHAR:			
				return "TEXT";
			case -16 : //LONGNVARCHAR
				return "Longvarchar";
			case Types.LONGVARBINARY:
				return "BLOB";

			case Types.CLOB:
				return "CLOB";
			case 2011 : //NCLOB
				return "NLOB";
			case 2009 : 
				return "Clob";
			case Types.DATE: 
				return "Date";
			case Types.TIME: 
				return "Time";
			case Types.TIMESTAMP: 
				return "DATETIME";
			case Types.BIT : 
				return "BIT";
			case -15 : //NCHAR
				return "NCHAR";
			case -9  : //NVARCHAR
				return "NVARCHAR";
			case Types.BOOLEAN:
				return "Boolean";
			case Types.BINARY:
				return "BINARY";
			case Types.VARBINARY:
				return "VARBINARY";
			default: return "String";
		}
	}
	public static Boolean getColumnTypeCheck(int type) {
		switch(type) {
			case Types.CHAR: 
			case Types.VARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.NUMERIC:
			case Types.DECIMAL:
				return true;
			default: return false;
		}
	}
	public static java.io.File getResource(String filename) {
		if(filename==null || filename.trim().length()==0) {
			return null;
		}
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();	
		java.net.URL url = classLoader.getResource(filename);	
	    if(url==null) {
		    url = classLoader.getResource("/"+filename);	    
	    }
		if(url==null) {
			url = classLoader.getResource("META-INF/"+filename);
		}
		if(url==null) {
			url = classLoader.getResource("/META-INF/"+filename);
		}
		if(url!=null) {
			return new java.io.File(url.getFile());
		}
		return null;
	}
	public static boolean isNumericType(int type) {
		switch(type) {
			case Types.NUMERIC:
			case Types.DECIMAL:
				return true;
			default: return false;
		}		
	}	
}
