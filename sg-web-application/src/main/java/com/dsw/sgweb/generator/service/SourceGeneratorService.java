package com.dsw.sgweb.generator.service;

import com.dsw.core.util.builder.SourceBuilder;
import com.dsw.core.util.builder.SourceBuilderProperties;
import com.dsw.core.util.db.sql.builder.FieldExpression;
import com.dsw.core.util.db.sql.builder.SqlBuilder;
import com.dsw.core.util.db.sql.builder.SqlSource;
import com.dsw.core.util.db.sql.builder.mysql.MySqlBuilder;
import com.dsw.core.util.db.sql.builder.oracle.OracleSqlBuilder;
import com.dsw.core.util.db.sql.builder.postgresql.PostgreSqlBuilder;
import com.dsw.core.util.db.sql.builder.sqlserver.SqlServerBuilder;
import com.dsw.sgweb.generator.builder.*;
import com.dsw.sgweb.generator.domain.SourceGenerator;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Description :
 * @author YSKim
 * @since 2013. 11. 6.
 */
@Service
public class SourceGeneratorService {

	@Autowired
	private Environment jdbc; //jdbc.properties 객체

	private SqlBuilder getSqlBuilder(String database, BasicDataSource dataSource) {

		SqlBuilder sqlBuilder;
		if ("oracle".equals(database)) {
			sqlBuilder = new OracleSqlBuilder(dataSource);
		} else if ("mssql".equals(database)) {
			sqlBuilder = new SqlServerBuilder(dataSource);
		} else if ("postgresql".equals(database)) {
			sqlBuilder = new PostgreSqlBuilder(dataSource);
		} else {
			sqlBuilder = new MySqlBuilder(dataSource);
		}

		return sqlBuilder;

	}

	public SqlSource getSqlSource(SourceGenerator vo) {

		// 직접 만든 데이터 소스
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(jdbc.getProperty("jdbc.driverClassName"));
		dataSource.setUrl(jdbc.getProperty("jdbc.url"));
		dataSource.setUsername(jdbc.getProperty("jdbc.username"));
		dataSource.setPassword(jdbc.getProperty("jdbc.password"));

		// sql builder 선정
		String database = jdbc.getProperty("jdbc.database");
		SqlBuilder sqlBuilder = getSqlBuilder(database, dataSource);

		SqlSource sqlSource = null;
		String tableName = vo.getTableName();
		String tablePrefix = vo.getTablePrefix();

		try{
			sqlBuilder.setVariablePrefix("#{");
			sqlBuilder.setVariableSuffix("}");
//			sqlBuilder.setVariableDomainName("vo");
			sqlBuilder.setKeyFieldNotUseVariableDomainName(true);
			sqlBuilder.setFieldNamesToLowerCase();  //테이블 필드를 소문자로 제작
			sqlBuilder.setUseAlternateKey(true);	//대체키 사용 (키 없는 테이블에 첫번째 필드를 키로 사용)

			FieldExpression selectFieldExpression = new FieldExpression();
			selectFieldExpression.addFieldSkip(FieldExpression.RESERVED_FIELD_CREATED_BY);
			selectFieldExpression.addFieldSkip(FieldExpression.RESERVED_FIELD_CREATED_DT);
			selectFieldExpression.addFieldSkip(FieldExpression.RESERVED_FIELD_MODIFIED_BY);
			selectFieldExpression.addFieldSkip(FieldExpression.RESERVED_FIELD_MODIFIED_DT);

			FieldExpression insertFieldExpression = new FieldExpression();
			insertFieldExpression.addFieldSkip(FieldExpression.RESERVED_FIELD_MODIFIED_BY);
			insertFieldExpression.addFieldSkip(FieldExpression.RESERVED_FIELD_MODIFIED_DT);
			insertFieldExpression.addFieldVariableSwitch(FieldExpression.RESERVED_EXPR_IS_ENABLED, "'Y'");
			insertFieldExpression.addFieldVariableSwitch(FieldExpression.RESERVED_EXPR_IS_REMOVED, "'N'");
			insertFieldExpression.addFieldVariableSwitch(FieldExpression.RESERVED_FIELD_MODIFIED_BY, "#{writer}");
			insertFieldExpression.addFieldVariableSwitch(FieldExpression.RESERVED_FIELD_MODIFIED_DT, FieldExpression.RESERVED_EXPR_SYSTEM_DATE);

			FieldExpression updateFieldExpression = new FieldExpression();
			updateFieldExpression.addFieldSkip(FieldExpression.RESERVED_FIELD_CREATED_BY);
			updateFieldExpression.addFieldSkip(FieldExpression.RESERVED_FIELD_CREATED_DT);
			updateFieldExpression.addFieldVariableSwitch(FieldExpression.RESERVED_FIELD_MODIFIED_BY, "#{writer}");
			updateFieldExpression.addFieldVariableSwitch(FieldExpression.RESERVED_FIELD_MODIFIED_DT, FieldExpression.RESERVED_EXPR_SYSTEM_DATE);

			sqlSource = sqlBuilder.build(tableName, "A", tablePrefix, selectFieldExpression, insertFieldExpression, updateFieldExpression);
		}catch(SQLException e){
			e.getMessage();
		}

		return sqlSource;
	}

