package com.dsw.sgweb.generator.builder;

import com.dsw.core.util.builder.SourceBuilder;
import com.dsw.core.util.builder.SourceBuilderProperties;
import com.dsw.core.util.builder.SourceBuilderUtil;
import com.dsw.core.util.builder.template.JavaSourceTemplate;
import com.dsw.core.util.db.sql.builder.SqlSource;

/**
 * Class Name : RepositoryBuilder.java
 * Description : RepositoryBuilder class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public class RepositoryBuilder implements SourceBuilder, JavaSourceBuilder {

	/**
	 * Template 파일 (tpl)
	 */
	public static final String TEMPLATE = "/static/tpl/RepositoryTemplate.template";

	public final static int BUILD_OPTION_READ = 1;
	public final static int BUILD_OPTION_WRITE = 2;
	public final static int BUILD_OPTION_BOTH= 0;

	/**
	 * Template Loading Encoding
	 */
	private final String ENCODING = "UTF-8";

	public static final String PROP_SQL_SOURCE = "PROP_SQL_SOURCE";
	public static final String PROP_REPOSITORY_PACKAGE_NAME = "PROP_REPOSITORY_PACKAGE_NAME";
	public static final String PROP_REPOSITORY_CLASS_NAME = "PROP_REPOSITORY_CLASS_NAME";
	public static final String PROP_DOMAIN_PACKAGE_NAME = "PROP_DOMAIN_PACKAGE_NAME";
	public static final String PROP_DOMAIN_CLASS_NAME = "PROP_DOMAIN_CLASS_NAME";
	public static final String PROP_BUSINESS_ENGLISH_NAME = "PROP_BUSINESS_ENGLISH_NAME";
	public static final String PROP_BUSINESS_KOREAN_NAME = "PROP_BUSINESS_KOREAN_NAME";
	public static final String PROP_AUTHOR = "PROP_AUTHOR";
	public static final String PROP_BUILD_OPTION = "PROP_BUILD_OPTION";
	public static final String PROP_BASE_PACKAGE_NAME = "PROP_BASE_PACKAGE_NAME";
	public static final String EXCEPTION = "Exception";

	private String source;

	/* (non-Javadoc)
	 * @see com.dsw.core.util.builder.SourceBuilder#build(com.dsw.core.util.builder.SourceBuilderProperties)
	 */
	public void build(SourceBuilderProperties<String, Object> properties) throws Exception {
		String repositoryPackage = properties.getString(PROP_REPOSITORY_PACKAGE_NAME);
		String repositoryClass = properties.getString(PROP_REPOSITORY_CLASS_NAME);
		String domainPackage = properties.getString(PROP_DOMAIN_PACKAGE_NAME);
		String domainClass = properties.getString(PROP_DOMAIN_CLASS_NAME);
		String domainName = Utility.toFieldName(domainClass);
		String businessEnglishName = properties.getString(PROP_BUSINESS_ENGLISH_NAME);
		String businessKoreanName = properties.getString(PROP_BUSINESS_KOREAN_NAME);
		String author = properties.getString(PROP_AUTHOR);
		String basePackage = properties.getString(PROP_BASE_PACKAGE_NAME);
		int buildOption = properties.getInt(PROP_BUILD_OPTION, 0);

		SqlSource sqlSource = (SqlSource)properties.get(PROP_SQL_SOURCE);

		String[] javaKeyFields = sqlSource.getJavaKeyFields();
		String[] javaKeyTypes = sqlSource.getJavaKeyTypes();

		JavaSourceTemplate template = new JavaSourceTemplate(TEMPLATE);
		template.loadTemplate(ENCODING);
		String createDate = template.creationDate();

		final String importDefine = template.getImportDefine("Type" + (buildOption == 1 || buildOption == 0 ? "1" : "2") );
		final String classDefine = template.getClassDefine("Type1");
		final String listDefine = template.getMethodDefine("Type1");
		final String selectDefine = template.getMethodDefine("Type2");
		final String insertDefine = template.getMethodDefine("Type3");
		final String updateDefine = template.getMethodDefine("Type4");
		final String deleteDefine = template.getMethodDefine("Type5");

		String importArea = createImportAreaSource(importDefine, repositoryPackage, repositoryClass, domainPackage, domainClass, basePackage);

		String listMethod = createSelectListMethodSource(listDefine, businessKoreanName, businessEnglishName, domainClass, javaKeyFields, javaKeyTypes);

		String selectMethod = createSelectMethodSource(selectDefine, businessKoreanName, businessEnglishName, domainClass, javaKeyFields, javaKeyTypes);

		String insertMethod = createInsertMethodSource(insertDefine, businessKoreanName, businessEnglishName, domainClass, domainName, javaKeyFields, javaKeyTypes);

		String updateMethod = createUpdateMethodSource(updateDefine, businessKoreanName, businessEnglishName, domainClass, domainName, javaKeyFields, javaKeyTypes);

		String deleteMethod = createDeleteMethodSource(deleteDefine, businessKoreanName, businessEnglishName, domainClass, domainName, javaKeyFields, javaKeyTypes);

		StringBuffer methods = new StringBuffer();
		if(buildOption == 1 || buildOption == 0)
			methods.append(CRLF).append(listMethod);
		if(buildOption == 1 || buildOption == 0)
			methods.append(CRLF).append(selectMethod);
		if(buildOption == 2 || buildOption == 0)
			methods.append(CRLF).append(insertMethod);
		if(buildOption == 2 || buildOption == 0)
			methods.append(CRLF).append(updateMethod);
		if(buildOption == 2 || buildOption == 0)
			methods.append(CRLF).append(deleteMethod);

		String classArea = createClassAreaSource(classDefine, createDate, repositoryPackage, repositoryClass, businessEnglishName, businessKoreanName, author);
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
	 *
	 * @param importDefine
	 * @param repositoryPackage
	 * @param repositoryClass
	 * @param domainPackage
	 * @param domainClass
	 * @return String
	 */
	private String createImportAreaSource(final String importDefine, String repositoryPackage, String repositoryClass, String domainPackage, String domainClass, String basePackage){
		String source = importDefine;
		String importStrings = "";
		importStrings += "import " + domainPackage + "." + domainClass + ";";
		source = SourceBuilderUtil.replaceVariables(source, IMPORTS, importStrings);
		source = SourceBuilderUtil.replaceVariables(source, BASE_PACKAGE, basePackage);
		return source;
	}

	/**
	 *
	 * @param classDefine
	 * @param createDate
	 * @param packageName
	 * @param className
	 * @param businessKoreanName
	 * @param author
	 * @return String
	 */
	public String createClassAreaSource(final String classDefine, String createDate, String packageName, String className, String businessEnglishName, String businessKoreanName, String author){
		String source = classDefine;
		source = SourceBuilderUtil.replaceVariables(source, AUTHOR, author);
		source = SourceBuilderUtil.replaceVariables(source, CREATION_DATE, createDate);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, PACKAGE, packageName);
		source = SourceBuilderUtil.replaceVariables(source, CLASS_NAME, className);
		return source;
	}

	/**
	 *
	 * @param selectDefine
	 * @param businessKoreanName
	 * @param businessEnglishName
	 * @param domainClass
	 * @param javaKeyFields
	 * @param javaKeyTypes
	 * @return String
	 */
	private String createSelectMethodSource(final String selectDefine, String businessKoreanName, String businessEnglishName, String domainClass, String[] javaKeyFields, String[] javaKeyTypes){
		String source = selectDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, THROWS, EXCEPTION);
		if(javaKeyFields.length > 0){
			String parameterVars = "";
			String parameterTypeAndVars = "";
			String[] parameterTypes = new String[javaKeyFields.length];
			for (int i = 0; i < javaKeyTypes.length; i++) {
				parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
				parameterVars += javaKeyFields[i];
				parameterTypes[i] = javaKeyFields[i];
				if(i < javaKeyFields.length - 1){
					parameterVars += ", ";
					parameterTypeAndVars += ", ";
				}
			}
			source = SourceBuilderUtil.replaceIterates(source, "@param", parameterTypes);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_VARS, parameterVars);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPE_AND_VARS, parameterTypeAndVars);
		}else{
			source = SourceBuilderUtil.removeIterates(source, "@param");
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_VARS);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPE_AND_VARS);
		}
		return source;
	}

	/**
	 *
	 * @param selectListDefine
	 * @param businessKoreanName
	 * @param businessEnglishName
	 * @param domainClass
	 * @param javaKeyFields
	 * @param javaKeyTypes
	 * @return String
	 */
	private String createSelectListMethodSource(final String selectListDefine, String businessKoreanName, String businessEnglishName, String domainClass, String[] javaKeyFields, String[] javaKeyTypes){
		String source = selectListDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, THROWS, EXCEPTION);
		if(javaKeyFields.length > 1){
			String parameterVars = "";
			String parameterTypeAndVars = "";
			String[] parameterTypes = new String[javaKeyFields.length-1];
			for (int i = 0; i < javaKeyTypes.length-1; i++) {
				parameterVars += javaKeyFields[i];
				parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
				parameterTypes[i] = javaKeyFields[i];
				if(i < javaKeyFields.length - 1){
					parameterVars += ", ";
					parameterTypeAndVars += ", ";
				}
			}
			source = SourceBuilderUtil.replaceIterates(source, "@param", parameterTypes);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_VARS, parameterVars);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPE_AND_VARS, parameterTypeAndVars);
		}else{
			source = SourceBuilderUtil.removeIterates(source, "@param");
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_VARS);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPE_AND_VARS);
		}
		return source;
	}

	/**
	 *
	 * @param insertDefine
	 * @param businessKoreanName
	 * @param businessEnglishName
	 * @param domainClass
	 * @param javaKeyFields
	 * @param javaKeyTypes
	 * @return String
	 */
	private String createInsertMethodSource(final String insertDefine, String businessKoreanName, String businessEnglishName, String domainClass, String domainName, String[] javaKeyFields, String[] javaKeyTypes){
		String source = insertDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_NAME, domainName);
		source = SourceBuilderUtil.replaceVariables(source, THROWS, EXCEPTION);
		if(javaKeyFields.length > 0){
			String parameterTypeAndVars = "";
			String[] parameterTypes = new String[javaKeyFields.length];
			for (int i = 0; i < javaKeyTypes.length; i++) {
				parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
				parameterTypes[i] = javaKeyFields[i];
				if(i < javaKeyFields.length - 1){
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

	/**
	 *
	 * @param updateDefine
	 * @param businessKoreanName
	 * @param businessEnglishName
	 * @param domainClass
	 * @param javaKeyFields
	 * @param javaKeyTypes
	 * @return String
	 */
	private String createUpdateMethodSource(final String updateDefine, String businessKoreanName, String businessEnglishName, String domainClass, String domainName, String[] javaKeyFields, String[] javaKeyTypes){
		String source = updateDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_NAME, domainName);
		source = SourceBuilderUtil.replaceVariables(source, THROWS, EXCEPTION);
		if(javaKeyFields.length > 0){
			String parameterTypeAndVars = "";
			String[] parameterTypes = new String[javaKeyFields.length];
			for (int i = 0; i < javaKeyTypes.length; i++) {
				parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
				parameterTypes[i] = javaKeyFields[i];
				if(i < javaKeyFields.length - 1){
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

	/**
	 *
	 * @param deleteDefine
	 * @param businessKoreanName
	 * @param businessEnglishName
	 * @param javaKeyFields
	 * @param javaKeyTypes
	 * @return String
	 */
	private String createDeleteMethodSource(final String deleteDefine, String businessKoreanName, String businessEnglishName, String domainClass, String domainName, String[] javaKeyFields, String[] javaKeyTypes){
		String source = deleteDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_NAME, domainName);
		source = SourceBuilderUtil.replaceVariables(source, THROWS, EXCEPTION);
		if(javaKeyFields.length > 0){
			String parameterVars = "";
			String parameterTypeAndVars = "";
			String[] parameterTypes = new String[javaKeyFields.length];
			for (int i = 0; i < javaKeyTypes.length; i++) {
				parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
				parameterVars += javaKeyFields[i];
				parameterTypes[i] = javaKeyFields[i];
				if(i < javaKeyFields.length - 1){
					parameterVars += ", ";
					parameterTypeAndVars += ", ";
				}
			}
			source = SourceBuilderUtil.replaceIterates(source, "@param", parameterTypes);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_VARS, parameterVars);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPE_AND_VARS, parameterTypeAndVars);
		}else{
			source = SourceBuilderUtil.removeIterates(source, "@param");
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_VARS);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPE_AND_VARS);
		}
		return source;
	}

}
