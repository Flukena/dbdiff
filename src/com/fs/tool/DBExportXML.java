package com.fs.tool;

import java.io.Serializable;
import com.fs.dev.Console;

@SuppressWarnings("serial")
public class DBExportXML implements Serializable {
	
	public DBExportXML() {
		super();
	}
	
	public void ExportXML(java.util.LinkedList<TableSchema> ts, String filename) throws Exception {
		StringBuilder result = new StringBuilder();
		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		result.append("<tables>");		
		for(int i = 0;i <  ts.size(); i++) {			
			result.append("\t<" + ts.get(i).getTablename() + ">\n");
			result.append(ts.get(i).toXML() );
			result.append("\t</" + ts.get(i).getTablename() + ">\n");
		}
		result.append("</tables>");
		save(result.toString(), filename);
	}

	public void save(String xml, java.io.OutputStream output) throws Exception {
		try(java.io.Writer file = new java.io.OutputStreamWriter(output)) {
			file.write(xml);
			file.flush();
		}
	}
	
	public void save(String xml, String filename)  throws Exception {
		Console.out.println("saving as : "+filename);
		try(java.io.FileOutputStream fos = new java.io.FileOutputStream(filename)) {
			save(xml,fos);
		}
	}
	
}