	public List<String> getTableNames() throws SQLException {
		// 직접 만든 데이터 소스
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(jdbc.getProperty("jdbc.driver-class-name"));
		dataSource.setUrl(jdbc.getProperty("jdbc.url"));
		dataSource.setUsername(jdbc.getProperty("jdbc.username"));
		dataSource.setPassword(jdbc.getProperty("jdbc.password"));

		// sql builder 선정
		String database = jdbc.getProperty("jdbc.database");
		String schema = jdbc.getProperty("jdbc.schema");
		SqlBuilder sqlBuilder = getSqlBuilder(database, dataSource);

		return sqlBuilder.getTableNames();
	}

	public String getSqlXmlSourceString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create SQL Source";
		SourceBuilder builder = new SqlXmlBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(SqlXmlBuilder.PROP_AUTHOR, vo.getAuthor());
			builderProperties.set(SqlXmlBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(SqlXmlBuilder.PROP_NAMESPACE, vo.getClassRepository().replaceAll("Repository", ""));
			builderProperties.set(SqlXmlBuilder.PROP_TITLE, vo.getServiceKorean());
			builderProperties.set(SqlXmlBuilder.PROP_BUSINESS_NAME, vo.getServiceEnglish());
			builderProperties.set(SqlXmlBuilder.PROP_DOMAIN_PACKAGE_NAME, vo.getPackageDomain());
			builderProperties.set(SqlXmlBuilder.PROP_DOMAIN_CLASS_NAME, vo.getClassDomain());
			builderProperties.set(SqlXmlBuilder.PROP_BUILD_OPTION, RepositoryBuilder.BUILD_OPTION_BOTH);
			builderProperties.set(SqlXmlBuilder.PROP_REPOSITORY_PACKAGE_NAME, vo.getPackageRepository());
			builderProperties.set(SqlXmlBuilder.PROP_REPOSITORY_CLASS_NAME, vo.getClassRepository());
			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getDomainString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create Domain Source";
		SourceBuilder builder = new DomainBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(DomainBuilder.PROP_PROJECT_NAME, vo.getProjectName());
			builderProperties.set(DomainBuilder.PROP_AUTHOR, vo.getAuthor());
			builderProperties.set(DomainBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(DomainBuilder.PROP_DOMAIN_PACKAGE_NAME, vo.getPackageDomain());
			builderProperties.set(DomainBuilder.PROP_DOMAIN_CLASS_NAME, vo.getClassDomain());
			builderProperties.set(DomainBuilder.PROP_BUSINESS_ENGLISH_NAME, vo.getServiceEnglish());
			builderProperties.set(DomainBuilder.PROP_BUSINESS_KOREAN_NAME, vo.getServiceKorean());
			builderProperties.set(DomainBuilder.PROP_BASE_PACKAGE_NAME, vo.getBasePackage());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getRepositoryString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create Repository Source";
		SourceBuilder builder = new RepositoryBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(RepositoryBuilder.PROP_AUTHOR, vo.getAuthor());
			builderProperties.set(RepositoryBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(RepositoryBuilder.PROP_REPOSITORY_PACKAGE_NAME, vo.getPackageRepository());
			builderProperties.set(RepositoryBuilder.PROP_REPOSITORY_CLASS_NAME, vo.getClassRepository());
			builderProperties.set(RepositoryBuilder.PROP_DOMAIN_PACKAGE_NAME, vo.getPackageDomain());
			builderProperties.set(RepositoryBuilder.PROP_DOMAIN_CLASS_NAME, vo.getClassDomain());
			builderProperties.set(RepositoryBuilder.PROP_BUSINESS_ENGLISH_NAME, vo.getServiceEnglish());
			builderProperties.set(RepositoryBuilder.PROP_BUSINESS_KOREAN_NAME, vo.getServiceKorean());
			builderProperties.set(RepositoryBuilder.PROP_BUILD_OPTION, RepositoryBuilder.BUILD_OPTION_BOTH);
			builderProperties.set(RepositoryBuilder.PROP_BASE_PACKAGE_NAME, vo.getBasePackage());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getBusinessServiceImplString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create BusinessServiceImpl Source";
		SourceBuilder builder = new BizServiceImplBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(BizServiceImplBuilder.PROP_PROJECT_NAME, vo.getProjectName());
			builderProperties.set(BizServiceImplBuilder.PROP_AUTHOR, vo.getAuthor());
			builderProperties.set(BizServiceImplBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(BizServiceImplBuilder.PROP_BUSINESS_PACKAGE_NAME, vo.getPackageBizService());
			builderProperties.set(BizServiceImplBuilder.PROP_BUSINESS_CLASS_NAME, vo.getClassBizService());
			builderProperties.set(BizServiceImplBuilder.PROP_REPOSITORY_PACKAGE_NAME, vo.getPackageRepository());
			builderProperties.set(BizServiceImplBuilder.PROP_REPOSITORY_CLASS_NAME, vo.getClassRepository());
			builderProperties.set(BizServiceImplBuilder.PROP_DOMAIN_PACKAGE_NAME, vo.getPackageDomain());
			builderProperties.set(BizServiceImplBuilder.PROP_DOMAIN_CLASS_NAME, vo.getClassDomain());
			builderProperties.set(BizServiceImplBuilder.PROP_BUSINESS_ENGLISH_NAME, vo.getServiceEnglish());
			builderProperties.set(BizServiceImplBuilder.PROP_BUSINESS_KOREAN_NAME, vo.getServiceKorean());
			builderProperties.set(BizServiceImplBuilder.PROP_BASE_PACKAGE_NAME, vo.getBasePackage());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getControllerString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create Controller Source";
		SourceBuilder builder = new ControllerBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(ControllerBuilder.PROP_PROJECT_NAME, vo.getProjectName());
			builderProperties.set(ControllerBuilder.PROP_AUTHOR, vo.getAuthor());
			builderProperties.set(ControllerBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(ControllerBuilder.PROP_MODULE_PACKAGE_NAME, vo.getModulePackage());
			builderProperties.set(ControllerBuilder.PROP_BUSINESS_PACKAGE_NAME, vo.getPackageBizService());
			builderProperties.set(ControllerBuilder.PROP_BUSINESS_CLASS_NAME, vo.getClassBizService());
			builderProperties.set(ControllerBuilder.PROP_SVC_PACKAGE_NAME, vo.getPackageController());
			builderProperties.set(ControllerBuilder.PROP_SVC_CLASS_NAME, vo.getClassController());
			builderProperties.set(ControllerBuilder.PROP_DOMAIN_PACKAGE_NAME, vo.getPackageDomain());
			builderProperties.set(ControllerBuilder.PROP_DOMAIN_CLASS_NAME, vo.getClassDomain());
			builderProperties.set(ControllerBuilder.PROP_BUSINESS_ENGLISH_NAME, vo.getServiceEnglish());
			builderProperties.set(ControllerBuilder.PROP_BUSINESS_KOREAN_NAME, vo.getServiceKorean());
			builderProperties.set(ControllerBuilder.PROP_BASE_PACKAGE_NAME, vo.getBasePackage());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getResourceString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create Resource Source";
		SourceBuilder builder = new ResourceBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(ResourceBuilder.PROP_PROJECT_NAME, vo.getProjectName());
			builderProperties.set(ResourceBuilder.PROP_AUTHOR, vo.getAuthor());
			builderProperties.set(ResourceBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(ResourceBuilder.PROP_MODULE_PACKAGE_NAME, vo.getModulePackage());
			builderProperties.set(ResourceBuilder.PROP_BUSINESS_PACKAGE_NAME, vo.getPackageBizService());
			builderProperties.set(ResourceBuilder.PROP_BUSINESS_CLASS_NAME, vo.getClassBizService());
			builderProperties.set(ResourceBuilder.PROP_SVC_PACKAGE_NAME, vo.getPackageController());
			builderProperties.set(ResourceBuilder.PROP_SVC_CLASS_NAME, vo.getClassController());
			builderProperties.set(ResourceBuilder.PROP_DOMAIN_PACKAGE_NAME, vo.getPackageDomain());
			builderProperties.set(ResourceBuilder.PROP_DOMAIN_CLASS_NAME, vo.getClassDomain());
			builderProperties.set(ResourceBuilder.PROP_BUSINESS_ENGLISH_NAME, vo.getServiceEnglish());
			builderProperties.set(ResourceBuilder.PROP_BUSINESS_KOREAN_NAME, vo.getServiceKorean());
			builderProperties.set(ResourceBuilder.PROP_BASE_PACKAGE_NAME, vo.getBasePackage());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getControllerTestString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create Controller Test Source";
		SourceBuilder builder = new ControllerTestBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(ControllerBuilder.PROP_PROJECT_NAME, vo.getProjectName());
			builderProperties.set(ControllerTestBuilder.PROP_AUTHOR, vo.getAuthor());
			builderProperties.set(ControllerTestBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(ControllerTestBuilder.PROP_MODULE_PACKAGE_NAME, vo.getModulePackage());
			builderProperties.set(ControllerTestBuilder.PROP_BUSINESS_PACKAGE_NAME, vo.getPackageBizService());
			builderProperties.set(ControllerTestBuilder.PROP_BUSINESS_CLASS_NAME, vo.getClassBizService());
			builderProperties.set(ControllerTestBuilder.PROP_SVC_PACKAGE_NAME, vo.getPackageController());
			builderProperties.set(ControllerTestBuilder.PROP_SVC_CLASS_NAME, vo.getClassController());
			builderProperties.set(ControllerTestBuilder.PROP_DOMAIN_PACKAGE_NAME, vo.getPackageDomain());
			builderProperties.set(ControllerTestBuilder.PROP_DOMAIN_CLASS_NAME, vo.getClassDomain());
			builderProperties.set(ControllerTestBuilder.PROP_BUSINESS_ENGLISH_NAME, vo.getServiceEnglish());
			builderProperties.set(ControllerTestBuilder.PROP_BUSINESS_KOREAN_NAME, vo.getServiceKorean());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getJspListString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create Jsp List Source";
		SourceBuilder builder = new JSPListBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(JSPListBuilder.PROP_PROJECT_NAME, vo.getProjectName());
			builderProperties.set(JSPListBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(JSPListBuilder.PROP_MODULE_PACKAGE_NAME, vo.getModulePackage());
			builderProperties.set(JSPListBuilder.PROP_SVC_CLASS_NAME, vo.getClassController());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getJspDetailString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create Jsp Detail Source";
		SourceBuilder builder = new JSPDetailBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(JSPDetailBuilder.PROP_PROJECT_NAME, vo.getProjectName());
			builderProperties.set(JSPDetailBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(JSPDetailBuilder.PROP_MODULE_PACKAGE_NAME, vo.getModulePackage());
			builderProperties.set(JSPDetailBuilder.PROP_SVC_CLASS_NAME, vo.getClassController());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getJspRegistString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create Jsp Regist Source";
		SourceBuilder builder = new JSPRegistBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(JSPRegistBuilder.PROP_PROJECT_NAME, vo.getProjectName());
			builderProperties.set(JSPRegistBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(JSPRegistBuilder.PROP_MODULE_PACKAGE_NAME, vo.getModulePackage());
			builderProperties.set(JSPRegistBuilder.PROP_SVC_CLASS_NAME, vo.getClassController());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getJspUpdateString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create Jsp Update Source";
		SourceBuilder builder = new JSPUpdateBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(JSPUpdateBuilder.PROP_PROJECT_NAME, vo.getProjectName());
			builderProperties.set(JSPUpdateBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(JSPUpdateBuilder.PROP_MODULE_PACKAGE_NAME, vo.getModulePackage());
			builderProperties.set(JSPUpdateBuilder.PROP_SVC_CLASS_NAME, vo.getClassController());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getHtmlListString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create Html List Source";
		SourceBuilder builder = new HtmlListBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(HtmlListBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(HtmlListBuilder.PROP_MODULE_PACKAGE_NAME, vo.getModulePackage());
			builderProperties.set(HtmlListBuilder.PROP_SVC_CLASS_NAME, vo.getClassController());

			//홈페이지어드민 , crm, Homepage 에 따라 템플릿이 바뀌므로 넣어준다
			builderProperties.set(HtmlListBuilder.PROP_BASE_PACKAGE_NAME, vo.getBasePackage());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getJsAppString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create JS App Source";
		SourceBuilder builder = new JsAppBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(JsAppBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(JsAppBuilder.PROP_MODULE_PACKAGE_NAME, vo.getModulePackage());
			builderProperties.set(JsAppBuilder.PROP_SVC_CLASS_NAME, vo.getClassController());
			builderProperties.set(JsAppBuilder.PROP_MODULE_KOREAN, vo.getServiceKorean());

			//홈페이지어드민 , crm, Homepage 에 따라 App템플릿이 바뀌므로 넣어준다
			builderProperties.set(JsAppBuilder.PROP_BASE_PACKAGE_NAME, vo.getBasePackage());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getJsListString(SourceGenerator vo, SqlSource sqlSource) {
		String rtnStr = "Can't create JS List Source";
		SourceBuilder builder = new JsListBuilder();
		SourceBuilderProperties<String, Object> builderProperties = new SourceBuilderProperties<String, Object>();
		try {
			builderProperties.set(JsListBuilder.PROP_SQL_SOURCE, sqlSource);
			builderProperties.set(JsListBuilder.PROP_MODULE_PACKAGE_NAME, vo.getModulePackage());
			builderProperties.set(JsListBuilder.PROP_SVC_CLASS_NAME, vo.getClassController());

			//홈페이지어드민 , crm, Homepage 에 따라 템플릿이 바뀌므로 넣어준다
			builderProperties.set(JsListBuilder.PROP_BASE_PACKAGE_NAME, vo.getBasePackage());

			builder.build(builderProperties);
			rtnStr = builder.getSourceString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}
}
