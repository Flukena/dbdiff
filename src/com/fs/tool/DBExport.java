package com.fs.tool;

public class DBExport {
	private boolean console = true;
	private String jsonfile;
	private String pdffile;
	private String txtfile;
	private String xlsfile;
	private String xmlfile;
	
	public DBExport() {
		super();
	}

	public DBExport(String txtfile,String jsonfile, String xmlfile, String xlsfile, String pdffile) {
		setTxtfile(txtfile);
		setPdffile(pdffile);
		setXmlfile(xmlfile);
		setJsonfile(jsonfile);
		setXlsfile(xlsfile);
	}

	public String getJsonfile() {
		return jsonfile;
	}
	
	public String getPdffile() {
		return pdffile;
	}
	
	public String getTxtfile() {
		return txtfile;
	}
	
	public String getXlsfile() {
		return xlsfile;
	}
	
	public String getXmlfile() {
		return xmlfile;
	}
	
	public boolean isConsole() {
		return console;
	}
	
	public void setConsole(boolean console) {
		this.console = console;
	}
	
	public void setJsonfile(String jsonfile) {
		this.jsonfile = jsonfile;
	}
	
	public void setPdffile(String pdffile) {
		this.pdffile = pdffile;
	}
	
	public void setTxtfile(String txtfile) {
		this.txtfile = txtfile;
	}
	
	public void setXlsfile(String xlsfile) {
		this.xlsfile = xlsfile;
	}
	
	public void setXmlfile(String xmlfile) {
		this.xmlfile = xmlfile;
	}

}
