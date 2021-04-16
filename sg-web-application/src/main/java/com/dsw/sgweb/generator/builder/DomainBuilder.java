package com.dsw.sgweb.generator.builder;

import com.dsw.core.util.builder.SourceBuilder;
import com.dsw.core.util.builder.SourceBuilderProperties;
import com.dsw.core.util.builder.SourceBuilderUtil;
import com.dsw.core.util.builder.template.JavaSourceTemplate;
import com.dsw.core.util.db.sql.builder.FieldExpression;
import com.dsw.core.util.db.sql.builder.SqlSource;
import org.apache.commons.lang3.StringUtils;

/**
 * Description :
 * @author YSKim
 * @since 2013. 11. 7.
 */
public class DomainBuilder implements JavaSourceBuilder, SourceBuilder {

	/**
	 * Template 파일 (tpl)
	 */
	public static final String TEMPLATE = "/static/tpl/DomainTemplate.template";

	/**
	 * Template Loading Encoding
	 */
	private final String ENCODING = "UTF-8";

	public static final String PROP_SQL_SOURCE = "PROP_SQL_SOURCE";
	public static final String PROP_PROJECT_NAME = "PROP_PROJECT_NAME";
	public static final String PROP_DOMAIN_PACKAGE_NAME = "PROP_DOMAIN_PACKAGE_NAME";
	public static final String PROP_DOMAIN_CLASS_NAME = "PROP_DOMAIN_CLASS_NAME";
	public static final String PROP_DOMAIN_SUPER_CLASS_NAME = "PROP_DOMAIN_SUPER_CLASS_NAME";
	public static final String PROP_DOMAIN_SUPER_CLASS_IMPORT = "PROP_DOMAIN_SUPER_CLASS_IMPORT";
	public static final String PROP_BUSINESS_ENGLISH_NAME = "PROP_BUSINESS_ENGLISH_NAME";
	public static final String PROP_BUSINESS_KOREAN_NAME = "PROP_BUSINESS_KOREAN_NAME";
	public static final String PROP_AUTHOR = "PROP_AUTHOR";
	public static final String PROP_BASE_PACKAGE_NAME = "PROP_BASE_PACKAGE_NAME";

	private String source;

