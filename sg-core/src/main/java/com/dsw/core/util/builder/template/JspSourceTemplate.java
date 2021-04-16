package com.dsw.core.util.builder.template;

/**
 * Class Name : JspSourceTemplate.java
 * Description : JspSourceTemplate class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 27.
 * @version 1.0
 *
 */
public class JspSourceTemplate extends SourceTemplate {
	/**
	 * @param template
	 */
	public JspSourceTemplate(String template){
		super(template);
	}

	/**
	 * @param typeName
	 * @return String
	 */
	public String getScriptletDefine(String typeName){
		return getDefine("SCRIPTLET-DEFINE", typeName);
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
