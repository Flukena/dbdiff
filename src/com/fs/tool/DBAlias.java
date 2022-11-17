package com.fs.tool;

@SuppressWarnings("serial")
public class DBAlias implements java.io.Serializable {
	private String database = null;
	private String driver = null;
	private String password = null;
	private String url = null;
	private String user = null;
	
	public DBAlias() {
		super();
	}
	
	public DBAlias(String driver,String url,String user,String password,String database) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
		this.database = database;
	}
	
	public void fetchSourceAlias(java.sql.ResultSet rs) throws Exception {
		this.driver = rs.getString("sourcedriver");
		this.url = rs.getString("sourceurl");
		this.user = rs.getString("sourceuser");
		this.password = rs.getString("sourcepassword");
		this.database = rs.getString("sourcedatabase");		
	}
	
	public void fetchTargetAlias(java.sql.ResultSet rs) throws Exception {
		this.driver = rs.getString("targetdriver");
		this.user = rs.getString("targetuser");
		this.url = rs.getString("targeturl");
		this.password = rs.getString("targetpassword");
		this.database = rs.getString("targetdatabase");		
	}
	
	public String getDatabase() {
		return database;
	}
	
	public String getDriver() {
		return driver;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public boolean hasConfig() {
		return this.driver!=null && this.driver.trim().length()>0 
				&& this.url!=null && this.url.trim().length()>0
				&& this.user!=null && this.user.trim().length()>0
				&& this.password!=null 
				&& this.database!=null && this.database.trim().length()>0;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return super.toString()+"[driver=" + driver + ", url=" + url + ", user=" + user + ", password=" + password
				+ ", database=" + database + "]";
	}
	
}
