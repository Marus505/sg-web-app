package com.dsw.sgweb.generator.builder;

import com.dsw.core.util.builder.*;
import com.dsw.core.util.builder.template.*;

/**
 * Class Name : JSPListBuilder.java
 * Description : JSPListBuilder class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 27.
 * @version 1.0
 *
 */
public class JSPRegistBuilder implements SourceBuilder {

	/**
	 * Template 파일 (tpl)
	 */
	public static final String TEMPLATE = "/static/tpl/JSPRegistTemplate.template";

	/**
	 * Template Loading Encoding
	 */
	private final String ENCODING = "UTF-8";

	/**
	 * Generated XML Source
	 */
	private StringBuffer source;

	public static final String PROP_SQL_SOURCE = "PROP_SQL_SOURCE";

	public static final String PROP_PROJECT_NAME = "PROP_PROJECT_NAME";

	public static final String PROP_MODULE_PACKAGE_NAME = "PROP_MODULE_PACKAGE_NAME";
	public static final String PROP_SVC_CLASS_NAME = "PROP_SVC_CLASS_NAME";

	/* (non-Javadoc)
	 * @see com.dsw.core.util.builder.SourceBuilder#build(com.dsw.core.util.builder.SourceBuilderProperties)
	 */
	public void build(SourceBuilderProperties<String, Object> properties) throws Exception {
		String projectName = properties.getString(PROP_PROJECT_NAME);
		String modulePackage = properties.getString(PROP_MODULE_PACKAGE_NAME);
		String svcClass = properties.getString(PROP_SVC_CLASS_NAME);
		String controllerMapName = modulePackage.replaceAll("\\.", "\\/");
		String jspPrefix = Utility.toFieldName(svcClass.replace("Controller", ""));

//		SqlSource sqlSource = (SqlSource)properties.get(PROP_SQL_SOURCE);

//		String[] javaKeyFields = sqlSource.getJavaKeyFields();
//		String[] javaKeyTypes = sqlSource.getJavaKeyTypes();

		JspSourceTemplate template = new JspSourceTemplate(TEMPLATE);
		template.loadTemplate(ENCODING);

		final String scriptletDefine = template.getScriptletDefine("scriptlet");
		final String docTypeDefine = template.getDocTypeDefine("docType");
		final String rootDefine = template.getRootDefine("root");

		StringBuffer source = new StringBuffer();
		source.append(SourceBuilderUtil.replaceVariables(scriptletDefine, "encode", ENCODING));
		source.append(docTypeDefine);

		//Create ROOT
		String rootNode = rootDefine;
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "projectName", projectName);
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "modulePackage", modulePackage);
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "svcClass", svcClass);
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "controllerMapName", controllerMapName);
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "jspPrefix", jspPrefix);

		source.append(rootNode);
		this.source = source;
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.builder.SourceBuilder#getSourceString()
	 */
	public String getSourceString() {
		return this.source.toString();
	}

}
