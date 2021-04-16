package com.dsw.sgweb.generator.builder;

import com.dsw.core.util.builder.SourceBuilder;
import com.dsw.core.util.builder.SourceBuilderProperties;
import com.dsw.core.util.builder.SourceBuilderUtil;
import com.dsw.core.util.builder.template.JavaSourceTemplate;
import com.dsw.core.util.db.sql.builder.FieldExpression;
import com.dsw.core.util.db.sql.builder.SqlSource;
import org.apache.commons.lang3.StringUtils;

/**
 * Class Name : BizServiceImplBuilder.java
 * Description : BizServiceImplBuilder class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public class BizServiceImplBuilder implements JavaSourceBuilder, SourceBuilder {

	/**
	 * Template 파일 (tpl)
	 */
	public static final String TEMPLATE = "/static/tpl/BizServiceImplTemplate.template";

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
	public static final String PROP_REPOSITORY_PACKAGE_NAME = "PROP_REPOSITORY_PACKAGE_NAME";
	public static final String PROP_REPOSITORY_CLASS_NAME = "PROP_REPOSITORY_CLASS_NAME";
	public static final String PROP_DOMAIN_PACKAGE_NAME = "PROP_DOMAIN_PACKAGE_NAME";
	public static final String PROP_DOMAIN_CLASS_NAME = "PROP_DOMAIN_CLASS_NAME";
	public static final String PROP_BUSINESS_ENGLISH_NAME = "PROP_BUSINESS_ENGLISH_NAME";
	public static final String PROP_BUSINESS_KOREAN_NAME = "PROP_BUSINESS_KOREAN_NAME";
	public static final String PROP_BASE_PACKAGE_NAME = "PROP_BASE_PACKAGE_NAME";
	public static final String PROP_AUTHOR = "PROP_AUTHOR";

	public static final String EXCEPTION = "Exception";

	private String source;

	/* (None Javadoc)
	 * @see com.dsw.core.util.builder.SourceBuilder#build(com.dsw.core.util.builder.SourceBuilderProperties)
	 */
	public void build(SourceBuilderProperties<String, Object> properties) throws Exception {
		String businessPackage = properties.getString(PROP_BUSINESS_PACKAGE_NAME);
		String businessClass = properties.getString(PROP_BUSINESS_CLASS_NAME);
		String repositoryPackage = properties.getString(PROP_REPOSITORY_PACKAGE_NAME);
		String repositoryClass = properties.getString(PROP_REPOSITORY_CLASS_NAME);
		String domainPackage = properties.getString(PROP_DOMAIN_PACKAGE_NAME);
		String domainClass = properties.getString(PROP_DOMAIN_CLASS_NAME);
		String domainName = Utility.toFieldName(domainClass);
		String businessKoreanName = properties.getString(PROP_BUSINESS_KOREAN_NAME);
		String basePackage = properties.getString(PROP_BASE_PACKAGE_NAME);
		String author = properties.getString(PROP_AUTHOR);
		String businessEnglishName = properties.getString(PROP_BUSINESS_ENGLISH_NAME);

		SqlSource sqlSource = (SqlSource)properties.get(PROP_SQL_SOURCE);

		String[] javaKeyFields = sqlSource.getJavaKeyFields();
		String[] javaKeyTypes = sqlSource.getJavaKeyTypes();
		String[] variables = sqlSource.getJavaFields();
		String[] comments = sqlSource.getComments();

		JavaSourceTemplate template = new JavaSourceTemplate(TEMPLATE);
		template.loadTemplate(ENCODING);
		String createDate = template.creationDate();

		final String importDefine = template.getImportDefine("Type1");
		final String classDefine = template.getClassDefine("Type1");
		final String listMethodDefine = template.getMethodDefine("Type2");
		final String excelMethodDefine = template.getMethodDefine("Type3");
		final String getMethodDefine = template.getMethodDefine("Type4");
		final String insertMethodDefine = template.getMethodDefine("Type5");
		final String updateMethodDefine = template.getMethodDefine("Type6");
		final String deleteMethodDefine = template.getMethodDefine("Type7");


		String importArea = createImportAreaSource(importDefine, repositoryPackage, repositoryClass, businessPackage, businessClass, domainPackage, domainClass, basePackage);

		String listMethod = createGetListMethodSource(listMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainClass, repositoryClass, javaKeyFields, javaKeyTypes);

		String excelMethod = createGetExcelMethodSource(excelMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainClass, repositoryClass, variables, comments);

		String getMethod = createGetMethodSource(getMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainClass, repositoryClass, javaKeyFields, javaKeyTypes);

		String insertMethod = createSetMethodSource(insertMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainPackage, domainClass, domainName, repositoryClass, javaKeyFields, javaKeyTypes);

		String updateMethod = createSetMethodSource(updateMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainPackage, domainClass, domainName, repositoryClass, javaKeyFields, javaKeyTypes);

		String deleteMethod = createSetMethodSource(deleteMethodDefine, businessKoreanName, businessEnglishName, businessPackage, businessClass, domainPackage, domainClass, domainName, repositoryClass, javaKeyFields, javaKeyTypes);

		StringBuffer methods = new StringBuffer();
		methods.append(listMethod);
		methods.append(CRLF);
		methods.append(excelMethod);
		methods.append(CRLF);
		methods.append(getMethod);
		methods.append(CRLF);
		methods.append(insertMethod);
		methods.append(CRLF);
		methods.append(updateMethod);
		methods.append(CRLF);
		methods.append(deleteMethod);

		String classArea = createClassAreaSource(classDefine, createDate, businessEnglishName, businessPackage, businessClass, sqlSource.getTableName(), businessKoreanName, author, javaKeyFields, javaKeyTypes, variables, comments);
		classArea = SourceBuilderUtil.replaceVariables(classArea, IMPORTS, importArea);
		classArea = SourceBuilderUtil.replaceVariables(classArea, METHODS, methods.toString());
		classArea = SourceBuilderUtil.replaceVariables(classArea, REPOSITORY_CLASS, repositoryClass);
		classArea = SourceBuilderUtil.replaceVariables(classArea, REPOSITORY_FIELD, Utility.toFieldName(repositoryClass));

		this.source = classArea;
	}

	public String getSourceString() {
		return this.source;
	}

	/**
	 * @param importDefine
	 * @param businessrepositoryPackage
	 * @param businessrepositoryClass
	 * @param businessPackage
	 * @param businessClass
	 * @param domainPackage
	 * @param domainClass
	 * @return String
	 */
	private String createImportAreaSource(final String importDefine, String businessrepositoryPackage, String businessrepositoryClass, String businessPackage, String businessClass, String domainPackage, String domainClass, String basePackage){
		String source = importDefine;
		String importStrings = "";
		importStrings += "import " + businessrepositoryPackage + "." + businessrepositoryClass + ";" + CRLF;
		importStrings += "import " + domainPackage + "." + domainClass + ";" + CRLF;
		source = SourceBuilderUtil.replaceVariables(source, IMPORTS, importStrings);
		source = SourceBuilderUtil.replaceVariables(source, BASE_PACKAGE, basePackage);
		return source;
	}

	/**
	 * @param classDefine
	 * @param createDate
	 * @param businessEnglishName
	 * @param packageName
	 * @param className
	 * @param tableName
	 * @param businessKoreanName
	 * @param author
	 * @return String
	 */
	public String createClassAreaSource(final String classDefine, String createDate, String businessEnglishName, String packageName, String className, String tableName, String businessKoreanName, String author, String[] javaKeyFields, String[] javaKeyTypes, String[] variables, String[] comments){
		String source = classDefine;
		source = SourceBuilderUtil.replaceVariables(source, AUTHOR, author);
		source = SourceBuilderUtil.replaceVariables(source, CREATION_DATE, createDate);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, TABLE_NAME, tableName);
		source = SourceBuilderUtil.replaceVariables(source, PACKAGE, packageName);
		source = SourceBuilderUtil.replaceVariables(source, CLASS_NAME, className);
		String parameterVars = "";
		if(javaKeyFields.length > 0){
			String parameterTypeAndVars = "";
			String parameterTypes = "";
			String parameterNames = "";
			for (int i = 0; i < javaKeyFields.length; i++) {
				if(i == javaKeyFields.length -1) {
					parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
					parameterTypes += javaKeyTypes[i];
					parameterVars += javaKeyFields[i];
					parameterNames += comments[i];
				}
			}
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPES, parameterTypes);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPE_AND_VARS, parameterTypeAndVars);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_VARS, parameterVars);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_NAMES, parameterNames);
		}else{
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPES);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPE_AND_VARS);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_VARS);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_NAMES);
		}
		String[][] params = new String[variables.length][2];
		for ( int i = 0; i < params.length; i++ ) {
			if ( parameterVars.equals(variables[i]) || isReservedExpression(variables[i]) ) {
				params[i] = null;
			} else {
				params[i][0] = variables[i];
				params[i][1] = comments[i];
			}
		}
		source = SourceBuilderUtil.replaceIterates(source, "variables", params);
		return source;
	}

	/**
	 * @param getDefine
	 * @param businessKoreanName
	 * @param businessEnglishName
	 * @param businessPackage
	 * @param businessClass
	 * @param domainClass
	 * @param repositoryClass
	 * @return String
	 */
	private String createGetMethodSource(final String getDefine, String businessKoreanName, String businessEnglishName, String businessPackage, String businessClass, String domainClass, String repositoryClass, String[] javaKeyFields, String[] javaKeyTypes){
		String source = getDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_PACKAGE, businessPackage);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_CLASS, businessClass);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, REPOSITORY_CLASS, Utility.toFieldName(repositoryClass));
		source = SourceBuilderUtil.replaceVariables(source, THROWS, EXCEPTION);
		if(javaKeyFields.length > 0){
			String parameterTypeAndVars = "";
			String parameterTypes = "";
			String parameterVars = "";
			for (int i = 0; i < javaKeyFields.length; i++) {
				parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
				parameterTypes += javaKeyTypes[i];
				parameterVars += javaKeyFields[i];
				if(i < javaKeyFields.length -1){
					parameterTypeAndVars += ", ";
					parameterTypes += ", ";
					parameterVars += ", ";
				}
			}
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPES, parameterTypes);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPE_AND_VARS, parameterTypeAndVars);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_VARS, parameterVars);
		}else{
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPES);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPE_AND_VARS);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_VARS);
		}
		return source;
	}

	/**
	 * @param getListDefine
	 * @param businessKoreanName
	 * @param businessEnglishName
	 * @param businessPackage
	 * @param businessClass
	 * @param domainClass
	 * @param repositoryClass
	 * @return String
	 */
	private String createGetListMethodSource(final String getListDefine, String businessKoreanName, String businessEnglishName, String businessPackage, String businessClass, String domainClass, String repositoryClass, String[] javaKeyFields, String[] javaKeyTypes){
		String source = getListDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_PACKAGE, businessPackage);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_CLASS, businessClass);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, THROWS, EXCEPTION);
		if(javaKeyFields.length > 1){
			String parameterTypeAndVars = "";
			String parameterTypes = "";
			String parameterVars = "";
			for (int i = 0; i < javaKeyFields.length; i++) {
				if(javaKeyFields.length-1 > i){
					parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
					parameterTypes += javaKeyTypes[i];
					parameterVars += javaKeyFields[i];
					if(i < javaKeyFields.length -2){
						parameterTypeAndVars += ", ";
						parameterTypes += ", ";
						parameterVars += ", ";
					}
				}
			}
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPES, parameterTypes);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPE_AND_VARS, parameterTypeAndVars);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_VARS, parameterVars);
		}else{
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPES);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPE_AND_VARS);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_VARS);
		}
		return source;
	}

	/**
	 * @param getExcelDefine
	 * @param businessKoreanName
	 * @param businessEnglishName
	 * @param businessPackage
	 * @param businessClass
	 * @param domainClass
	 * @param repositoryClass
	 * @return String
	 */
	private String createGetExcelMethodSource(final String getExcelDefine, String businessKoreanName, String businessEnglishName, String businessPackage, String businessClass, String domainClass, String repositoryClass, String[] fields, String[] comments){
		String source = getExcelDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_PACKAGE, businessPackage);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_CLASS, businessClass);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, THROWS, EXCEPTION);
		if(fields.length > 1){
			StringBuilder sb = new StringBuilder();
			int rowCnt = 0;
			for (int i = 0; i < fields.length; i++) {
				if (
						fields[i].equals(FieldExpression.RESERVED_EXPR_IS_REMOVED)
								|| fields[i].equals(FieldExpression.RESERVED_EXPR_IS_ENABLED)
				)
				{
					continue;
				}
				if(rowCnt > 0) {
					sb.append("\r\n        ");
				}
				sb.append(".addColumn(\"" + fields[i] + "\", \""+ comments[i] +"\")");
				rowCnt++;
			}
			source = SourceBuilderUtil.replaceVariables(source, "addColumns", sb.toString());
		}else{
			source = SourceBuilderUtil.removeVariables(source, "addColumns");
		}
		return source;
	}

	/**
	 * @param setDefine
	 * @param businessKoreanName
	 * @param businessEnglishName
	 * @param businessPackage
	 * @param businessClass
	 * @param domainPackage
	 * @param domainClass
	 * @param repositoryClass
	 * @param javaKeyFields
	 * @param javaKeyTypes
	 * @return String
	 */
	private String createSetMethodSource(final String setDefine, String businessKoreanName, String businessEnglishName, String businessPackage, String businessClass, String domainPackage, String domainClass, String domainName, String repositoryClass, String[] javaKeyFields, String[] javaKeyTypes){
		String source = setDefine;
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_PACKAGE, businessPackage);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_CLASS, businessClass);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_NAME, domainName);
		source = SourceBuilderUtil.replaceVariables(source, REPOSITORY_CLASS, Utility.toFieldName(repositoryClass));
		source = SourceBuilderUtil.replaceVariables(source, THROWS, EXCEPTION);
		if(javaKeyFields.length > 0){
			String parameterTypeAndVars = "";
			String parameterTypes = "";
			String parameterVars = "";
			String parameterNames = "";
			String[][] parameterKeyVariables = new String[javaKeyFields.length-1][4];
			String[][] voKeyVariables = new String[javaKeyFields.length][4];
			String[][] voKeyRemainVariable = new String[1][4];
			for (int i = 0; i < javaKeyFields.length; i++) {
				if(javaKeyFields.length-1 > i){
					parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
					parameterTypes += javaKeyTypes[i];
					parameterVars += javaKeyFields[i];
					parameterTypeAndVars += ", ";
					parameterTypes += ", ";
					parameterVars += ", ";
					parameterKeyVariables[i][0] = Utility.toSimpleClassName(javaKeyTypes[i]);
					parameterKeyVariables[i][1] = javaKeyFields[i];
					parameterKeyVariables[i][2] = javaKeyFields[i].substring(0, 1).toUpperCase() + javaKeyFields[i].substring(1);
					parameterKeyVariables[i][3] = javaKeyTypes[i].indexOf(".") > -1 ? "null" : "-1";
				}
				voKeyVariables[i][0] = Utility.toSimpleClassName(javaKeyTypes[i]);
				voKeyVariables[i][1] = javaKeyFields[i];
				voKeyVariables[i][2] = javaKeyFields[i].substring(0, 1).toUpperCase() + javaKeyFields[i].substring(1);
				voKeyVariables[i][3] = javaKeyTypes[i].indexOf(".") > -1 ? "null" : "-1";
				if(javaKeyFields.length-1 == i){
					parameterTypeAndVars += Utility.toSimpleClassName(javaKeyTypes[i]) + " " + javaKeyFields[i];
					parameterTypes += javaKeyTypes[i];
					parameterVars += javaKeyFields[i];
					voKeyRemainVariable[0][0] = Utility.toSimpleClassName(javaKeyTypes[i]);
					voKeyRemainVariable[0][1] = javaKeyFields[i];
					voKeyRemainVariable[0][2] = javaKeyFields[i].substring(0, 1).toUpperCase() + javaKeyFields[i].substring(1);
					voKeyRemainVariable[0][3] = javaKeyTypes[i].indexOf(".") > -1 ? "null" : "-1";
				}
			}
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPES, parameterTypes);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPE_AND_VARS, parameterTypeAndVars);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_VARS, parameterVars);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_NAMES, parameterNames);
			source = SourceBuilderUtil.replaceIterates(source, "voKeyNullCheckers", voKeyVariables);
			source = SourceBuilderUtil.replaceIterates(source, "voKeyEqualCheckers", parameterKeyVariables);
			source = SourceBuilderUtil.replaceIterates(source, "parameterKeyNullChecker", parameterKeyVariables);
			source = SourceBuilderUtil.replaceIterates(source, "voKeyRemainVars", voKeyRemainVariable);
			if(parameterKeyVariables.length < 1){
				source = SourceBuilderUtil.removeIterates(source, "voKeyEqualCheckers");
				source = SourceBuilderUtil.removeIterates(source, "parameterKeyNullChecker");
			}

		}else{
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPES);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPE_AND_VARS);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_VARS);
			source = SourceBuilderUtil.removeIterates(source, "voKeyNullCheckers");
			source = SourceBuilderUtil.removeIterates(source, "voKeyEqualCheckers");
			source = SourceBuilderUtil.removeIterates(source, "parameterKeyNullChecker");
			source = SourceBuilderUtil.removeIterates(source, "voKeyRemainVars");
		}
		return source;
	}

	Boolean isReservedExpression(String javaFields) {
		if (StringUtils.isEmpty(javaFields)) { return false; }

		if (
				javaFields.equals(FieldExpression.RESERVED_EXPR_CREATED_BY) || javaFields.equals(FieldExpression.RESERVED_EXPR_CREATED_DT) ||
						javaFields.equals(FieldExpression.RESERVED_EXPR_MODIFIED_BY) || javaFields.equals(FieldExpression.RESERVED_EXPR_MODIFIED_DT) ||
						javaFields.equals(FieldExpression.RESERVED_EXPR_IS_REMOVED) || javaFields.equals(FieldExpression.RESERVED_EXPR_IS_ENABLED)
		) {
			return true;
		} else {
			return false;
		}
	}

}
