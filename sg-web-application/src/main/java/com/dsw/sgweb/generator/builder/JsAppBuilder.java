package com.dsw.sgweb.generator.builder;

import com.dsw.core.util.builder.SourceBuilder;
import com.dsw.core.util.builder.SourceBuilder.Utility;
import com.dsw.core.util.builder.SourceBuilderProperties;
import com.dsw.core.util.builder.SourceBuilderUtil;
import com.dsw.core.util.builder.template.JspSourceTemplate;

public class JsAppBuilder implements SourceBuilder {
	/**
	 * Template 파일 (tpl)
	 */
	public static final String TEMPLATE = "/static/tpl/JsAppTemplate.template";

	/**
	 * Template Loading Encoding
	 */
	private final String ENCODING = "UTF-8";

	/**
	 * Generated XML Source
	 */
	private StringBuffer source;

	public static final String PROP_SQL_SOURCE = "PROP_SQL_SOURCE";

	public static final String PROP_MODULE_PACKAGE_NAME = "PROP_MODULE_PACKAGE_NAME";
	public static final String PROP_SVC_CLASS_NAME = "PROP_SVC_CLASS_NAME";

	public static final String PROP_MODULE_KOREAN = "PROP_MODULE_KOREAN";

	public static final String PROP_BASE_PACKAGE_NAME = "PROP_BASE_PACKAGE_NAME";

	/* (non-Javadoc)
	 * @see com.dsw.core.util.builder.SourceBuilder#build(com.dsw.core.util.builder.SourceBuilderProperties)
	 */
	public void build(SourceBuilderProperties<String, Object> properties) throws Exception {
		String modulePackage = properties.getString(PROP_MODULE_PACKAGE_NAME);
		String moduleKorean = properties.getString(PROP_MODULE_KOREAN);
		String svcClass = properties.getString(PROP_SVC_CLASS_NAME);
		String controllerMapName = modulePackage.replaceAll("\\.", "\\/");
		String jspPrefix = Utility.toFieldName(svcClass.replace("Controller", ""));

		String basePackage = properties.getString(PROP_BASE_PACKAGE_NAME);

//		SqlSource sqlSource = (SqlSource)properties.get(PROP_SQL_SOURCE);

//		String[] javaKeyFields = sqlSource.getJavaKeyFields();
//		String[] javaKeyTypes = sqlSource.getJavaKeyTypes();


		JspSourceTemplate template = new JspSourceTemplate(TEMPLATE);
		template.loadTemplate(ENCODING);

		final String rootDefine = template.getRootDefine("root");

		StringBuffer source = new StringBuffer();

		//Create ROOT
		String rootNode = rootDefine;
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "modulePackage", modulePackage);
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "svcClass", svcClass);
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "controllerMapName", controllerMapName);
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "jspPrefix", jspPrefix);
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "moduleKorean", moduleKorean);

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