package com.dsw.sgweb.generator.builder;

import com.dsw.core.util.builder.SourceBuilder;
import com.dsw.core.util.builder.SourceBuilderProperties;
import com.dsw.core.util.db.sql.builder.FieldExpression;
import com.dsw.core.util.db.sql.builder.SqlBuilder;
import com.dsw.core.util.db.sql.builder.SqlSource;
import com.dsw.core.util.db.sql.builder.oracle.OracleSqlBuilder;
import com.dsw.core.util.orm.mybatis.builder.MyBatisSqlMapBuilder;

import java.sql.SQLException;

/**
 * Description :
 * @author YSKim
 * @since 2013. 11. 6.
 */
public class SqlXmlBuilder extends MyBatisSqlMapBuilder {

	/**
	 * 테스트 실행
	 * @param args
	 */
	public static void main(String[] args) {
		SourceBuilder builder = new MyBatisSqlMapBuilder();
		try {

			SqlBuilder sqlBuilder = new OracleSqlBuilder("oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@192.168.115.17:1521:www", "EMEDDEV", "wylie0102");
			SqlSource sqlSource = null;

			try {
				FieldExpression selectFieldExpression = new FieldExpression();
				selectFieldExpression.addFieldSkip("REG_ID");
				selectFieldExpression.addFieldSkip("REG_DT");
				selectFieldExpression.addFieldSkip("MDFY_ID");
				selectFieldExpression.addFieldSkip("MDFY_DT");

				FieldExpression insertFieldExpression = new FieldExpression();
				insertFieldExpression.addFieldSkip("MDFY_ID");
				insertFieldExpression.addFieldSkip("MDFY_DT");
				insertFieldExpression.addFieldVariableSwitch("REG_ID", "#{writer}");
				insertFieldExpression.addFieldVariableSwitch("REG_DT", FieldExpression.RESERVED_EXPR_SYSTEM_DATE);

				FieldExpression updateFieldExpression = new FieldExpression();
				updateFieldExpression.addFieldSkip("REG_ID");
				updateFieldExpression.addFieldSkip("REG_DT");
				insertFieldExpression.addFieldVariableSwitch("MDFY_ID", "#{writer}");
				updateFieldExpression.addFieldVariableSwitch("MDFY_DT", FieldExpression.RESERVED_EXPR_SYSTEM_DATE);

				sqlBuilder.setVariablePrefix("#{");
				sqlBuilder.setVariableSuffix("}");
				sqlBuilder.setVariableDomainName("vo");
				sqlBuilder.setFieldNamesToUpperCase();
				sqlSource = sqlBuilder.build("WWWMAN.H_EMP_PERSON", "A", "", selectFieldExpression, insertFieldExpression, updateFieldExpression);

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

			SourceBuilderProperties<String, Object> properties = new SourceBuilderProperties<String, Object>();

			properties.set(PROP_SQL_SOURCE, sqlSource);

			properties.set(PROP_NAMESPACE, "Smp001");
			properties.set(PROP_DOMAIN_PACKAGE_NAME, "echon.sample.zipcode");
			properties.set(PROP_DOMAIN_CLASS_NAME, "FamilyVO");

			builder.build(properties);
			System.out.println(builder.getSourceString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
