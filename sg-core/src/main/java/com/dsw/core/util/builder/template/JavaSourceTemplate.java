package com.dsw.core.util.builder.template;

/**
 *
 * Class Name : JavaSourceTemplate.java
 * Description : JavaSourceTemplate class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 */
public class JavaSourceTemplate extends SourceTemplate {

	/**
	 * @param template
	 */
	public JavaSourceTemplate(String template){
		super(template);
	}

	/**
	 * @param typeName
	 * @return String
	 */
	public String getImportDefine(String typeName){
		//[METHOD-DEFINE
		return super.getDefine("IMPORT-DEFINE", typeName);
	}

	/**
	 * @param typeName
	 * @return String
	 */
	public String getClassDefine(String typeName){
		//[CLASS-DEFINE]
		return super.getDefine("CLASS-DEFINE", typeName);
	}

	/**
	 * @param typeName
	 * @return String
	 */
	public String getMethodDefine(String typeName){
		//[METHOD-DEFINE
		return super.getDefine("METHOD-DEFINE", typeName);
	}

	/**
	 * @param typeName
	 * @return String
	 */
	public String getFieldDefine(String typeName){
		//[FIELD-DEFINE
		return super.getDefine("FIELD-DEFINE", typeName);
	}

	/**
	 * @param typeName
	 * @return String
	 */
	public String getIoCDefine(String typeName){
		//[IoC-DEFINE
		return super.getDefine("IoC-DEFINE", typeName);
	}
}
