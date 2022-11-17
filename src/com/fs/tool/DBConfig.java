package com.fs.tool;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import com.fs.bean.misc.KnSQL;
import com.fs.bean.util.BeanUtility;
import com.fs.dev.Console;

@SuppressWarnings("serial")
public class DBConfig implements java.io.Serializable {

	private String configid;
	private String configname;
	private Boolean ignorecase;
	private DBAlias sourceConfig;
	private DBAlias targetConfig;

	public DBConfig() {
		super();
	}

	public DBConfig(DBAlias source, DBAlias target) {
		this.sourceConfig = source;
		this.targetConfig = target;
	}

	public DBConfig(DBAlias source, DBAlias target, Boolean ignorecase, String configid, String configname) {
		this.sourceConfig = source;
		this.targetConfig = target;
		this.ignorecase = ignorecase;
		this.configid = configid;
		this.configname = configname;
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
	public String getConfigid() {
		return configid;
	}
	public String getConfigname() {
		return configname;
	}

	public DBAlias getSourceConfig() {
		return sourceConfig;
	}

	public DBAlias getTargetConfig() {
		return targetConfig;
	}

	public boolean hasConfig() {
		return hasConfig(getSourceConfig(),getTargetConfig());
	}
	
	public boolean hasConfig(DBAlias source, DBAlias target) {
		return (source!=null && source.hasConfig()) && (target!=null && target.hasConfig());		
	}
	public Boolean isIgnorecase() {
		return ignorecase;
	}
	public String readConfig(java.sql.Connection connection, DBUserConfig userConfig) throws Exception {
		KnSQL knsql = new KnSQL(this);
		knsql.append("select * from tdifconfig where configid = ?configid");
		knsql.setParameter("configid", userConfig.getConfigid());
		try(java.sql.ResultSet rs = knsql.executeQuery(connection)) {
			if(rs.next()) {
				setConfigid(rs.getString("configid"));
				setConfigname(rs.getString("configname"));
				DBAlias source = new DBAlias();
				source.fetchSourceAlias(rs);
				DBAlias target = new DBAlias();
				target.fetchTargetAlias(rs);
				if(hasConfig(source, target)) {
					setSourceConfig(source);
					setTargetConfig(target);
					return userConfig.getConfigid();
				}		
			} else {
				if(hasConfig()) {
					saveConfig(knsql, userConfig, connection);
					return userConfig.getConfigid();
				}
			}
		}
		Console.out.println("Config incomplete information");
		return null;
	}
	public DBConfig readConfig(java.sql.Connection connection, String configid) throws Exception {
		KnSQL knsql = new KnSQL(this);
		knsql.append("select * from tdifconfig where configid = ?configid");
		knsql.setParameter("configid", configid);
		try(java.sql.ResultSet rs = knsql.executeQuery(connection)) {
			if(rs.next()) {
				DBAlias source = new DBAlias();
				source.fetchSourceAlias(rs);
				DBAlias target = new DBAlias();
				target.fetchTargetAlias(rs);
				
				if(hasConfig(source, target)) {
					return new DBConfig(source, target, rs.getBoolean("ignorecase"), rs.getString("configid"), rs.getString("configname"));
				}		
				
			}
		}
		return null;
	}
	public void saveConfig(KnSQL knsql, DBUserConfig userConfig, java.sql.Connection connection) throws SQLException {
		knsql.clear();
		knsql.append("insert into tdifconfig (configid, configname, sourcedriver, sourceurl, sourceuser, sourcepassword, sourcedatabase, targetdriver, targeturl, targetuser, targetpassword, targetdatabase, ignorecase, createdate, createtime, createuser)");
		knsql.append("Values (?configid, ?configname, ?sourcedriver, ?sourceurl, ?sourceuser, ?sourcepassword, ?sourcedatabase, ?targetdriver, ?targeturl, ?targetuser, ?targetpassword, ?targetdatabase, ?ignorecase, ?createdate, ?createtime, ?createuser)");
		Date createdate = BeanUtility.getCurrentDate();
		Time createtime = BeanUtility.getCurrentTime();
		String createuser= userConfig.getUser();
		knsql.setParameter("configid", userConfig.getConfigid());
		knsql.setParameter("configname", userConfig.getConfigname()!= null && userConfig.getConfigname().trim().length() > 0 ? userConfig.getConfigname() : userConfig.getConfigid());
		
		assignParameters(knsql, getSourceConfig(), getTargetConfig());
		
		knsql.setParameter("ignorecase", userConfig.getIgnorecase());
		knsql.setParameter("createdate", createdate);
		knsql.setParameter("createtime", createtime);
		knsql.setParameter("createuser",createuser);
		knsql.executeUpdate(connection);
	}
	
	public void setConfigid(String configid) {
		this.configid = configid;
	}
	public void setConfigname(String configname) {
		this.configname = configname;
	}
	public void setIgnorecase(Boolean ignorecase) {
		this.ignorecase = ignorecase;
	}
	
	public void setSourceConfig(DBAlias sourceConfig) {
		this.sourceConfig = sourceConfig;
	}

	public void setTargetConfig(DBAlias targetConfig) {
		this.targetConfig = targetConfig;
	}
	
	public void updateConfig(Connection connection, String fsuser) throws SQLException {
		Date createdate = BeanUtility.getCurrentDate();
		Time createtime = BeanUtility.getCurrentTime();
		KnSQL knsql = new KnSQL();
		knsql.append("UPDATE tdifconfig SET  configid = ?configid, configname = ?configname, sourcedriver = ?sourcedriver, sourceurl = ?sourceurl, sourceuser = ?sourceuser, sourcepassword = ?sourcepassword, sourcedatabase = ?sourcedatabase, targetdriver =?targetdriver, targeturl = ?targeturl, targetuser = ?targetuser, targetpassword = ?targetpassword, targetdatabase = ?targetdatabase, createdate = ?createdate, createtime = ?createtime, createuser = ?createuser, ignorecase = ?ignorecase ");
		knsql.append(" where configid = ?configid");
		
		knsql.setParameter("configid", getConfigid());
		knsql.setParameter("configname", getConfigname());
		
		assignParameters(knsql, getSourceConfig(), getTargetConfig());
		
		knsql.setParameter("createdate", createdate);
		knsql.setParameter("createtime", createtime);
		knsql.setParameter("createuser", fsuser);
		knsql.setParameter("ignorecase", isIgnorecase());
		
		knsql.executeUpdate(connection);
	}
	
}
