package com.fs.tool;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.fs.bean.misc.KnSQL;
import com.fs.bean.util.BeanUtility;
import com.fs.dev.Arguments;
import com.fs.dev.Console;

@SuppressWarnings("serial")
public class DBDiffer extends DBDifferHandler {
	
	public static void main(String[] args) {
		//java com/fs/tool/DBDiffer -test -ms PROMPT
		//java com/fs/tool/DBDiffer -local -ms PROMPT
		//java com/fs/tool/DBDiffer -local -ms PROMPT -f d:\differ.txt -json d:\differ.json -xml d:\differ.xml -xls d:\differ.xls -pdf d:\differ.pdf
		DBAlias source = new DBAlias();
		DBAlias target = new DBAlias();
		DBUserConfig userConfig = new DBUserConfig();
		DBExport export = new DBExport();
		
		if(args.length>0) {
			if(Arguments.getBoolean(args, "-help","-?")) {
				usage();
				return;
			}

			source.setDriver(Arguments.getString(args, null, "-sd","-drivers")); //driver name
			source.setUrl(Arguments.getString(args, null, "-srl","-urls")); //URL
			source.setUser(Arguments.getString(args, null, "-su","-users")); //user name
			source.setPassword(Arguments.getString(args, null, "-sp","-passwords")); //user's password
			source.setDatabase(Arguments.getString(args, null, "-sdb","-dbnames")); //database name 		
			
			target.setDriver(Arguments.getString(args, null, "-td","-drivert"));
			target.setUrl(Arguments.getString(args, null, "-trl","-urlt")); //URL
			target.setUser(Arguments.getString(args, null, "-tu","-usert")); //user name
			target.setPassword(Arguments.getString(args, null, "-tp","-passwordt")); //user's password
			target.setDatabase(Arguments.getString(args, null, "-tdb","-dbnamet")); //database name 
			
			userConfig.setIgnorecase(Arguments.getBoolean(args, null, "-igc", "-ignorecase"));
			userConfig.setConfigid(Arguments.getString(args,null,"-cfg","-configid"));
			userConfig.setConfigname(Arguments.getString(args,null,"-cn", "-configname"));
			userConfig.setSection(Arguments.getString(args,null,"-ms", "-section"));
			userConfig.setUser(Arguments.getString(args, null, "-u","-user"));
			
			export.setTxtfile(Arguments.getString(args,null,"-f", "-filename"));
			export.setPdffile(Arguments.getString(args,null,"-pdf"));
			export.setXlsfile(Arguments.getString(args,null,"-xls"));
			export.setJsonfile(Arguments.getString(args, null, "-json"));
			export.setXmlfile(Arguments.getString(args, null, "-xml"));
			
			if(Arguments.getBoolean(args, "-test")) {				
				source = new DBAlias("com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/refdb1?allowPublicKeyRetrieval=true&useSSL=false&verifyServerCertificate=false", "root", "172352", "refdb1");
				target = new DBAlias("com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/refdb2?allowPublicKeyRetrieval=true&useSSL=false&verifyServerCertificate=false", "root", "172352", "refdb2");			
				userConfig.setIgnorecase(true);
			}			
			else if(Arguments.getBoolean(args, "-local")) {
				source = new DBAlias("com.mysql.jdbc.Driver","jdbc:mysql://127.0.0.1:3306/testdb1?allowPublicKeyRetrieval=true&useSSL=false&verifyServerCertificate=false","root","root","testdb1");
				target = new DBAlias("com.mysql.jdbc.Driver","jdbc:mysql://127.0.0.1:3306/testdb2?allowPublicKeyRetrieval=true&useSSL=false&verifyServerCertificate=false","root","root","testdb2");			
			}
			
			try {
				DBDiffer differ = new DBDiffer();				
				differ.execute(userConfig, source, target, export);				
			} catch(SQLException ex) {
				ex.printStackTrace();
				Console.out.println("ERROR CODE = "+ex.getErrorCode());
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		} else {
			usage();
		}		
	}
	
	public static void usage() {
		Console.out.println("USAGE : "+DBDiffer.class);
		Console.out.println("Source:");
		Console.out.println("\t-sd, -drivers JDBC Driver");
		Console.out.println("\t-srl, -urls URL to be access");
		Console.out.println("\t-su, -users user's name privileged");
		Console.out.println("\t-sp, -passwords user's password");
		Console.out.println("\t-sdb, -dbnames Database Name");
		Console.out.println("Target:");
		Console.out.println("\t-td, -driver JDBC Driver");
		Console.out.println("\t-trl, -url URL to be access");
		Console.out.println("\t-tu, -user user's name privileged");
		Console.out.println("\t-tp, -password user's password");
		Console.out.println("\t-tdb, -dbname Database Name");		
	}
	public DBDiffer() {
		super();
	}
	public DBDiffer(DBUserConfig userConfig, DBConfig dconfig) throws Exception {
		execute(userConfig, dconfig);
	}
	
	public void assignParameters(KnSQL knsql, DBAlias source, DBAlias target) {
		knsql.setParameter("sourcedriver", source.getDriver());
		knsql.setParameter("sourceurl", source.getUrl());
		knsql.setParameter("sourceuser", source.getUser());
		knsql.setParameter("sourcepassword", source.getPassword());
		knsql.setParameter("sourcedatabase", source.getDatabase());
		
		knsql.setParameter("targetdriver", target.getDriver());
		knsql.setParameter("targeturl", target.getUrl());
		knsql.setParameter("targetuser", target.getUser());
		knsql.setParameter("targetpassword", target.getPassword());
		knsql.setParameter("targetdatabase", target.getDatabase());
		
		
	}
	
	@Override
	public void doSaveTables(Connection connection, String configsave, DBUserConfig userConfig) throws Exception {		
		KnSQL knsql = new KnSQL(this);
		if(configsave != null && configsave.trim().length() > 0) {
		} else {
			userConfig.setConfigid(java.util.UUID.randomUUID().toString());
			saveTodifconfig(knsql, connection, userConfig);
		}		
		knsql.clear();
		saveTotdifschema(knsql, connection, userConfig);
		knsql.clear();
		if(getDatabaseUnMatch() != null) {
			for(int i = 0; i < getDatabaseUnMatch().size(); i++) {
				knsql.clear();
				TableSchema tb = getDatabaseUnMatch().get(i);
				saveTotdiftable(connection, tb, userConfig);
				saveTotdifcolumn(tb, userConfig, connection);
			}
		} else {
			System.out.println("Data not found");
		}		
	}
	public java.util.LinkedHashSet<String> getColumns(Connection connection, String tablename,String journalid) throws SQLException{
		java.util.LinkedHashSet<String> list_col = new java.util.LinkedHashSet<String>();
		KnSQL knsql = new KnSQL();
		knsql.append("select * from tdifcolumn where journalid = ?journalid and tablename = ?tablename ");
		knsql.setParameter("journalid", journalid);
		knsql.setParameter("tablename", tablename);
		try(ResultSet rs = knsql.executeQuery(connection)){
			while(rs.next()) {
				list_col.add(rs.getString("columnname"));
			}
		}
		return list_col;
	}
	public DBObjectSchema getSourceColumnSchemas(String tablename,String journalid,Connection connection) throws SQLException {
		
		java.util.Map<String, ColumnSchema> columns = new LinkedHashMap<>();
		java.util.Map<String, ColumnDifferSchema> columnsdiff = new LinkedHashMap<>();
		java.util.LinkedHashSet<String> columnnames = getColumns(connection, tablename, journalid);
		KnSQL knsql = new KnSQL();
		for(String s : columnnames) {
			
		knsql.clear();
		knsql.append("select * from tdifcolumn where journalid = ?journalid and tablename = ?tablename and columnname = ?columnname");
		knsql.setParameter("journalid", journalid);
		knsql.setParameter("tablename", tablename);
		knsql.setParameter("columnname", s);
		
		try(ResultSet crs = knsql.executeQuery(connection)){
			ColumnSchema source = null;
			ColumnSchema target = null;
			ColumnDifferSchema dif = null;
			int size = 0;
			if (crs != null) {
				crs.last();
				size = crs.getRow();
				crs.beforeFirst();
				
			}
			String kind = "";
			while(crs.next()) {
				if(size == 1) kind = crs.getString("columnkind");
				if("S".equals(crs.getString("columnkind"))) source = new ColumnSchema(crs, true);
				if("T".equals(crs.getString("columnkind"))) {
					target = new ColumnSchema(crs, true); 
					if(size == 2) {
						dif = new ColumnDifferSchema(source.getLength() == -1 ? null: source, target.getLength() == -1 ? null : target, crs.getBoolean("lengthflag"), crs.getBoolean("precisionflag"), crs.getBoolean("typeflag"));
					}
					
				}
			}
			if(size == 2) {
				columns.put(s, source);
				columnsdiff.put(s, dif);
			}else {
				if("S".equals(kind)) {
					columns.put(s, source);
				}else {
					columns.put(s, target);
				}
			}

			
		}
		}
		return new DBObjectSchema(columns, columnsdiff);	
	}

	
	public java.util.LinkedList<TableSchema> getTdiftable(String journalid,Connection connection) throws SQLException {
		KnSQL knsql = new KnSQL();
		knsql.append("select * from tdiftable where journalid = ?journalid");
		knsql.setParameter("journalid", journalid);
		TableSchema tableschema;
		java.util.LinkedList<TableSchema> unmatch = new LinkedList<TableSchema>();
		try(ResultSet res = knsql.executeQuery(connection)){
			while (res.next()) { 

				DBObjectSchema objectschema = getSourceColumnSchemas(res.getString("tablename"), journalid, connection);
				tableschema = new TableSchema(objectschema.getSource(), objectschema.getColumnDifferSchema(), res.getString("tablename"), res.getString("tablekind"));
				unmatch.add(tableschema);
			}
		}
		return unmatch;
	}

	public LinkedList<TableSchema> readHistory(String journalid, Connection connection) throws SQLException {
		
		if(connection == null) return null;
		LinkedList<TableSchema> unmatch = getTdiftable(journalid, connection);
		return unmatch;
		
		
	
	}
	public void saveColumn(Connection connection, TableSchema table, java.util.Map.Entry<String, ColumnDifferSchema> key, DBUserConfig userConfig, String kind) throws SQLException {
		KnSQL knsql = new KnSQL();
		String trxid = java.util.UUID.randomUUID().toString();
		knsql.append("insert into tdifcolumn (trxid, journalid, tablename, columnname, columnkind, columnlength, columnprecision");
		knsql.append(", columntype, lengthflag, precisionflag, typeflag, createdate, createtime, createuser)");
		knsql.append("Values (?trxid, ?journalid, ?tablename, ?columnname, ?columnkind, ?columnlength, ?columnprecision,");
		knsql.append("?columntype, ?lengthflag, ?precisionflag, ?typeflag, ?createdate, ?createtime, ?createuser)");
		knsql.setParameter("trxid", trxid);
		knsql.setParameter("journalid", getJournalid());
		knsql.setParameter("tablename", table.getTablename());
		knsql.setParameter("columnname", key.getKey() );
		knsql.setParameter("columnkind", kind);
		if("S".equals(kind)) {
			knsql.setParameter("columnlength", key.getValue().getSourceLength());
			knsql.setParameter("columnprecision", key.getValue().getSourcePrecision());
			knsql.setParameter("columntype", key.getValue().getSourceType());
		}else {
			knsql.setParameter("columnlength", key.getValue().getTargetLength());
			knsql.setParameter("columnprecision", key.getValue().getTargetPrecision());
			knsql.setParameter("columntype", key.getValue().getTargetType());			
		}
		knsql.setParameter("lengthflag", key.getValue().getLengthflag());
		knsql.setParameter("precisionflag", key.getValue().getPrecisionflag());
		knsql.setParameter("typeflag", key.getValue().getTypeflag());
		knsql.setParameter("createdate", BeanUtility.getCurrentDate());
		knsql.setParameter("createtime", BeanUtility.getCurrentTime());
		knsql.setParameter("createuser", userConfig.getUser());
		knsql.executeUpdate(connection);
	}
	public void saveTodifconfig(DBConfig config , Connection connection, DBUserConfig userConfig) throws SQLException {	
		KnSQL knsql = new KnSQL();
		knsql.clear();
		knsql.append("insert into tdifconfig (configid, configname, sourcedriver, sourceurl, sourceuser, sourcepassword, sourcedatabase, targetdriver, targeturl, targetuser, targetpassword, targetdatabase, createdate, createtime, createuser, ignorecase)");
		knsql.append("Values (?configid, ?configname, ?sourcedriver, ?sourceurl, ?sourceuser, ?sourcepassword, ?sourcedatabase, ?targetdriver, ?targeturl, ?targetuser, ?targetpassword, ?targetdatabase, ?createdate, ?createtime, ?createuser, ?ignorecase)");
		
		String configname = userConfig.getConfigname();
		
				
		Date createdate = BeanUtility.getCurrentDate();
		Time createtime = BeanUtility.getCurrentTime();
		if(configname==null) configname = "Auto saving as at "+BeanUtility.getCurrentTimestamp();
		knsql.setParameter("configid", userConfig.getConfigid());
		knsql.setParameter("configname", configname);
		
		assignParameters(knsql, config.getSourceConfig(), config.getTargetConfig());
		
		knsql.setParameter("createdate", createdate);
		knsql.setParameter("createtime", createtime);
		knsql.setParameter("createuser", userConfig.getUser());
		knsql.setParameter("ignorecase", config.isIgnorecase());
		knsql.executeUpdate(connection);
	}
	
	public void saveTodifconfig(KnSQL knsql, Connection connection, DBUserConfig userConfig) throws SQLException {		
		knsql.append("insert into tdifconfig (configid, configname, sourcedriver, sourceurl, sourceuser, sourcepassword, sourcedatabase, targetdriver, targeturl, targetuser, targetpassword, targetdatabase, createdate, createtime, createuser, ignorecase)");
		knsql.append("Values (?configid, ?configname, ?sourcedriver, ?sourceurl, ?sourceuser, ?sourcepassword, ?sourcedatabase, ?targetdriver, ?targeturl, ?targetuser, ?targetpassword, ?targetdatabase, ?createdate, ?createtime, ?createuser, ?ignorecase)");
		

		
		String configname = userConfig.getConfigname();
		Date createdate = BeanUtility.getCurrentDate();
		Time createtime = BeanUtility.getCurrentTime();
		if(configname==null) configname = "Auto saving as at "+BeanUtility.getCurrentTimestamp();

		knsql.setParameter("configid", userConfig.getConfigid());
		knsql.setParameter("configname", configname);
		
		assignParameters(knsql, getConfig().getSourceConfig() , getConfig().getTargetConfig());
		
		knsql.setParameter("createdate", createdate);
		knsql.setParameter("createtime", createtime);
		knsql.setParameter("createuser", userConfig.getUser());
		knsql.setParameter("ignorecase", userConfig.getIgnorecase());
		knsql.executeUpdate(connection);
	}
	public void saveTotdifcolumn( TableSchema table, DBUserConfig userConfig, Connection connection) throws SQLException, Exception {	
		if(table.getColumnsdiff().size() > 0) {
		for(Entry<String, ColumnDifferSchema> key : table.getColumnsdiff().entrySet()) {
			
			saveColumn(connection, table, key, userConfig, "S");
			saveColumn(connection, table, key, userConfig, "T");		
		}
		}else {
			for(Entry<String, ColumnSchema> key : table.getColumns().entrySet()) {
				KnSQL knsql = new KnSQL();
				String trxid = java.util.UUID.randomUUID().toString();
				knsql.append("insert into tdifcolumn (trxid, journalid, tablename, columnname, columnkind, columnlength, columnprecision");
				knsql.append(", columntype, lengthflag, precisionflag, typeflag, createdate, createtime, createuser)");
				knsql.append("Values (?trxid, ?journalid, ?tablename, ?columnname, ?columnkind, ?columnlength, ?columnprecision,");
				knsql.append("?columntype, ?lengthflag, ?precisionflag, ?typeflag, ?createdate, ?createtime, ?createuser)");
				knsql.setParameter("trxid", trxid);
				knsql.setParameter("journalid", getJournalid());
				knsql.setParameter("tablename", table.getTablename());
				knsql.setParameter("columnname", key.getKey() );
				knsql.setParameter("columnkind", table.getTablekind());
				if("S".equals(table.getTablekind())) {
					knsql.setParameter("columnlength", key.getValue().getLength());
					knsql.setParameter("columnprecision", key.getValue().getPrecision());
					knsql.setParameter("columntype", key.getValue().getDatatype());
				}else {
					knsql.setParameter("columnlength", key.getValue().getLength());
					knsql.setParameter("columnprecision", key.getValue().getPrecision());
					knsql.setParameter("columntype", key.getValue().getDatatype());			
				}
				knsql.setParameter("lengthflag", 1);
				knsql.setParameter("precisionflag", 1);
				knsql.setParameter("typeflag", 1);
				knsql.setParameter("createdate", BeanUtility.getCurrentDate());
				knsql.setParameter("createtime", BeanUtility.getCurrentTime());
				knsql.setParameter("createuser", userConfig.getUser());
				knsql.executeUpdate(connection);
			}
		}

	}
	public void saveTotdifschema(KnSQL knsql,Connection connection, DBUserConfig userConfig) throws SQLException {
		String journalname = userConfig.getJournalname();
		int diftables = getDatabaseUnMatch().size();
		Date createdate = BeanUtility.getCurrentDate();
		Time createtime = BeanUtility.getCurrentTime();
		if(journalname==null) journalname = "Auto saving as at "+BeanUtility.getCurrentTimestamp();
		String createuser = userConfig.getUser();
		knsql.append("insert into tdifschema (journalid, journalname, configid, sourcedatabase, targetdatabase, diftables, createdate, createtime, createuser) values (?journalid, ?journalname, ?configid, ?sourcedatabase, ?targetdatabase, ?diftables, ?createdate, ?createtime, ?createuser)");
		knsql.setParameter("journalid", getJournalid());
		knsql.setParameter("journalname", journalname);
		knsql.setParameter("configid", userConfig.getConfigid());
		knsql.setParameter("sourcedatabase", getConfig().getSourceConfig().getDatabase());
		knsql.setParameter("targetdatabase",getConfig().getTargetConfig().getDatabase());
		knsql.setParameter("diftables", diftables);
		knsql.setParameter("createdate", createdate);
		knsql.setParameter("createtime", createtime);
		knsql.setParameter("createuser", createuser);
		knsql.executeUpdate(connection);
	}

	public void saveTotdiftable(Connection connection, TableSchema tb, DBUserConfig userConfig) throws SQLException {
		KnSQL knsql = new KnSQL();
		knsql.append("insert into tdiftable (journalid, tablename, tablekind, difcolumns, createdate, createtime, createuser) ");
		knsql.append("Values (?journalid, ?tablename, ?tablekind, ?difcolumns, ?createdate, ?createtime, ?createuser)");
		knsql.setParameter("journalid", getJournalid());
		knsql.setParameter("tablename", tb.getTablename());
		knsql.setParameter("difcolumns", tb.getColumnsdiff().size());
		knsql.setParameter("tablekind", tb.getTablekind());
		knsql.setParameter("createdate", BeanUtility.getCurrentDate());
		knsql.setParameter("createtime", BeanUtility.getCurrentTime());
		knsql.setParameter("createuser", userConfig.getUser());
		knsql.executeUpdate(connection);
	}

}
