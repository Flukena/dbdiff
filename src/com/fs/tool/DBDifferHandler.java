package com.fs.tool;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;


import com.fs.bean.misc.DBConnection;
import com.fs.bean.misc.KnSQL;
import java.sql.DatabaseMetaData;

@SuppressWarnings("serial")
public class DBDifferHandler implements java.io.Serializable {
	
	protected DBConfig config = null;
	protected java.util.LinkedList<TableSchema> databaseUnMatch = new java.util.LinkedList<TableSchema>();


	protected String journalid ="";

	public void compareDatabase(java.util.Map<String, TableSchema> source, java.util.Map<String, TableSchema> target) throws Exception {
		java.util.List<String> nondiff =  new LinkedList<String>();
		java.util.List<String> diff =  new LinkedList<String>();
		java.util.Map<String, TableSchema> stemp = new java.util.LinkedHashMap<>();
		stemp.putAll(target);
		setJournalid(java.util.UUID.randomUUID().toString());
		for (java.util.Map.Entry<String, TableSchema> fentry : source.entrySet()) {
			String fkey = fentry.getKey();
			TableSchema targetTables = target.get(fkey);
			TableSchema sourceTables = source.get(fkey);
			if(targetTables!=null) {
				Boolean compare = compareTable(sourceTables, targetTables, fkey);
				
				if(compare) {
					nondiff.add(fkey);
					stemp.remove(fkey);
				} else {
					stemp.remove(fkey);
					diff.add(fkey);					
					sourceTables.setTablekind("A");
					this.databaseUnMatch.add(sourceTables);
				}				
			} else {				
				diff.add(fkey);
				stemp.remove(fkey);
				sourceTables.setTablekind("S");
				this.databaseUnMatch.add(sourceTables);
			}					
		}
		for (java.util.Map.Entry<String, TableSchema> stempentry : stemp.entrySet()) {
			diff.add(stempentry.getKey());			
			TableSchema entry = stempentry.getValue();
			entry.setTablekind("T");
			this.databaseUnMatch.add(entry);
			
		}
		
		java.util.Collections.sort(this.databaseUnMatch);
		System.out.println("Database dif: "+ this.databaseUnMatch);		
	}
	
	public boolean compareTable(TableSchema ftable , TableSchema stable, String tablename) throws SQLException, Exception {	
		java.util.List<String> tbnondiff =  new LinkedList<String>();
		java.util.List<String> tbdiff =  new LinkedList<String>();
		java.util.Map<String, ColumnSchema> stabletemp = new java.util.LinkedHashMap<String, ColumnSchema>();
		stabletemp.putAll(stable.getColumns());
		for(java.util.Map.Entry<String, ColumnSchema> ftentry : ftable.getColumns().entrySet()) {
			String ftkey = ftentry.getKey();			
			ColumnSchema stableschema = stabletemp.get(ftkey);
			ColumnSchema ftableschema = ftentry.getValue();
			if(stableschema!=null) {				
				Boolean equaltable = ftableschema.equals(stableschema);
				
				if(equaltable) {
					stabletemp.remove(ftkey);
					tbnondiff.add(ftkey);
				} else {
					stabletemp.remove(ftkey);
					tbdiff.add(ftkey);
					
					ColumnDifferSchema dbdifferSchema = new ColumnDifferSchema(ftableschema, stableschema);
					
					ftable.addColumnsDiff(ftkey, dbdifferSchema);
					
				}
			} else {
				ColumnDifferSchema dbdifferSchema = new ColumnDifferSchema(ftableschema, null);

				ftable.addColumnsDiff(ftkey, dbdifferSchema);
				
				tbdiff.add(ftkey); 
				stabletemp.remove(ftkey);
			}
		}
		for(java.util.Map.Entry<String, ColumnSchema> diff : stabletemp.entrySet()) {
			tbdiff.add(diff.getKey());		
			ColumnDifferSchema dbdifferSchema = new ColumnDifferSchema(null, diff.getValue());
			ftable.addColumnsDiff(diff.getKey(), dbdifferSchema);			
		}				
		if(tbdiff.size() > 0) {
			return false;
		}
		return true;		
	}
	
	protected void doSaveTables(Connection connection, String configsave, DBUserConfig userConfig) throws Exception{
		//for override
	}

	public String execute(DBUserConfig userConfig, DBAlias source, DBAlias target, DBExport export) throws Exception {
		return execute(userConfig,new DBConfig(source, target), export);
	}
	
	public String execute(DBUserConfig userConfig, DBConfig dconfig) throws Exception {
		
		return execute(userConfig, dconfig, null);
	}

