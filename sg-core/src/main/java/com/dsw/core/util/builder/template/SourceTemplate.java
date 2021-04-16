package com.dsw.core.util.builder.template;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Class Name : SourceTemplate.java
 * Description : SourceTemplate class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public abstract class SourceTemplate {
	private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	protected final String START = "SOT@";
	protected final String END = "@EOT";

	protected String tpl;
	protected StringBuffer template;

	/**
	 * @param templateFile
	 */
	public SourceTemplate(String templateFile){
		this.tpl = templateFile;
		this.template = new StringBuffer();
	}

	/**
	 * @param encoding
	 * @throws Exception
	 */
	public void loadTemplate(String encoding) throws Exception {

		InputStream in = null;

		try{
			URL url = this.getClass().getResource(tpl);
			if(url == null){
				throw new Exception("Template 파일 : " + tpl + " 을(를) 읽을 수 없습니다.");
			}
			in = url.openStream();
			template = new StringBuffer();
			byte[] buffer = new byte[1024];
			while(in.read(buffer) > -1){
				template.append(new String(buffer, encoding));
			}
		}catch(IOException e){
			throw new Exception("Template 파일 : " + tpl + " 을(를) 읽을 수 없습니다." + e.getMessage());
		}finally{
			if(in != null)
				try{in.close();}catch(Exception e){}
		}
	}

	/**
	 * @param defineName
	 * @param typeName
	 * @return String
	 */
	protected String getDefine(String defineName, String typeName) {
		int define = template.indexOf("["+defineName+"=" + typeName + "]");
		int start = template.indexOf(START, define) + START.length();
		int end = template.indexOf(END, define);
		return template.substring(start, end);
	}


	/**
	 * @return String
	 */
	public String creationDate() {
		return dateFormatter.format(new Date());
	}
}
