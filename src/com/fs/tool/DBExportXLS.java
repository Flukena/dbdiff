package com.fs.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import com.fs.dev.exim.ExcelWriter;

public class DBExportXLS {

	public DBExportXLS() {
		super();
	}
	
	public DBExportXLS(java.util.LinkedList<TableSchema> ts, DBConfig config, String filename) throws Exception {
		export(ts, config, filename);
	}
	
	public void export(java.util.LinkedList<TableSchema> ts, DBConfig config, String filename) throws Exception {
		
		ExcelWriter fs_writer = new ExcelWriter();
		fs_writer.setHeaderStyled(ExcelWriter.BASE_HEADER_STYLE_THIN);
		java.util.ArrayList<java.util.Map<String, String>> arraylist = new ArrayList<>();
		int count = 1;
		for (int i = 0 ; i < ts.size(); i++) {
			java.util.Map<String, String> tables = new java.util.LinkedHashMap<String, String>(); 
			String table = ts.get(i).getTablename();
			if(ts.get(i).getTablekind() == "S" || ts.get(i).getTablekind() == "A") {
				tables.put("db1", " "+count+". " + table);
			} else {
				tables.put("db1", "-");
			}
			if(ts.get(i).getTablekind() == "T") {
				tables.put("db2"," "+count+". " + table);
			} else {
				tables.put("db2", "-");
			}			
			arraylist.add(tables);
			if(ts.get(i).getColumnsdiff() != null) {
				for(java.util.Map.Entry<String, ColumnDifferSchema> s : ts.get(i).getColumnsdiff().entrySet()) {
					java.util.Map<String, String> columns = new java.util.LinkedHashMap<String, String>(); 
					String col1 = "-";
					String col2 = "-";
					if(s.getValue().getSourceType() != 0) {
						col1 = "     " + s.getKey() +" "+ DBUtils.getColumnType(s.getValue().getSourceType())+"("+s.getValue().getSourceLength()+ (s.getValue().getSourcePrecision() != 0 ? ","+s.getValue().getSourcePrecision():"" )+")";						
					}
					if(s.getValue().getTargetType() != 0) {
						col2 = "     " + s.getKey() +" "+ DBUtils.getColumnType(s.getValue().getTargetType())+"("+s.getValue().getTargetLength()+ (s.getValue().getTargetPrecision() != 0 ? ","+s.getValue().getTargetPrecision():"" )+")";						
					}
					columns.put("db1", col1);
					columns.put("db2", col2);				
					arraylist.add(columns);
				}
			}
			java.util.Map<String, String> space = new java.util.LinkedHashMap<String, String>(); 	
			space.put("db1", " ");
			space.put("db2", " ");
			count++;
			arraylist.add(space);
		}
		fs_writer.addHeader("db1", config.getSourceConfig().getDatabase());
		fs_writer.addHeader("db2", config.getTargetConfig().getDatabase());
		
		//String currentDir = System.getProperty("user.dir");
		//System.out.println("Excelfile current dir using System:" + currentDir);
		//File fs = new File(currentDir+"\\"+filename+".xlsx");
		File fs = new File(filename);
		try(FileOutputStream foutput = new FileOutputStream(filename)) {
			fs_writer.execute(foutput, fs.getName(), arraylist);
		}
	}
	
}
