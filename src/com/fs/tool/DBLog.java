package com.fs.tool;

import java.io.Serializable;
import java.util.Map.Entry;



import com.fs.dev.Console;

@SuppressWarnings("serial")
public class DBLog implements Serializable {
	private String result; 
	public DBLog() {
		super();
	}
	public DBLog(String dbsource, String dbtarget, java.util.LinkedList<TableSchema> unMatch) throws Exception {
		setResult(createLog(dbsource, dbtarget, unMatch, false));
		printLog();
	}
	public DBLog(String dbsource, String dbtarget, java.util.LinkedList<TableSchema> unMatch, Boolean tagtable) throws Exception {
		setResult(createLog(dbsource, dbtarget, unMatch, tagtable));		
	}
	public DBLog(String dbsource, String dbtarget, java.util.LinkedList<TableSchema> unMatch, String filename) throws Exception {
		setResult(createLog(dbsource, dbtarget, unMatch, false));
		save(filename);
	}
	public String createLog(String dbsource, String dbtarget, java.util.LinkedList<TableSchema> unMatch, Boolean tagtable) throws Exception  {	
		StringBuilder result = new StringBuilder();		
		
		if(tagtable) {
			result.append("<table id=\"datatable\" class=\"table table-bordered\">");
			result.append("<tr ><th class=\"text-center\" style=\"cursor: default;\">No.</th><th>");
			result.append(dbsource);
			result.append("</th class=\"text-center\" style=\"cursor: default;\"><th>");
			result.append(dbtarget);
			result.append("</th class=\"text-center\" style=\"cursor: default;\"></tr>");
		}else {
			result.append("\t"+dbsource+"\t\t\t\t\t"+dbtarget+" \n");
		}
		
		if(unMatch.size() != 0) {
			for(int i = 0;i < unMatch.size(); i++) {
				
				String tablename = unMatch.get(i).getTablename();
				Boolean found_source = false;
				Boolean found_target = false;
				
				if("S".equals(unMatch.get(i).getTablekind()) ||"A".equals(unMatch.get(i).getTablekind())) {
					
					if(tagtable) {
						result.append("<tr style=\" background-color: #bce2d5;\"> <td class=\"text-center\">");
						result.append(i+1);
						result.append("</td><td>");
						result.append(tablename);
						result.append("</td><td></td></tr>");
						
					}else {
						result.append(i+1+". "+tablename);
						
						
					}
					
					found_source = true;
				}
				else if("T".equals(unMatch.get(i).getTablekind())) {
					found_target = true;
					if(tagtable) {
						result.append("<tr style=\" background-color: #bce2d5;\"> <td class=\"text-center\">");
						result.append(i+1);
						result.append(".</td><td></td><td>");
						result.append(tablename);
						result.append("</td></tr>");
					}else {
						result.append(i+1+". " + "\t\t\t\t\t"+tablename);
					}
					
				}
				result.append("\n");
				if(found_source) {
					if(unMatch.get(i).getColumnsdiff().size() > 0) {
						for(java.util.Map.Entry<String, ColumnDifferSchema> cols :unMatch.get(i).getColumnsdiff().entrySet()) {
							if(tagtable) result.append("<tr>");
							if(cols.getValue().getSourceType() != 0) {
								if(tagtable) {
									result.append("<td></td> <td>");
									result.append(cols.getValue().serialize("s"));
									
									result.append("</td>");
									
								}else {
									if(cols.getValue().serialize("s") != "") {
										result.append(" "+cols.getValue().serialize("s"));
									}else {
										result.append("\t");
									}

								}
							}
							else {
								
								if(tagtable){
									result.append("<td></td><td></td>");
								}else {
									result.append("\t\t");
								}
								
							}
							if(cols.getValue().getTargetType() != 0) {
								if(tagtable) {
									result.append("<td>");
									result.append(cols.getValue().serialize("t"));
									result.append("</td></tr>");
								}else {
									if(cols.getValue().getSourceType() != 0) {
										if((" "+cols.getValue().serialize("s")).length() > 15) {
											result.append("\t\t\t " +cols.getValue().serialize("t")+"\n");
										}else {
											result.append("\t\t\t\t " +cols.getValue().serialize("t")+"\n");
										}
										
										
										
									}else {
										result.append("\t\t\t\t\t\t " +cols.getValue().serialize("t")+"\n");
										
									}
										

									
								}
								
								
							}else {
								if(tagtable) {
									result.append("<td></td></tr>");
								}else {
									result.append("\t\t\n");
								}
							}
							
						}
					}else {
						for(Entry<String, ColumnSchema> cols :unMatch.get(i).getColumns().entrySet()) {		
							if(tagtable) {
								result.append("<tr><td></td><td>");
								result.append(cols.getValue().serialize());
								
								result.append("</td></tr>");
							}else {
								result.append(" "+ cols.getValue().serialize()+"\n");
							}
						}
					}
				}
				if(found_target) {
					
					if(unMatch.get(i).getColumnsdiff().size() > 0) {
						for(java.util.Map.Entry<String, ColumnDifferSchema> cols :unMatch.get(i).getColumnsdiff().entrySet()) {
							if(tagtable) {
								result.append("<tr><td></td><td></td><td>");
								result.append(cols.getValue().serialize("t"));
								
								result.append("</td>");
							}else {
								result.append("\t\t\t\t\t " + cols.getValue().serialize("t"));
							}
							
						}
					} else {					
						for(Entry<String, ColumnSchema> cols :unMatch.get(i).getColumns().entrySet()) {	
							if(tagtable) {
								result.append("<tr><td></td><td></td><td>");	
								result.append(cols.getValue().serialize());
								
								result.append("</td></tr>");
							}else {
								result.append("\t\t\t\t\t " + cols.getValue().serialize()+"\n");
							}
							
						}
					}
					
				}
			}
		}
		else {
			result.append("\t ******* The database check is complete. database match *******");
		}
		if(tagtable) result.append("</table>");
		
		return result.toString();
	}
	
	public String getResult() {
		return result;
	}

	public void printLog() {

		Console.out.println(getResult());
	}

	public void save(java.io.OutputStream output) throws Exception {
		 try(java.io.Writer writer = new java.io.OutputStreamWriter(output)) {
			 writer.write(getResult());
		 }
	}
	
	public void save(String filename) throws Exception {
		 Console.out.println("saving as : "+filename);
		 try(java.io.FileOutputStream output = new java.io.FileOutputStream(filename)) {
			 save(output);
		 }
	}

	public void setResult(String result) {
		this.result = result;
	}
	
}