	/* (None Javadoc)
	 * @see com.dsw.core.util.builder.SourceBuilder#build(com.dsw.core.util.builder.SourceBuilderProperties)
	 */
	public void build(SourceBuilderProperties<String, Object> properties) throws Exception {
		String domainPackage = properties.getString(PROP_DOMAIN_PACKAGE_NAME);
		String domainClass = properties.getString(PROP_DOMAIN_CLASS_NAME);
		String voSuperClass = properties.getString(PROP_DOMAIN_SUPER_CLASS_NAME);
		String voSuperClassImport = properties.getString(PROP_DOMAIN_SUPER_CLASS_IMPORT);
		String author = properties.getString(PROP_AUTHOR);
		String businessEnglishName = properties.getString(PROP_BUSINESS_ENGLISH_NAME);
		String businessKoreanName = properties.getString(PROP_BUSINESS_KOREAN_NAME);
		String basePackage = properties.getString(PROP_BASE_PACKAGE_NAME);

		SqlSource sqlSource = (SqlSource)properties.get(PROP_SQL_SOURCE);

		String[] fields = null;
		String[] javaTypes = null;
		String[] javaFields = null;
		String[] comments = null;
		int[] scale = null;
		int[] precisions = null;
		boolean[] nullables = null;
		String[] importTypes = new String[0];
		if ( sqlSource != null ) {
			fields = sqlSource.getFields();
			javaTypes = sqlSource.getJavaTypes();
			javaFields = sqlSource.getJavaFields();
			comments = sqlSource.getComments();
			scale = sqlSource.getScales();
			precisions = sqlSource.getPrecisions();
			nullables = sqlSource.getNullables();

			importTypes = new String[javaTypes.length];
			for (int i = 0; i < importTypes.length; i++) {

				if ( javaTypes[i].startsWith("java.lang") || !javaTypes[i].startsWith("java.") ) {
					continue;
				}

				if( !com.dsw.core.util.Utility.isInArray(importTypes, javaTypes[i]) ) {
					if (
							javaFields[i].equals(FieldExpression.RESERVED_EXPR_IS_REMOVED)
							|| javaFields[i].equals(FieldExpression.RESERVED_EXPR_IS_ENABLED)
							|| javaFields[i].equals(FieldExpression.RESERVED_EXPR_CREATED_BY)
							|| javaFields[i].equals(FieldExpression.RESERVED_EXPR_CREATED_DT)
							|| javaFields[i].equals(FieldExpression.RESERVED_EXPR_MODIFIED_BY)
							|| javaFields[i].equals(FieldExpression.RESERVED_EXPR_MODIFIED_DT)
						)
					{
						continue;
					}
					importTypes[i] = javaTypes[i];
				}
			}

		}

		if(voSuperClassImport != null){
			String[] _importTypes = new String[importTypes.length + 1];
			for(int i = 0; i < _importTypes.length; i++){
				if(i == _importTypes.length - 1){
					_importTypes[i] = voSuperClassImport;
				}else{
					_importTypes[i] = importTypes[i];
				}
			}
			importTypes = _importTypes;
		}

		String[] javaKeyFields = sqlSource.getJavaKeyFields();
		String[] javaKeyTypes = sqlSource.getJavaKeyTypes();

		JavaSourceTemplate template = new JavaSourceTemplate(TEMPLATE);
		template.loadTemplate(ENCODING);
		String createDate = template.creationDate();

		final String importDefine = template.getImportDefine("Type" + (sqlSource != null ? "1" : "2"));
		final String classDefine = template.getClassDefine("Type1");
		final String fieldDefine = template.getFieldDefine("Type1");

		String importArea = createImportAreaSource(importDefine, importTypes, basePackage);

		String fieldArea = createFieldAreaSource(fieldDefine, javaTypes, javaFields, precisions, scale, nullables, comments, fields);

		String classArea = createClassAreaSource(classDefine, createDate, businessEnglishName, businessKoreanName, domainPackage, domainClass, voSuperClass, author, javaKeyFields, javaKeyTypes);
		classArea = SourceBuilderUtil.replaceVariables(classArea, DOMAIN_ALIAS, Utility.toFieldName(domainClass));
		classArea = SourceBuilderUtil.replaceVariables(classArea, IMPORTS, importArea);
		classArea = SourceBuilderUtil.replaceVariables(classArea, FIELDS, fieldArea);
		this.source = classArea;
	}


	private String createImportAreaSource(final String importDefine, String[] importTypes, String basePackage){
		if(importTypes == null)
			return "";
		String source = importDefine;
		StringBuilder importString = new StringBuilder();
		for(int i = 0; i < importTypes.length; i++){
			if(importTypes[i] == null)
				continue;
			importString.append("import ").append(importTypes[i]).append(";").append(CRLF);
		}
		source = SourceBuilderUtil.replaceVariables(source, IMPORTS, importString.toString());
		source = SourceBuilderUtil.replaceVariables(source, BASE_PACKAGE, basePackage);
		return source;
	}

