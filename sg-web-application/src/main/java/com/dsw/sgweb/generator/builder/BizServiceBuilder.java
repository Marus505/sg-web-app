package com.dsw.sgweb.generator.builder;

import com.dsw.core.util.builder.SourceBuilder;
import com.dsw.core.util.builder.SourceBuilderProperties;
import com.dsw.core.util.builder.SourceBuilderUtil;
import com.dsw.core.util.builder.template.JavaSourceTemplate;
import com.dsw.core.util.db.sql.builder.SqlSource;

/**
 * Description :
 * @author YSKim
 * @since 2013. 11. 7.
 */
public class BizServiceBuilder implements SourceBuilder, JavaSourceBuilder {

	/**
	 * Template 파일 (tpl)
	 */
	public static final String TEMPLATE = "/static/tpl/BizServiceTemplate.template";

	/**
	 * Template Loading Encoding
	 */
	private final String ENCODING = "UTF-8";
	/**
	 * Generated SqlSource
	 */
	public static final String PROP_SQL_SOURCE = "PROP_SQL_SOURCE";

	public static final String PROP_PROJECT_NAME = "PROP_PROJECT_NAME";

	public static final String PROP_BUSINESS_PACKAGE_NAME = "PROP_BUSINESS_PACKAGE_NAME";
	public static final String PROP_BUSINESS_CLASS_NAME = "PROP_BUSINESS_CLASS_NAME";
	public static final String PROP_DOMAIN_PACKAGE_NAME = "PROP_DOMAIN_PACKAGE_NAME";
	public static final String PROP_DOMAIN_CLASS_NAME = "PROP_DOMAIN_CLASS_NAME";
	public static final String PROP_BUSINESS_ENGLISH_NAME = "PROP_BUSINESS_ENGLISH_NAME";
	public static final String PROP_BUSINESS_KOREAN_NAME = "PROP_BUSINESS_KOREAN_NAME";
	public static final String PROP_AUTHOR = "PROP_AUTHOR";

	public static final String EXCEPTION = "Exception";

	private String source;

	public void build(SourceBuilderProperties<String, Object> properties) throws Exception {
		String businessPackage = properties.getString(PROP_BUSINESS_PACKAGE_NAME);
		String businessClass = properties.getString(PROP_BUSINESS_CLASS_NAME);
		String domainPackage = properties.getString(PROP_DOMAIN_PACKAGE_NAME);
		String domainClass = properties.getString(PROP_DOMAIN_CLASS_NAME);
		String businessKoreanName = properties.getString(PROP_BUSINESS_KOREAN_NAME);
		String author = properties.getString(PROP_AUTHOR);
		String businessEnglishName = properties.getString(PROP_BUSINESS_ENGLISH_NAME);

		SqlSource sqlSource = (SqlSource)properties.get(PROP_SQL_SOURCE);

		String[] javaKeyFields = sqlSource.getJavaKeyFields();
		String[] javaKeyTypes = sqlSource.getJavaKeyTypes();

		JavaSourceTemplate template = new JavaSourceTemplate(TEMPLATE);
		template.loadTemplate(ENCODING);
		String createDate = template.creationDate();

		final String importDefine = template.getImportDefine("Type1");
		final String classDefine = template.getClassDefine("Type1");
		final String getCountMethodDefine = template.getMethodDefine("Type1");
		final String getMethodDefine = template.getMethodDefine("Type2");
		final String getListMethodDefine = template.getMethodDefine("Type3");
		final String insertMethodDefine = template.getMethodDefine("Type4");
		final String updateMethodDefine = template.getMethodDefine("Type5");
		final String deleteMethodDefine = template.getMethodDefine("Type6");

		String importArea = createImportAreaSource(importDefine, businessPackage, businessClass, domainPackage, domainClass);

		String getCountMethod = createGetMethodSource(getCountMethodDefine, businessKoreanName, businessEnglishName, domainClass, javaKeyFields, javaKeyTypes);

		String getMethod = createGetMethodSource(getMethodDefine, businessKoreanName, businessEnglishName, domainClass, javaKeyFields, javaKeyTypes);

		String getListMethod = createGetListMethodSource(getListMethodDefine, businessKoreanName, businessEnglishName,  domainClass, javaKeyFields, javaKeyTypes);

		String insertMethod = createSetMethodSource(insertMethodDefine, businessKoreanName, businessEnglishName, domainClass, javaKeyFields, javaKeyTypes);

		String updateMethod = createSetMethodSource(updateMethodDefine, businessKoreanName, businessEnglishName, domainClass, javaKeyFields, javaKeyTypes);

		String deleteMethod = createSetMethodSource(deleteMethodDefine, businessKoreanName, businessEnglishName, domainClass, javaKeyFields, javaKeyTypes);

		StringBuffer methods = new StringBuffer();
		methods.append(getCountMethod);
		methods.append(CRLF);
		methods.append(getMethod);
		methods.append(CRLF);
		methods.append(getListMethod);
		methods.append(CRLF);
		methods.append(insertMethod);
		methods.append(CRLF);
		methods.append(updateMethod);
		methods.append(CRLF);
		methods.append(deleteMethod);

		String classArea = createClassAreaSource(classDefine, createDate, businessEnglishName, businessPackage, businessClass, sqlSource.getTableName(), businessKoreanName, author);
		classArea = SourceBuilderUtil.replaceVariables(classArea, IMPORTS, importArea);
		classArea = SourceBuilderUtil.replaceVariables(classArea, METHODS, methods.toString());

		this.source = classArea;
	}

