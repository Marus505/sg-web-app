package com.dsw.core.util.builder.template;

/**
 * Class Name : XmlSourceTemplate.java
 * Description : XmlSourceTemplate class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public class XmlSourceTemplate extends SourceTemplate {

	/**
	 * @param template
	 */
	public XmlSourceTemplate(String template){
		super(template);
	}

	/**
	 * @param typeName
	 * @return String
	 */
	public String getXmlDefine(String typeName){
		return getDefine("XML-DEFINE", typeName);
	}

	/**
	 * @param typeName
	 * @return String
	 */
	public String getDocTypeDefine(String typeName){
		return getDefine("DOCTYPE-DEFINE", typeName);
	}

	/**
	 * @param typeName
	 * @return String
	 */
	public String getRootDefine(String typeName){
		return getDefine("ROOT-DEFINE", typeName);
	}

	/**
	 * @param typeName
	 * @return String
	 */
	public String getNodeDefine(String typeName){
		return getDefine("NODE-DEFINE", typeName);
	}

}