	public String createClassAreaSource(final String classDefine, String createDate, String businessEnglishName, String businessKoreanName, String packageName, String className, String superClassName, String author, String[] javaKeyFields, String[] javaKeyTypes) {
		String source = classDefine;
		source = SourceBuilderUtil.replaceVariables(source, AUTHOR, author);
		source = SourceBuilderUtil.replaceVariables(source, CREATION_DATE, createDate);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_ENGLISH_NAME, businessEnglishName);
		source = SourceBuilderUtil.replaceVariables(source, BUSINESS_KOREAN_NAME, businessKoreanName);
		source = SourceBuilderUtil.replaceVariables(source, PACKAGE, packageName);
		source = SourceBuilderUtil.replaceVariables(source, CLASS_NAME, className);
		source = SourceBuilderUtil.replaceVariables(source, SUPER_CLASS_NAME, superClassName);
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
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_VARS, parameterVars);
			source = SourceBuilderUtil.replaceVariables(source, PARAMETER_TYPE_AND_VARS, parameterTypeAndVars);
		}else{
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_VARS);
			source = SourceBuilderUtil.removeVariables(source, PARAMETER_TYPE_AND_VARS);
		}
		return source;
	}

	public String createFieldAreaSource(final String fieldDefine, String[] types, String[] variables, int[] precisions, int[] scales, boolean[] nullables, String[] comments, String[] fields) {
		if(types == null || variables == null)
			return "";

		String source = fieldDefine;
		String[][] params = new String[types.length][5];
		for ( int i = 0; i < params.length; i++ ) {
			if ( isReservedExpression(variables[i]) ) {
				params[i] = null;
			} else {
				params[i][0] = Utility.toSimpleClassName(types[i]);
				params[i][1] = variables[i];
				String comment = comments[i] == null ? "" : comments[i] + " 은(는)";
				params[i][2] = nullables[i] ? "" : "String".equals(params[i][0]) ? ("\n\t@NotEmpty(message=\"" + comment + " 필수값입니다.\")") : ("\n\t@NotNull(message=\"" + comment + " 필수값입니다.\")");
				params[i][3] = comments[i] == null ? "" : "\n  /** \n   * " + comments[i] + "\n   */";
			}
		}
		source = SourceBuilderUtil.replaceIterates(source, "fields", params);
		return source;
	}

	public String createMethodAreaSource(final String methodDefine, String[] types, String[] variables, String[] comments){
		if(types == null || variables == null)
			return "";

		String source = methodDefine;
		String[][] params = new String[types.length][5];
		for ( int i = 0; i < params.length; i++ ) {
			if ( isReservedExpression(variables[i]) ) {
				params[i] = null;
			} else {
				params[i][0] = Utility.toSimpleClassName(types[i]);
				params[i][1] = variables[i];
				params[i][2] = variables[i].substring(0, 1).toUpperCase() + variables[i].substring(1);
				params[i][3] = comments[i] == null ? "" : "\n\t * " + comments[i] + " setter";
				params[i][4] = comments[i] == null ? "" : "\n\t * " + comments[i] + " getter";
			}
		}
		source = SourceBuilderUtil.replaceIterates(source, "methods", params);
		return source;
	}

	public String createToStringMethodAreaSource(final String methodDefine, String domainClass, String[] variables){
		if(variables == null)
			return "";

		String source = methodDefine;
		source = SourceBuilderUtil.replaceVariables(source, DOMAIN_CLASS, domainClass);
		String[][] params = new String[variables.length][1];
		for ( int i = 0; i < params.length; i++ ) {
			if ( isReservedExpression(variables[i]) ) {
				params[i] = null;
			}else{
				params[i][0] = variables[i];
			}
		}
		source = SourceBuilderUtil.replaceIterates(source, "builder", params);
		return source;
	}

	public String getSourceString() {
		return this.source;
	}

	Boolean isReservedExpression(String javaFields) {
		if (StringUtils.isEmpty(javaFields)) { return false; }

		if (
				javaFields.equals(FieldExpression.RESERVED_EXPR_IS_REMOVED)
				|| javaFields.equals(FieldExpression.RESERVED_EXPR_IS_ENABLED)
				|| javaFields.equals(FieldExpression.RESERVED_EXPR_CREATED_DT)
				|| javaFields.equals(FieldExpression.RESERVED_EXPR_CREATED_BY)
				|| javaFields.equals(FieldExpression.RESERVED_EXPR_MODIFIED_DT)
				|| javaFields.equals(FieldExpression.RESERVED_EXPR_MODIFIED_BY)
			) {
			return true;
		} else {
			return false;
		}
	}

}