	public String getSourceString() {
		return this.source;
	}

	private String createImportAreaSource(final String importDefine, String repositoryPackage, String businessClass, String domainPackage, String domainClass){
		String source = importDefine;
		String importStrings = "";
		importStrings += "import " + domainPackage + "." + domainClass + ";";
		source = SourceBuilderUtil.replaceVariables(source, IMPORTS, importStrings);
		return source;
	}

	public String createClassAreaSource(final String classDefine, String createDate, String businessEnglishName, String packageName, String className, String tableName, String businessKoreanName, String author){
		String source = classDefine;
		source = SourceBuilderUtil.replaceVariables(source, AUTHOR, author);
		source = SourceBuilderUtil.replaceVariables(source, CREATION_DATE, createDate);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, TABLE_NAME, tableName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, PACKAGE, packageName);
		source = SourceBuilderUtil.replaceVariables(source, CLASS_NAME, className);
		return source;
	}

	private String createGetMethodSource(final String selectDefine, String businessKoreanName, String businessEnglishName, String domainClass, String[] javaKeyFields, String[] javaKeyTypes){
		String source = selectDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, THROWS, EXCEPTION);
		if(javaKeyFields.length > 0){
			String parameterTypeAndVars = "";
			String[] parameterTypes = new String[javaKeyFields.length];
			for (int i = 0; i < javaKeyTypes.length; i++) {
				parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
				parameterTypes[i] = javaKeyFields[i];
				if(i < javaKeyFields.length -1){
					parameterTypeAndVars += ", ";
				}
			}
			source = SourceBuilderUtil.replaceIterates(source, "@param", parameterTypes);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPE_AND_VARS, parameterTypeAndVars);
		}else{
			source = SourceBuilderUtil.removeIterates(source, "@param");
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPE_AND_VARS);
		}
		return source;
	}

	private String createGetListMethodSource(final String selectListDefine, String businessKoreanName, String businessEnglishName, String domainClass, String[] javaKeyFields, String[] javaKeyTypes){
		String source = selectListDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, THROWS, EXCEPTION);
		if(javaKeyFields.length > 1){
			String parameterTypeAndVars = "";
			String[] parameterTypes = new String[javaKeyFields.length-1];
			for (int i = 0; i < javaKeyTypes.length-1; i++) {
				parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
				parameterTypes[i] = javaKeyFields[i];
				if(i < javaKeyFields.length -2){
					parameterTypeAndVars += ", ";
				}
			}
			source = SourceBuilderUtil.replaceIterates(source, "@param", parameterTypes);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPE_AND_VARS, parameterTypeAndVars);
		}else{
			source = SourceBuilderUtil.removeIterates(source, "@param");
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPE_AND_VARS);
		}
		return source;
	}

	private String createSetMethodSource(final String insertDefine, String businessKoreanName, String businessEnglishName, String domainClass, String[] javaKeyFields, String[] javaKeyTypes){
		String source = insertDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, THROWS, EXCEPTION);
		if(javaKeyFields.length > 0){
			String parameterTypeAndVars = "";
			String[] parameterTypes = new String[javaKeyFields.length-1];
			for (int i = 0; i < javaKeyTypes.length; i++) {
				if(javaKeyFields.length-1 > i){
					parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
					parameterTypes[i] = javaKeyFields[i];
					parameterTypeAndVars += ", ";
				}
			}
			source = SourceBuilderUtil.replaceIterates(source, "@param", parameterTypes);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPE_AND_VARS, parameterTypeAndVars);
		}else{
			source = SourceBuilderUtil.removeIterates(source, "@param");
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPE_AND_VARS);
		}
		return source;
	}

}
