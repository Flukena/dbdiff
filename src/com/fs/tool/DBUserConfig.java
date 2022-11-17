package com.fs.tool;

public class DBUserConfig {
	private String configid;
	private String configname;
	private Boolean ignorecase;
	private String journalname;
	private String section;
	private String user;
	
	public DBUserConfig() {
		super();
	}
	
	public DBUserConfig(String configid, String configname, Boolean ignorecase, String user, String section) {
		setConfigid(configid);
		setConfigname(configname);
		setIgnorecase(ignorecase);
		setUser(user);
		setSection(section);
	}

	
	public String getConfigid() {
		return configid;
	}
	
	public String getConfigname() {
		return configname;
	}
	
	public Boolean getIgnorecase() {
		return ignorecase;
	}
	public String getJournalname() {
		return journalname;
	}
	
	public String getSection() {
		return section;
	}
	
	public String getUser() {
		return user;
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
	
	public void setJournalname(String journalname) {
		this.journalname = journalname;
	}
	
	public void setSection(String section) {
		this.section = section;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	@Override
	public String toString() {
		return super.toString()+"[configid=" + configid + ", configname=" + configname + ", ignorecase=" + ignorecase
				+ ", journalname=" + journalname + ", section=" + section + ", user=" + user + "]";
	}
	
}
