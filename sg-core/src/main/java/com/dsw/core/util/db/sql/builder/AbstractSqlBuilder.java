package com.dsw.core.util.db.sql.builder;

import org.apache.commons.lang3.StringUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dsw.core.util.db.sql.builder.SqlBuilderUtil.toJavaFieldName;

/**
 * Class Name : AbstractSqlBuilder.java
 * Description : AbstractSqlBuilder class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public abstract class AbstractSqlBuilder  implements SqlBuilder {
	protected final String SYSTEM_DATE_RESERVED_WORD;

	protected final DataSource dataSource;

	private final int connectionType;
	private final String dsName;

	private final String url;
	private final String user;
	private final String password;
	private final String driverClass;

	protected boolean isUpperCase = false;
	protected boolean isLowerCase = false;

	protected String variablePrefix = "${";
	protected String variableSuffix = "}";
	protected String variableDomainName;
	protected boolean isKeyFieldNotUseVariableDomainName = true;

	protected String[] sqls = new String[7];

	protected String[] keyFields = null;
	protected int[] keyTypes = null;
	protected String[] fields = null;
	protected int[] types = null;
	protected int[] scales = null;
	protected int[] precisions = null;
	protected boolean[] nullables = null;
	protected String[] comments = null;

	protected String[] javaKeyFields = null;
	protected String[] javaTypes = null;
	protected String[] javaFields = null;
	protected String[] javaKeyTypes = null;

	protected boolean useAlternateKey = false;

	/**
	 * @param dsName
	 * @param systemDateReservedWord
	 */
	public AbstractSqlBuilder(String dsName, String systemDateReservedWord) {
		this.connectionType = 1;
		this.dsName = dsName;
		this.url = null;
		this.user = null;
		this.password = null;
		this.driverClass = null;
		this.dataSource = null;
		this.SYSTEM_DATE_RESERVED_WORD = systemDateReservedWord;
	}

	/**
	 * @param url
	 * @param user
	 * @param password
	 */
	public AbstractSqlBuilder(String driverClass, String url, String user, String password, String systemDateReservedWord){
		this.connectionType = 2;
		this.dsName = null;
		this.url = url;
		this.user = user;
		this.password = password;
		this.driverClass = driverClass;
		this.dataSource = null;
		this.SYSTEM_DATE_RESERVED_WORD = systemDateReservedWord;
	}

	/**
	 * @param dataSource
	 * @param systemDateReservedWord
	 */
	public AbstractSqlBuilder(DataSource dataSource, String systemDateReservedWord) {
		this.connectionType = 3;
		this.dsName = null;
		this.url = null;
		this.user = null;
		this.password = null;
		this.driverClass = null;
		this.dataSource = dataSource;
		this.SYSTEM_DATE_RESERVED_WORD = systemDateReservedWord;
	}

	/**
	 * @return Connection
	 * @throws SQLException
	 */
	protected Connection getConnection() throws SQLException{
		Connection connection = null;
		if(this.connectionType == 1){
			connection = getConnection(dsName);
		} else if(this.connectionType == 2) {
			connection = getConnection(driverClass, url, user, password);
		} else {
			connection = this.dataSource.getConnection();
		}

		return connection;
	}



	/**
	 * 사용하려면 로직 검토해볼 것!
	 * @param dsName
	 * @return Conncetion
	 * @throws SQLException
	 */
	private Connection getConnection(String dsName) throws SQLException{
		try {
			InitialContext ctx = new InitialContext();
			return ((DatabaseMetaData) ctx.lookup(dsName)).getConnection();
		} catch (NamingException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @param driverClass
	 * @param url
	 * @param user
	 * @param password
	 * @return Connection
	 * @throws SQLException
	 */
	private Connection getConnection(String driverClass, String url, String user, String password) throws SQLException{
		try {
			Class.forName(driverClass);
			return DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new SQLException("연결을 얻을 수 없습니다. driverClass=" + driverClass + " : " + e.getMessage());
		} catch (SQLException e) {
			throw new SQLException("연결을 얻을 수 없습니다. url=" + url + ", user="+user+" : " + e.getMessage());
		}
	}


	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.SqlBuilder#build(java.lang.String, java.lang.String, com.dsw.core.util.db.sql.builder.FieldExpression, com.dsw.core.util.db.sql.builder.FieldExpression, com.dsw.core.util.db.sql.builder.FieldExpression)
	 */
	public abstract SqlSource build(String tableName, String alias, String tablePrefix, FieldExpression selectQueryFieldExpression, FieldExpression insertQueryFieldExpression, FieldExpression updateQueryFieldExpression) throws SQLException;




	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.SqlBuilder#buildForQuery(java.lang.String)
	 */
	public SqlSource buildForQuery(String query) throws SQLException{
		SqlSource sqlSource = null;

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		ResultSetMetaData resultSetMetaData = null;
		try{
			connection = getConnection();

			List<String> pkFields = new ArrayList<String>();

			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			resultSetMetaData = resultSet.getMetaData();
			int count = resultSetMetaData.getColumnCount();
			fields = new String[count];
			types = new int[count];
			precisions = new int[count];
			scales = new int[count];
			nullables = new boolean[count];
			javaFields = new String[count];
			javaTypes = new String[count];
			for(int i = 0; i < count; i++){
				fields[i] = resultSetMetaData.getColumnName(i+1);
				types[i] = resultSetMetaData.getColumnType(i+1);
				precisions[i] = resultSetMetaData.getPrecision(i+1);
				scales[i] = resultSetMetaData.getScale(i+1);
				nullables[i] = resultSetMetaData.isNullable(i+1) == 1;
				javaFields[i] = SqlBuilderUtil.toJavaFieldName(fields[i]);
				javaTypes[i] = SqlBuilderUtil.toJavaFieldType(types[i], precisions[i], scales[i]);
			}

			//Keys
			keyFields = new String[pkFields.size()];
			keyTypes = new int[pkFields.size()];
			javaKeyFields = new String[pkFields.size()];
			javaKeyTypes = new String[pkFields.size()];
			for(int i = 0; i < pkFields.size(); i++){
				String _field = (String)pkFields.get(i);
				keyFields[Integer.parseInt(_field.substring(0, 1))-1] = _field.substring(2);
				javaKeyFields[Integer.parseInt(_field.substring(0, 1))-1] = SqlBuilderUtil.toJavaFieldName(_field.substring(2));

				for (int j = 0; j < fields.length; j++) {
					if(fields[j].equals(_field.substring(2))){
						keyTypes[Integer.parseInt(_field.substring(0, 1))-1] = types[j];
						javaKeyTypes[Integer.parseInt(_field.substring(0, 1))-1] = SqlBuilderUtil.toJavaFieldType(types[j], precisions[j], scales[j]);
					}
				}
			}

			sqls[0] = query;
			sqls[1] = null;
			sqls[2] = null;
			sqls[3] = null;

		}finally{
			try{if(resultSet != null) resultSet.close();}catch(Exception e){e.printStackTrace();}
			try{if(statement != null) statement.close();}catch(Exception e){e.printStackTrace();}
			try{if(connection != null) connection.close();}catch(Exception e){e.printStackTrace();}
		}

		sqlSource = new SqlSource(null);
		sqlSource.setAlias(null);
		sqlSource.setKeys(keyFields);
		sqlSource.setKeyTypes(keyTypes);
		sqlSource.setSqls(sqls);
		sqlSource.setFields(fields);
		sqlSource.setFieldTypes(types);
		sqlSource.setScales(scales);
		sqlSource.setPrecisions(precisions);
		sqlSource.setNullables(nullables);
		sqlSource.setJavaFields(javaFields);
		sqlSource.setJavaKeyFields(javaKeyFields);
		sqlSource.setJavaTypes(javaTypes);
		sqlSource.setJavaKeyTypes(javaKeyTypes);

		return sqlSource;

	}

	/**
	 * @param tableName
	 * @param databaseMetaData
	 * @return String
	 */
	protected abstract String getDummySelectQuery(String tableName, DatabaseMetaData databaseMetaData);

	/**
	 * 코멘트 가져오기 위한 쿼리. DB마다 다를 것이다.
	 *
	 * @param tableName
	 * @return String
	 */
	protected abstract String getCommentsSelectQuery(String tableName);

	/**
	 * 캐릭터셋을 가져오기 위한 쿼리.
	 *
	 * @return String
	 */
	protected abstract String getNlsLangSelectQuery();

	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.SqlBuilder#isNumericField(java.lang.String)
	 */
	public boolean isNumericField(String fieldName){
		if(fields == null)
			throw new RuntimeException("Sql Not Builded.");
		for(int i = 0; i < fields.length; i++){
			if(fields[i].equals(fieldName)){
				switch (types[i]) {
					case Types.NUMERIC:
						return true;
					case Types.INTEGER:
						return true;
					case Types.BIGINT:
						return true;
					case Types.SMALLINT:
						return true;
					case Types.TINYINT:
						return true;
					default:
						return false;
				}
			}
		}
		return false;
	}

	/**
	 *
	 * @return
	 * @throws SQLException
	 */
	protected List<String> getTableNames(String query)  throws SQLException {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		List<String> tableNames = new ArrayList<String>();

		try {
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);

			while(resultSet.next()) {
				tableNames.add(resultSet.getString(1));
			}

		} finally {
			try {
				if (resultSet != null) resultSet.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (statement != null) statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return tableNames;
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.SqlBuilder#isKeyField(java.lang.String)
	 */
	public boolean isKeyField(String fieldName){
		if(keyFields == null)
			throw new RuntimeException("Sql Not Builded.");
		for (int i = 0; i < keyFields.length; i++) {
			if(keyFields[i].equals(fieldName))
				return true;
		}
		return false;
	}

	/**
	 * SQL Server comment 가져오기 대응
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected HashMap<String, String> getResultMap(ResultSet rs) throws SQLException {
		return getResultMap(rs, 2, "");
	}

	/**
	 * MySQL comment 가져오기 대응
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected HashMap<String, String> getResultMap(ResultSet rs, int offset) throws SQLException {
		return getResultMap(rs, offset, "");
	}

	/**
	 * Oracle comment 가져오기 대응
	 * @param rs
	 * @param nlsLang
	 * @return
	 * @throws SQLException
	 */
	protected HashMap<String, String> getResultMap(ResultSet rs, String nlsLang) throws SQLException {
		return getResultMap(rs, 2, nlsLang);
	}

	protected HashMap<String, String> getResultMap(ResultSet rs, int offset, String nlsLang) throws SQLException {
		HashMap<String, String> map = new HashMap<String, String>();

		while(rs.next()) {
			map.put(rs.getString(1), getNullableResult(rs, offset, nlsLang));
		}
			return map;
	}


	/**
	 * @param tableName
	 * @param alias
	 * @param fieldExpression
	 * @return String
	 */
	protected String makeSelect(String schemeName, String tableName, String alias, FieldExpression fieldExpression){
		String fieldAlias = alias == null ? "" : (alias + ".");
		String tableAlias = alias == null ? "" : (" " + alias);
		if(fieldExpression == null)
			fieldExpression = new FieldExpression();

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");

		sql = this.appendSelectListField(sql, fieldExpression, fieldAlias, "\r\n");

		sql.append("\r\nFROM    ");
		sql.append(schemeName + "." + tableName);
		sql.append(tableAlias);
		if(keyFields.length > 0){
			sql.append("\r\n         WHERE    ");
			for (int i = 0; i < keyFields.length; i++) {
				String keyName = keyFields[i];

				if(i > 0) sql.append("   AND    ");

				sql.append(fieldAlias);
				sql.append(keyName).append(" = ");
				sql.append(variablePrefix);
				sql.append(SqlBuilderUtil.toJavaFieldName(keyName));
				sql.append(variableSuffix);
				if(i+1 < keyFields.length) sql.append("\r\n        ");
			}
		}else if(fields.length > 0){
			if(useAlternateKey){
				sql.append("\r\n         WHERE    ");
				String fieldName = fields[0];

				sql.append(fieldAlias);
				sql.append(fieldName).append(" = ");
				sql.append(variablePrefix);
				sql.append(SqlBuilderUtil.toJavaFieldName(fieldName));
				sql.append(variableSuffix);
			}
		}

		return sql.toString();
	}

	/**
	 *
	 * @param sql
	 * @param fieldExpression
	 * @param fieldAlias
	 * @param tabExpr
	 * @return
	 */
	protected StringBuffer appendSelectListField(StringBuffer sql, FieldExpression fieldExpression, String fieldAlias, String tabExpr) {

		for ( int i = 0; i < fields.length; i++ ) {
			if( fieldExpression.isSkipField(fields[i]) ) {
				continue;
			}

			String switchFieldExpression = fieldExpression.getSwitchFieldExpression(fields[i]);
			String switchFieldExpressionForStartsWith = fieldExpression.getSwitchFieldExpressionForStartsWith(fields[i]);
			String switchFieldExpressionForEndsWith = fieldExpression.getSwitchFieldExpressionForEndsWith(fields[i]);
			if ( switchFieldExpression != null ) {
				switchFieldExpression = switchFieldExpression.replaceAll("\\$\\{field-name\\}", fieldAlias + fields[i]);
				sql.append(switchFieldExpression);
			} else if ( switchFieldExpressionForStartsWith != null ) {
				switchFieldExpressionForStartsWith = switchFieldExpressionForStartsWith.replaceAll("\\$\\{field-name\\}", fieldAlias + fields[i]);
				sql.append(switchFieldExpressionForStartsWith);
			} else if ( switchFieldExpressionForEndsWith != null ) {
				switchFieldExpressionForEndsWith = switchFieldExpressionForEndsWith.replaceAll("\\$\\{field-name\\}", fieldAlias + fields[i]);
				sql.append(switchFieldExpressionForEndsWith);
			} else {
				sql.append(fieldAlias);
				sql.append(fields[i]);
			}

			if (i+1 < fields.length) {
				sql.append(", ");
			}
			if( (i+1) % 5 == 0 ) {
				sql.append(tabExpr);
			}
		}

		sql = new StringBuffer(sql.toString().trim());
		if(sql.toString().endsWith(", ")){
			sql = new StringBuffer(sql.toString().substring(0, sql.toString().length()-2));
		}
		if(sql.toString().endsWith(",")){
			sql = new StringBuffer(sql.toString().substring(0, sql.toString().length()-1));
		}

		return sql;
	}


	/**
	 * @param tableName
	 * @param alias
	 * @return String
	 */
	protected String makeSelectCount(String tableName, String alias) {
		String tableAlias = alias == null ? "" : (" " + alias);

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT    COUNT(*) CNT");
		sql.append("\r\n          FROM    ");
		sql.append(tableName);
		sql.append(tableAlias);

		return sql.toString();
	}

	/**
	 *
	 * @return
	 */
	protected String makeResultMap() {

		StringBuffer sql = new StringBuffer();

		if(keyFields.length > 0){
			for (int k = 0; k < keyFields.length; k++) {
				sql.append("<id property=\"" + javaKeyFields[k] + "\" column=\"" + keyFields[k] + "\" />\r\n    ");
			}
		}

		for (int i = 0; i < fields.length; i++) {

			if(isKeyField(fields[i]))
				continue;
			if (
				javaFields[i].equals(FieldExpression.RESERVED_EXPR_IS_REMOVED)
				|| javaFields[i].equals(FieldExpression.RESERVED_EXPR_IS_ENABLED)
			)
				continue;

			if( i == fields.length-1) {
				sql.append("<result property=\"" + javaFields[i] + "\" column=\"" + fields[i] + "\" />");
			} else {
				sql.append("<result property=\"" + javaFields[i] + "\" column=\"" + fields[i] + "\" />\r\n    ");
			}
		}

		return sql.toString();
	}

	/**
	 *
	 * @return
	 */
	protected String makeDefaultSelectClause(String schemeName, String tableName) {

		StringBuffer sql = new StringBuffer();

		sql.append("\r\n    SELECT");

		boolean isFirst = true;
		int removedColumnIndex = -1;
		for (int i = 0; i < fields.length; i++) {
			if(StringUtils.isEmpty(fields[i])) continue;
			if (
				javaFields[i].equals(FieldExpression.RESERVED_EXPR_IS_REMOVED)
			) {
				removedColumnIndex = i;
			} else {
				sql.append((isFirst ? " " : ",\r\n           ") + fields[i]);
				isFirst = false;
			}
		}

		sql.append("\r\n      FROM ");
		sql.append(schemeName + "." + tableName);

		String whereClause = "\r\n     WHERE ";
		if(removedColumnIndex >= 0) {
			whereClause += fields[removedColumnIndex] + " = 'N'";
		} else {
			whereClause += "1 = 1";
		}
		sql.append(whereClause + "\r\n  ");

		return sql.toString();
	}

	/**
	 * @param tableName
	 * @param fieldExpression
	 * @return String
	 */
	protected String makeInsert(String schemeName, String tableName, FieldExpression fieldExpression){
		if(fieldExpression == null)
			fieldExpression = new FieldExpression();

		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO ");
		sql.append(schemeName + "." + tableName);
		sql.append(" (\r\n      ");
		int cnt = 0;
		for (int i = 0; i < fields.length; i++) {
			if (FieldExpression.RESERVED_EXPR_MODIFIED_BY.equals(javaFields[i])
					|| FieldExpression.RESERVED_EXPR_MODIFIED_DT.equals(javaFields[i])) {
				continue;
			}
			sql.append(fields[i]);
			if(cnt+1 < fields.length) sql.append(", ");
			if((cnt+1)%5 == 0) sql.append("\r\n      ");
			cnt++;
		}
		sql = new StringBuffer(sql.toString().trim());
		if(sql.toString().endsWith(", ")){
			sql = new StringBuffer(sql.toString().substring(0, sql.toString().length()-2));
		}
		if(sql.toString().endsWith(",")){
			sql = new StringBuffer(sql.toString().substring(0, sql.toString().length()-1));
		}
		sql.append("\r\n    ) VALUES (\r\n      ");
		cnt = 0;
		for (int i = 0; i < fields.length; i++) {
			if (FieldExpression.RESERVED_EXPR_MODIFIED_BY.equals(javaFields[i])
					|| FieldExpression.RESERVED_EXPR_MODIFIED_DT.equals(javaFields[i])) {
				continue;
			}
			if (FieldExpression.RESERVED_EXPR_IS_REMOVED.equals(javaFields[i])) {
				sql.append("'N'");
			} else if (FieldExpression.RESERVED_EXPR_CREATED_BY.equals(javaFields[i])) {
				sql.append("#{_cur.seq}");
			} else if (FieldExpression.RESERVED_EXPR_CREATED_DT.equals(javaFields[i])) {
				sql.append(FieldExpression.RESERVED_EXPR_SYSTEM_DATE);
			} else {
				sql.append(variablePrefix);
				sql.append(javaFields[i]);
				sql.append(variableSuffix);
			}

			if(cnt+1 < fields.length) sql.append(", ");
			if((cnt+1)%5 == 0) sql.append("\r\n      ");
			cnt++;
		}
		sql = new StringBuffer(sql.toString().trim());
		if(sql.toString().endsWith(", ")){
			sql = new StringBuffer(sql.toString().substring(0, sql.toString().length()-2));
		}
		if(sql.toString().endsWith(",")){
			sql = new StringBuffer(sql.toString().substring(0, sql.toString().length()-1));
		}
		sql.append("\r\n    )");
		return sql.toString();
	}

	/**
	 * @param tableName
	 * @param fieldExpression
	 * @return String
	 */
	protected String makeUpdate(String schemeName, String tableName, FieldExpression fieldExpression, String tablePrefix){
		if(fieldExpression == null)
			fieldExpression = new FieldExpression();

		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE ");
		sql.append(schemeName + "." + tableName);
		sql.append("\r\n       SET ");
		for (int i = 0; i < fields.length; i++) {
			if(isKeyField(fields[i]))
				continue;

			if (FieldExpression.RESERVED_EXPR_IS_REMOVED.equals(javaFields[i])
					|| FieldExpression.RESERVED_EXPR_CREATED_BY.equals(javaFields[i])
					|| FieldExpression.RESERVED_EXPR_CREATED_DT.equals(javaFields[i])) {
				continue;
			}
			sql.append(fields[i]);
			if (FieldExpression.RESERVED_EXPR_MODIFIED_BY.equals(javaFields[i])) {
				sql.append(" = #{_cur.seq}");
			} else if (FieldExpression.RESERVED_EXPR_MODIFIED_DT.equals(javaFields[i])) {
				sql.append(" = " + FieldExpression.RESERVED_EXPR_SYSTEM_DATE);
			} else {
				sql.append(" = " + variablePrefix);
				sql.append(javaFields[i]);
				sql.append(variableSuffix);
			}
			if(i+1 < fields.length) sql.append(", ");
			sql.append("\r\n           ");
		}

		sql = new StringBuffer(sql.toString().trim());
		if(sql.toString().endsWith(", ")){
			sql = new StringBuffer(sql.toString().substring(0, sql.toString().length()-2));
		}
		if(sql.toString().endsWith(",")){
			sql = new StringBuffer(sql.toString().substring(0, sql.toString().length()-1));
		}

		if(keyFields.length > 0){
			sql.append("\r\n     WHERE ");
			for (int i = 0; i < keyFields.length; i++) {
				String keyName = keyFields[i];

				if(i > 0) sql.append("       AND ");

				sql.append(keyName).append(" = ");
				sql.append(variablePrefix);
				if(variableDomainName != null && !isKeyFieldNotUseVariableDomainName){
					sql.append(variableDomainName+".");
				}
				sql.append(SqlBuilderUtil.toJavaFieldName(keyName, tablePrefix));
				sql.append(variableSuffix);
				if(i+1 < keyFields.length) sql.append("\r\n");
			}
		}else if(fields.length > 0){
			if(useAlternateKey){
				sql.append("\r\n     WHERE ");
				String field = fields[0];

				sql.append(field).append(" = ");
				sql.append(variablePrefix);
				if(variableDomainName != null){
					sql.append(variableDomainName+".");
				}
				sql.append(SqlBuilderUtil.toJavaFieldName(field, tablePrefix));
				sql.append(variableSuffix);
			}
		}
		return sql.toString();
	}

	/**
	 * @param tableName
	 * @return String
	 */
	protected String makeUpdateDelete(String schemeName, String tableName, String tablePrefix){

		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE ");
		sql.append(schemeName + "." + tableName);
		sql.append("\r\n       SET ");
		sql.append(tablePrefix + FieldExpression.RESERVED_FIELD_IS_REMOVED + " = 'Y',");
		sql.append("\r\n           ");
		sql.append(tablePrefix + FieldExpression.RESERVED_FIELD_MODIFIED_BY + " = #{_cur.seq},");
		sql.append("\r\n           ");
		sql.append(tablePrefix + FieldExpression.RESERVED_FIELD_MODIFIED_DT + " = " + FieldExpression.RESERVED_EXPR_SYSTEM_DATE);
		if(keyFields.length > 0){
			sql.append("\r\n     WHERE ");
			for (int i = 0; i < keyFields.length; i++) {
				String keyName = keyFields[i];

				if(i > 0) sql.append("       AND ");

				sql.append(keyName).append(" = ");
				sql.append(variablePrefix);
				if(variableDomainName != null && !isKeyFieldNotUseVariableDomainName){
					sql.append(variableDomainName+".");
				}
				sql.append(toJavaFieldName(keyName, tablePrefix));
				sql.append(variableSuffix);
				if(i+1 < keyFields.length) sql.append("\r\n");
			}
		}else if(fields.length > 0){
			if(useAlternateKey){
				sql.append("\r\n     WHERE ");
				String fieldName = fields[0];

				sql.append(fieldName).append(" = ");
				sql.append(variablePrefix);
				if(variableDomainName != null){
					sql.append(variableDomainName+".");
				}
				sql.append(toJavaFieldName(fieldName, tablePrefix));
				sql.append(variableSuffix);
			}
		}
		return sql.toString();
	}

	/**
	 * @param tableName
	 * @return String
	 */
	protected String makeDelete(String tableName){

		StringBuffer sql = new StringBuffer();
		sql.append("DELETE FROM    ");
		sql.append(tableName);
		if(keyFields.length > 0){
			sql.append("\r\n         WHERE    ");
			for (int i = 0; i < keyFields.length; i++) {
				String keyName = keyFields[i];

				if(i > 0) sql.append("           AND    ");

				sql.append(keyName).append(" = ");
				sql.append(variablePrefix);
				if(variableDomainName != null && !isKeyFieldNotUseVariableDomainName){
					sql.append(variableDomainName+".");
				}
				sql.append(SqlBuilderUtil.toJavaFieldName(keyName));
				sql.append(variableSuffix);
				if(i+1 < keyFields.length) sql.append("\r\n");
			}
		}else if(fields.length > 0){
			if(useAlternateKey){
				sql.append("\r\n         WHERE    ");
				String fieldName = fields[0];

				sql.append(fieldName).append(" = ");
				sql.append(variablePrefix);
				if(variableDomainName != null){
					sql.append(variableDomainName+".");
				}
				sql.append(SqlBuilderUtil.toJavaFieldName(fieldName));
				sql.append(variableSuffix);
			}
		}
		return sql.toString();
	}


	@SuppressWarnings("serial")
	public String getNullableResult(ResultSet rs, int columnIndex, String nlsLang) throws SQLException {

		String result = rs.getString(columnIndex);

		if(result != null && !"".equals(result)) {
			try {
				if ("US7ASCII".equals(nlsLang)) {
					result = new String(result.getBytes("8859_1"), "MS949");
				}
			} catch (Exception e) {

			}
		}
		return result;
	}
	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.SqlBuilder#setVariablePrefix(java.lang.String)
	 */
	public void setVariablePrefix(String variablePrefix) {
		this.variablePrefix = variablePrefix;
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.SqlBuilder#setVariableSuffix(java.lang.String)
	 */
	public void setVariableSuffix(String variableSuffix) {
		this.variableSuffix = variableSuffix;
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.SqlBuilder#setVariableDomainName(java.lang.String)
	 */
	public void setVariableDomainName(String variableDomainName) {
		this.variableDomainName = variableDomainName;
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.SqlBuilder#setKeyFieldNotUseVariableDomainName(boolean)
	 */
	public void setKeyFieldNotUseVariableDomainName(boolean isKeyFieldNotUseVariableDomainName) {
		this.isKeyFieldNotUseVariableDomainName = isKeyFieldNotUseVariableDomainName;
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.SqlBuilder#setUseAlternateKey(boolean)
	 */
	public void setUseAlternateKey(boolean useAlternateKey) {
		this.useAlternateKey = useAlternateKey;
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.SqlBuilder#setFieldNamesToUpperCase()
	 */
	public void setFieldNamesToUpperCase(){
		this.isUpperCase = true;
		this.isLowerCase = false;
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.SqlBuilder#setFieldNamesToLowerCase()
	 */
	public void setFieldNamesToLowerCase(){
		this.isUpperCase = false;
		this.isLowerCase = true;
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.SqlBuilder#setCancelFieldNamesCase()
	 */
	public void setCancelFieldNamesCase(){
		this.isUpperCase = false;
		this.isLowerCase = false;
	}
}