	public String execute(DBUserConfig userConfig, DBConfig dconfig, DBExport export) throws Exception {
		if(userConfig==null) throw new SQLException("User config not defined");
		String configsave = "";
		try(java.sql.Connection connection = DBConnection.getConnection(userConfig.getSection())) {
			if(userConfig.getConfigid() != null && userConfig.getConfigid().trim().length() > 0) {
				if(dconfig==null) dconfig = new DBConfig();
				configsave = dconfig.readConfig(connection, userConfig);
			} else {
				dconfig.setSourceConfig(dconfig.getSourceConfig());
				dconfig.setTargetConfig(dconfig.getTargetConfig());				
			}
			setConfig(dconfig);
			prepareDiff(dconfig, userConfig.getIgnorecase());
			doSaveTables(connection, configsave, userConfig);
			
			
			DBLog log = null;
			if(export!=null) {
				if(export.isConsole()) {
					log = new DBLog(getConfig().getSourceConfig().getDatabase(), getConfig().getTargetConfig().getDatabase(), this.databaseUnMatch);
				}
				if(export.getXmlfile()!= null && export.getXmlfile().trim().length() > 0) {
					new DBExportXML().ExportXML(getDatabaseUnMatch(), export.getXmlfile());
				}
				if(export.getTxtfile() != null && export.getTxtfile().trim().length() > 0) {
					new DBLog(getConfig().getSourceConfig().getDatabase(), getConfig().getTargetConfig().getDatabase(), this.databaseUnMatch, export.getTxtfile());
				}
				if(export.getJsonfile() != null && export.getJsonfile().trim().length() > 0) {
					new DBExportJSON().ExportJSON(getDatabaseUnMatch(), export.getJsonfile());
				}
				if(export.getPdffile() != null && export.getPdffile().trim().length() > 0) {
					new DBExportPDF(getDatabaseUnMatch(), getConfig(), export.getPdffile());
				}
				if(export.getXlsfile() != null && export.getXlsfile().trim().length() > 0) {
					new DBExportXLS(getDatabaseUnMatch(), getConfig(), export.getXlsfile());
				}
			}
			if(log!=null) return log.getResult();
		}
		return null;
	}
	
	public ColumnSchema fetchMetaDataAt(java.sql.ResultSetMetaData meta,int index) throws Exception {
		  if(meta==null) return null;
		  String colname = meta.getColumnName(index);
		  String label = meta.getColumnLabel(index);
		  if(label!=null && label.trim().length()>0 && !colname.equals(label)) colname = label;
		  ColumnSchema columnschema = new ColumnSchema();
		  columnschema.setColumnname(colname);
		  columnschema.setDatatype(meta.getColumnType(index));
		  columnschema.setLength(meta.getPrecision(index));
		  columnschema.setPrecision(meta.getScale(index));
		  return columnschema;
	}
	
	public DBConfig getConfig() {
		return config;
	}
	
	protected Connection getConnection(DBAlias config) throws Exception {
		if(config==null || !config.hasConfig()) throw new SQLException("Database configuration not defined");
		return getConnection(config.getDriver(),config.getUrl(),config.getUser(),config.getPassword(),config.getDatabase());
	}
	
	private Connection getConnection(String driver, String url, String user, String password, String dbname) throws Exception{
		System.out.println("try to get connection => "+ dbname+" ("+url+")");
		Class.forName(driver);		   
		return DriverManager.getConnection(url,user,password);
	}

	public java.util.LinkedList<TableSchema> getDatabaseUnMatch() {
		return databaseUnMatch;
	}
	
	public String getJournalid() {
		return journalid;
	}
	


	public java.util.Map<String, TableSchema> pickupData(Connection conn,Boolean ignorecase) throws Exception {
		java.util.Map<String, TableSchema> result = new java.util.LinkedHashMap<>();
		DatabaseMetaData metaData = conn.getMetaData();            
		try(ResultSet res = metaData.getTables(conn.getCatalog(), null, null, null)) {  
			while (res.next()) {
				String tablename = res.getString("TABLE_NAME");
				java.util.Map<String, ColumnSchema> fstable = new LinkedHashMap<>(); 
				try(ResultSet crs = metaData.getColumns(conn.getCatalog(),"",tablename,"")) {
					while(crs.next()) {
						ColumnSchema  table = new ColumnSchema(crs);
						fstable.put(ignorecase?table.getColumnname().toLowerCase():table.getColumnname(), table);
					}
				}
				if(fstable.isEmpty()) {
					KnSQL knsql = new KnSQL(this);
					knsql.append("select * from ");
					knsql.append(tablename);
					knsql.append(" where 1 = 1 ");
					try(java.sql.ResultSet rs = knsql.executeQuery(conn)) {
						readingMetaData(rs, fstable);
					}											
					result.put(ignorecase?tablename.toLowerCase():tablename, new TableSchema(tablename, fstable));		
				}
			} 
		}		
		return result;
	}
	
	public void prepareDiff(DBConfig config, Boolean ignorecase) throws Exception {		
		if(config==null) throw new SQLException("Databases configuration not defined");		
		try(Connection fconn = getConnection(config.getSourceConfig())) {
			try(Connection sconn = getConnection(config.getTargetConfig())) {
				java.util.Map<String, TableSchema> ftableschema = pickupData(fconn, ignorecase);
				java.util.Map<String, TableSchema> stableschema = pickupData(sconn, ignorecase);
				
				compareDatabase(ftableschema, stableschema);
			}
		}		
	}
	
	public LinkedList<TableSchema> readHistory(String string, Connection connection) throws Exception{
		return databaseUnMatch;
	
		
	}
	
	public void readingMetaData(java.sql.ResultSet rs,java.util.Map<String, ColumnSchema> fstable) throws Exception {		 
		java.sql.ResultSetMetaData met = rs.getMetaData();
		int colcount = met.getColumnCount();
		for(int i=1;i<=colcount;i++) {  
			String colname = met.getColumnName(i);		   
			fstable.put(colname, fetchMetaDataAt(met, i));
		}   
	}

	public void setConfig(DBConfig config) {
		this.config = config;
	}
	
	public void setDatabaseUnMatch(java.util.LinkedList<TableSchema> databaseUnMatch) {
		this.databaseUnMatch = databaseUnMatch;
	}



	public void setJournalid(String journalid) {
		this.journalid = journalid;
	}
	
	@Override
	public String toString() {
		return super.toString()+"DBDifferHandler [config=" + config + ", databaseUnMatch=" + databaseUnMatch + ", journalid=" + journalid
				+ "]";
	}
	
}
