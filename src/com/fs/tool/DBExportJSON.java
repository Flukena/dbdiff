package com.fs.tool;

import java.io.Serializable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.fs.dev.Console;

@SuppressWarnings({"serial","unchecked"})
public class DBExportJSON implements Serializable {
	public DBExportJSON() {
		super();
	}
	
	public JSONArray ExportJSON(java.util.LinkedList<TableSchema> ts, String filename) throws Exception {
		JSONArray result =  new JSONArray();		
		for(int i = 0;i <  ts.size(); i++) {
			JSONObject temp = new JSONObject();
			temp.put(ts.get(i).getTablename(), ts.get(i).toJSONObject());
			result.add(temp);
		}		
		save(result, filename);		
		return result; 
	}
	
	public void save(JSONArray json, java.io.OutputStream output) throws Exception {
		try(java.io.Writer file = new java.io.OutputStreamWriter(output)) {
			file.write(json.toJSONString());
			file.flush();
		}
	}

	public void save(JSONArray json, String filename) throws Exception {
		Console.out.println("saving as : "+filename);
		try(java.io.FileOutputStream fos = new java.io.FileOutputStream(filename)) {
			save(json,fos);
		}
	}
	
}
