package com.dsw.sgweb.generator.builder;

import com.dsw.core.util.builder.SourceBuilder;
import com.dsw.core.util.builder.SourceBuilderProperties;
import com.dsw.core.util.builder.SourceBuilderUtil;
import com.dsw.core.util.builder.template.JavaSourceTemplate;
import com.dsw.core.util.db.sql.builder.SqlSource;

/**
 * Class Name : ControllerBuilder.java
 * Description : ControllerBuilder class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public class ControllerBuilder implements JavaSourceBuilder, SourceBuilder {

	/**
	 * Template 파일 (tpl)
	 */
	public static final String TEMPLATE = "/static/tpl/ControllerTemplate.template";

	/**
	 * Template Loading Encoding
	 */
	private final String ENCODING = "UTF-8";

	/**
	 * Generated SqlSource
	 */
	public static final String PROP_SQL_SOURCE = "PROP_SQL_SOURCE";
	public static final String PROP_PROJECT_NAME = "PROP_PROJECT_NAME";
	public static final String PROP_MODULE_PACKAGE_NAME = "PROP_MODULE_PACKAGE_NAME";
	public static final String PROP_SVC_PACKAGE_NAME = "PROP_SVC_PACKAGE_NAME";
	public static final String PROP_SVC_CLASS_NAME = "PROP_SVC_CLASS_NAME";
	public static final String PROP_BUSINESS_PACKAGE_NAME = "PROP_BUSINESS_PACKAGE_NAME";
	public static final String PROP_BUSINESS_CLASS_NAME = "PROP_BUSINESS_CLASS_NAME";
	public static final String PROP_DOMAIN_PACKAGE_NAME = "PROP_DOMAIN_PACKAGE_NAME";
	public static final String PROP_DOMAIN_CLASS_NAME = "PROP_DOMAIN_CLASS_NAME";
	public static final String PROP_BUSINESS_ENGLISH_NAME = "PROP_BUSINESS_ENGLISH_NAME";
	public static final String PROP_BUSINESS_KOREAN_NAME = "PROP_BUSINESS_KOREAN_NAME";
	public static final String PROP_BASE_PACKAGE_NAME = "PROP_BASE_PACKAGE_NAME";
	public static final String PROP_AUTHOR = "PROP_AUTHOR";

	private String source;

	/* (None Javadoc)
	 * @see com.dsw.core.util.builder.SourceBuilder#build(com.dsw.core.util.builder.SourceBuilderProperties)
	 */
	public void build(SourceBuilderProperties<String, Object> properties) throws Exception {
		String modulePackage = properties.getString(PROP_MODULE_PACKAGE_NAME);
		String svcPackage = properties.getString(PROP_SVC_PACKAGE_NAME);
		String svcClass = properties.getString(PROP_SVC_CLASS_NAME);
		String businessPackage = properties.getString(PROP_BUSINESS_PACKAGE_NAME);
		String businessClass = properties.getString(PROP_BUSINESS_CLASS_NAME);
		String domainPackage = properties.getString(PROP_DOMAIN_PACKAGE_NAME);
		String domainClass = properties.getString(PROP_DOMAIN_CLASS_NAME);
		String businessKoreanName = properties.getString(PROP_BUSINESS_KOREAN_NAME);
		String author = properties.getString(PROP_AUTHOR);
		String businessEnglishName = properties.getString(PROP_BUSINESS_ENGLISH_NAME);
		String basePackage = properties.getString(PROP_BASE_PACKAGE_NAME);
		String controllerMapName = domainClass.toLowerCase().replaceAll("\\.", "\\/");
		String jspPrefix = Utility.toFieldName(svcClass.replace("Controller", ""));

		SqlSource sqlSource = (SqlSource)properties.get(PROP_SQL_SOURCE);

		String[] javaKeyFields = sqlSource.getJavaKeyFields();
		String[] javaKeyTypes = sqlSource.getJavaKeyTypes();

		JavaSourceTemplate template = new JavaSourceTemplate(TEMPLATE);
		template.loadTemplate(ENCODING);
		String createDate = template.creationDate();

		final String importDefine = template.getImportDefine("Type1");
		final String classDefine = template.getClassDefine("Type1");

		final String getListMethodDefine = template.getMethodDefine("Type1");
		final String getMethodDefine = template.getMethodDefine("Type2");
		final String insertViewMethodDefine = template.getMethodDefine("Type3");
		final String insertMethodDefine = template.getMethodDefine("Type4");
		final String updateViewMethodDefine = template.getMethodDefine("Type5");
		final String updateMethodDefine = template.getMethodDefine("Type6");
		final String deleteMethodDefine = template.getMethodDefine("Type7");
		final String excelMethodDefine = template.getMethodDefine("Type8");


		String importArea = createImportAreaSource(importDefine, svcPackage, svcClass, businessPackage, businessClass, domainPackage, domainClass, basePackage, modulePackage);

		String getListMethod = createCommonMethodSource(getListMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainClass, svcClass, controllerMapName, jspPrefix, javaKeyFields, javaKeyTypes);

		String getMethod = createCommonMethodSource(getMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainClass, svcClass, controllerMapName, jspPrefix, javaKeyFields, javaKeyTypes);

		String insertViewMethod = createCommonMethodSource(insertViewMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainClass, svcClass, controllerMapName, jspPrefix, javaKeyFields, javaKeyTypes);

		String insertMethod = createCommonMethodSource(insertMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainClass, svcClass, controllerMapName, jspPrefix, javaKeyFields, javaKeyTypes);

		String updateViewMethod = createCommonMethodSource(updateViewMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainClass, svcClass, controllerMapName, jspPrefix, javaKeyFields, javaKeyTypes);

		String updateMethod = createCommonMethodSource(updateMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainClass, svcClass, controllerMapName, jspPrefix, javaKeyFields, javaKeyTypes);

		String deleteMethod = createCommonMethodSource(deleteMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainClass, svcClass, controllerMapName, jspPrefix, javaKeyFields, javaKeyTypes);

		String excelMethod = createCommonMethodSource(excelMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainClass, svcClass, controllerMapName, jspPrefix, javaKeyFields, javaKeyTypes);


		StringBuffer methods = new StringBuffer();
		methods.append(getListMethod);
		methods.append(CRLF);
		methods.append(getMethod);
		methods.append(CRLF);
		methods.append(insertViewMethod);
		methods.append(CRLF);
		methods.append(insertMethod);
		methods.append(CRLF);
		methods.append(updateViewMethod);
		methods.append(CRLF);
		methods.append(updateMethod);
		methods.append(CRLF);
		methods.append(deleteMethod);
		methods.append(CRLF);
		methods.append(excelMethod);

		String classArea = createClassAreaSource(classDefine, createDate, businessEnglishName, svcPackage, svcClass, sqlSource.getTableName(), businessKoreanName, businessClass, author, controllerMapName, jspPrefix);
		classArea = SourceBuilderUtil.replaceVariables(classArea, IMPORTS, importArea);
		classArea = SourceBuilderUtil.replaceVariables(classArea, METHODS, methods.toString());

		this.source = classArea;
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.builder.SourceBuilder#getSourceString()
	 */
	public String getSourceString() {
		return this.source;
	}

	/**
	 * @param importDefine
	 * @param businessSvcPackage
	 * @param businessSvcClass
	 * @param businessPackage
	 * @param businessClass
	 * @param domainPackage
	 * @param domainClass
	 * @return String
	 */
	private String createImportAreaSource(final String importDefine, String businessSvcPackage, String businessSvcClass, String businessPackage, String businessClass, String domainPackage, String domainClass, String basePackage, String modulePackage) {
		String source = importDefine;
		String importStrings = "";
		importStrings += "import " + domainPackage + "." + domainClass + ";" + CRLF;
		importStrings += "import " + businessPackage + "." + businessClass + ";";
		source = SourceBuilderUtil.replaceVariables(source, IMPORTS, importStrings);
		source = SourceBuilderUtil.replaceVariables(source, BASE_PACKAGE, basePackage);
		source = SourceBuilderUtil.replaceVariables(source, MODULE_PACKAGE, modulePackage);
		return source;
	}

	/**
	 *
	 * @param classDefine
	 * @param createDate
	 * @param businessEnglishName
	 * @param packageName
	 * @param className
	 * @param tableName
	 * @param businessKoreanName
	 * @param businessClass
	 * @param author
	 * @param controllerMapName
	 * @param jspPrefix
	 * @return String
	 */
	public String createClassAreaSource(final String classDefine, String createDate, String businessEnglishName, String packageName, String className, String tableName, String businessKoreanName, String businessClass, String author, String controllerMapName, String jspPrefix){
		String source = classDefine;
		source = SourceBuilderUtil.replaceVariables(source, AUTHOR, author);
		source = SourceBuilderUtil.replaceVariables(source, CREATION_DATE, createDate);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, TABLE_NAME, tableName);
		source = SourceBuilderUtil.replaceVariables(source, PACKAGE, packageName);
		source = SourceBuilderUtil.replaceVariables(source, CLASS_NAME, className);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_CLASS, businessClass);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_FIELD, Utility.toFieldName(businessClass));
		source = SourceBuilderUtil.replaceVariables(source, CONTROLLER_MAP_NAME, controllerMapName);
		source = SourceBuilderUtil.replaceVariables(source, JSP_PREFIX, jspPrefix);
		return source;
	}

	/**
	 *
	 * @param getDefine
	 * @param businessKoreanName
	 * @param businessEnglishName
	 * @param businessPackage
	 * @param businessClass
	 * @param domainClass
	 * @param svcClass
	 * @param controllerMapName
	 * @param jspPrefix
	 * @param javaKeyFields
	 * @param javaKeyTypes
	 * @return String
	 */
	private String createCommonMethodSource(final String getDefine, String businessKoreanName, String businessEnglishName, String businessPackage, String businessClass, String domainClass, String svcClass, String controllerMapName, String jspPrefix, String[] javaKeyFields, String[] javaKeyTypes){
		String source = getDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_PACKAGE, businessPackage);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_CLASS, businessClass);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_FIELD, Utility.toFieldName(businessClass));
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, CONTROLLER_MAP_NAME, controllerMapName);
		source = SourceBuilderUtil.replaceVariables(source, JSP_PREFIX, jspPrefix);
		source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPES);
		source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPE_AND_VARS);
		source = SourceBuilderUtil.removeVariables(source, PARAMETER_VARS);
		return source;
	}

}
