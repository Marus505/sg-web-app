package com.dsw.core.util.db.sql.builder;

import java.sql.SQLException;
import java.util.List;


/**
 * Class Name : SqlBuilder.java
 * Description : SqlBuilder class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public interface SqlBuilder {
	int SELECT = 1;
	int INSERT = 2;
	int UPDATE = 4;
	int DELETE = 8;

	/**
	 * Table명으로 SqlSource 생성
	 * @param tableName
	 * @param alias
	 * @param tablePrefix
	 * @param selectQueryFieldExpression
	 * @param insertQueryFieldExpression
	 * @param updateQueryFieldExpression
	 * @return SqlSource
	 * @throws SQLException
	 */
	SqlSource build(String tableName, String alias, String tablePrefix, FieldExpression selectQueryFieldExpression, FieldExpression insertQueryFieldExpression, FieldExpression updateQueryFieldExpression) throws SQLException;

	/**
	 * Query로 SqlSource 생성
	 * @param query
	 * @return SqlSource
	 * @throws SQLException
	 */
	SqlSource buildForQuery(String query) throws SQLException;

	/**
	 *
	 * @return
	 * @throws SQLException
	 */
	List<String> getTableNames() throws SQLException;

	/**
	 * 기본값 "${"
	 * @param variablePrefix
	 */
	void setVariablePrefix(String variablePrefix);

	/**
	 * 기본값 "}"
	 * @param variableSuffix
	 */
	void setVariableSuffix(String variableSuffix);

	/**
	 * VO를 사용할 경우 이 값을 지정한다. 지정 안할경우 사용안함.
	 * ex) FamilyVO 의 seq, name, age 가 있을 경우 이 값을 vo 로 지정하면
	 * 		insert, update, delete문 SQL에 값 매핑이 vo.seq, vo.name, vo.age로 지정된다.
	 * @param variableDomainName
	 */
	void setVariableDomainName(String variableDomainName);

	/**
	 * VO를 사용할 경우 VariableDomainName을 지정한하여 사용하는데, true로 설정하면 Key필드는 제외한다.
	 * @param isKeyFieldNotUseVariableDomainName
	 */
	void setKeyFieldNotUseVariableDomainName(boolean isKeyFieldNotUseVariableDomainName);

	/**
	 * Key가 없는 테이블의 경우 생성된 쿼리의 오작동으로 인한 데이터분실을 막기 위해 키가 아닌 필드중 첫번째 필드를 조건에 추가한다.
	 * @param useAlternateKey
	 */
	void setUseAlternateKey(boolean useAlternateKey);

	/**
	 * 모든 필드를 Upper Case로 생성함
	 */
	void setFieldNamesToUpperCase();

	/**
	 * 모든 필드를 Lower Case로 생성함
	 */
	void setFieldNamesToLowerCase();

	/**
	 * 필드를 Upper 또는 Lower Case로 생성하기로 설정한 것을 해제
	 */
	void setCancelFieldNamesCase();

	/**
	 * 숫자형 필드인지 리턴
	 * @param fieldName
	 * @return boolean
	 */
	boolean isNumericField(String fieldName);

	/**
	 * 키 필드인지 리턴
	 * @param fieldName
	 * @return boolean
	 */
	boolean isKeyField(String fieldName);
}
