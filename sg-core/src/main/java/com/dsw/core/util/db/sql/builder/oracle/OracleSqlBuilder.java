package com.dsw.core.util.db.sql.builder.oracle;

import com.dsw.core.util.db.sql.builder.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dsw.core.util.db.sql.builder.SqlBuilderUtil.toJavaFieldName;

/**
 * Class Name : OracleSqlBuilder.java
 * Description : OracleSqlBuilder class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public class OracleSqlBuilder extends AbstractSqlBuilder implements SqlBuilder {

	final static String DATE_RESERVE_KEYWORD = "SYSDATE";

	/**
	 * @param dataSouce
	 */
	public OracleSqlBuilder(DataSource dataSouce) {
		super(dataSouce, DATE_RESERVE_KEYWORD);
	}

	/**
	 * @param dsName
	 */
	public OracleSqlBuilder(String dsName) {
		super(dsName, DATE_RESERVE_KEYWORD);
	}

	/**
	 * @param driverClass
	 * @param url
	 * @param user
	 * @param password
	 */
	public OracleSqlBuilder(String driverClass, String url, String user, String password){
		super(driverClass, url, user, password, DATE_RESERVE_KEYWORD);
	}

	public SqlSource build(String tableName, String alias, String tablePrefix, FieldExpression selectQueryFieldExpression, FieldExpression insertQueryFieldExpression, FieldExpression updateQueryFieldExpression) throws SQLException{
		SqlSource sqlSource = null;

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		ResultSetMetaData resultSetMetaData = null;
		try{
			connection = getConnection();

			DatabaseMetaData databaseMetaData = connection.getMetaData();

			String _tableName = tableName;
			String _schemeName = null;
			if (_tableName.indexOf(".") > -1 ) {
				_schemeName = tableName.substring(0, tableName.lastIndexOf("."));
				_tableName = tableName.substring(tableName.lastIndexOf(".")+1);
			}
			resultSet = databaseMetaData.getPrimaryKeys(null, _schemeName, _tableName);
			List<String> pkFields = new ArrayList<String>();
			while(resultSet.next()){
				pkFields.add(resultSet.getString(5) + "," + resultSet.getString(4));
			}
			try{if(resultSet != null){ resultSet.close(); } }catch(Exception e){ e.printStackTrace(); }

			statement = connection.createStatement();

			//NLS_CHARACTERSET 받아오는 곳
			String nlsLangQuery = getNlsLangSelectQuery();
			resultSet = statement.executeQuery(nlsLangQuery);
			resultSet.next();
			String nlsLang = resultSet.getString(1);
			try{if(resultSet != null){ resultSet.close(); } }catch(Exception e){ e.printStackTrace(); }

			//comments 받아오는 부분
			String commentsQuery = getCommentsSelectQuery(tableName);
			resultSet = statement.executeQuery(commentsQuery);
			HashMap<String, String> commentsMap = getResultMap(resultSet, nlsLang);
			try{if(resultSet != null){ resultSet.close(); } }catch(Exception e){ e.printStackTrace(); }

			resultSet = statement.executeQuery(getDummySelectQuery(tableName, databaseMetaData));
			resultSetMetaData = resultSet.getMetaData();
			int fieldCount = resultSetMetaData.getColumnCount();
			fields = new String[fieldCount];
			types = new int[fieldCount];
			precisions = new int[fieldCount];
			scales = new int[fieldCount];
			nullables = new boolean[fieldCount];
			javaFields = new String[fieldCount];
			javaTypes = new String[fieldCount];
			comments = new String[fieldCount];
			for(int i = 0; i < fieldCount; i++){
				fields[i] = resultSetMetaData.getColumnName(i+1);
				if (commentsMap.containsKey(fields[i]))
					comments[i] = commentsMap.get(fields[i]);

				if(fields[i] != null){
					if(isUpperCase){
						fields[i] = fields[i].toUpperCase();
					}else if(isLowerCase){
						fields[i] = fields[i].toLowerCase();
					}
				}
				types[i] = resultSetMetaData.getColumnType(i+1);
				precisions[i] = resultSetMetaData.getPrecision(i+1);
				scales[i] = resultSetMetaData.getScale(i+1);
				nullables[i] = resultSetMetaData.isNullable(i+1) == 1;
				javaFields[i] = toJavaFieldName(fields[i], tablePrefix);
				javaTypes[i] = SqlBuilderUtil.toJavaFieldType(types[i], precisions[i], scales[i]);
			}

			//Keys
			int pkFieldCount = pkFields.size();
			keyFields = new String[pkFieldCount];
			keyTypes = new int[pkFieldCount];
			javaKeyFields = new String[pkFieldCount];
			javaKeyTypes = new String[pkFieldCount];
			for(int i = 0; i < pkFieldCount; i++){
				String _field = pkFields.get(i);
				if (isUpperCase) {
					keyFields[Integer.parseInt(_field.substring(0, 1))-1] = _field.substring(2).toUpperCase();
				} else {
					keyFields[Integer.parseInt(_field.substring(0, 1))-1] = _field.substring(2).toLowerCase();
				}

				javaKeyFields[Integer.parseInt(_field.substring(0, 1))-1] = toJavaFieldName(_field.substring(2), tablePrefix);

				for (int j = 0; j < fields.length; j++) {
					String _pkField = _field.substring(2);
					if (isUpperCase) {
						_pkField = _pkField.toUpperCase();
					} else {
						_pkField = _pkField.toLowerCase();
					}

					if(fields[j].equals(_pkField)){
						keyTypes[Integer.parseInt(_field.substring(0, 1))-1] = types[j];
						javaKeyTypes[Integer.parseInt(_field.substring(0, 1))-1] = SqlBuilderUtil.toJavaFieldType(types[j], precisions[j], scales[j]);
					}
				}
			}

			sqls[0] = makeSelect(_schemeName, _tableName, alias, selectQueryFieldExpression);
			sqls[1] = makeInsert(_schemeName, _tableName, insertQueryFieldExpression);
			sqls[2] = makeUpdate(_schemeName, _tableName, updateQueryFieldExpression, tablePrefix);
			sqls[3] = makeUpdateDelete(_schemeName, _tableName, tablePrefix);
			sqls[4] = makeSelectCount(_tableName, alias);
			sqls[5] = makeSelectList(_tableName, alias, selectQueryFieldExpression);
            sqls[6] = makeResultMap();

		}finally{
			try{if(resultSet != null) resultSet.close();}catch(Exception e){e.printStackTrace();}
			try{if(statement != null) statement.close();}catch(Exception e){e.printStackTrace();}
			try{if(connection != null) connection.close();}catch(Exception e){e.printStackTrace();}
		}

		sqlSource = new SqlSource(tableName);
		sqlSource.setAlias(alias);
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
		sqlSource.setComments(comments);
		sqlSource.setSelectQueryFieldExpression(selectQueryFieldExpression);
		sqlSource.setInsertQueryFieldExpression(insertQueryFieldExpression);
		sqlSource.setUpdateQueryFieldExpression(updateQueryFieldExpression);

		return sqlSource;
	}

	private String makeSelectList(String tableName, String alias, FieldExpression fieldExpression) {
		String fieldAlias = alias == null ? "" : (alias + ".");
		String tableAlias = alias == null ? "" : (" " + alias);
		if(fieldExpression == null)
			fieldExpression = new FieldExpression();

		StringBuffer sql = new StringBuffer();

		sql.append("<include refid=\"Common.pagingHeader-oracle\"/>\r\t\t");

		sql.append("SELECT	");

		sql = super.appendSelectListField(sql, fieldExpression, fieldAlias, "\r\n\t\t\t\t");

		sql.append("\r\n\t\t  FROM\t");
		sql.append(tableName);
		sql.append(tableAlias);

		sql.append("\r\t\t<include refid=\"${parameterClass}Condition\"/>");
		sql.append("\r\t\t<include refid=\"Common.sort\"/>");
		sql.append("\r\t\t<include refid=\"Common.pagingFooter-oracle\"/>");

		return sql.toString();
	}

	/**
	 * @param tableName
	 * @param alias
	 * @param fieldExpression
	 * @return String
	 */
	protected String makeSelect(String tableName, String alias, FieldExpression fieldExpression){
		String fieldAlias = alias == null ? "" : (alias + ".");
		String tableAlias = alias == null ? "" : (" " + alias);
		if(fieldExpression == null)
			fieldExpression = new FieldExpression();

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT\t");

		sql = super.appendSelectListField(sql, fieldExpression, fieldAlias, "\r\n\t\t\t\t");

		sql.append("\r\n\t\t  FROM\t");
		sql.append(tableName);
		sql.append(tableAlias);
		if(keyFields.length > 0){
			sql.append("\r\n\t\t WHERE\t");
			for (int i = 0; i < keyFields.length; i++) {
				String keyName = keyFields[i];

				if(i > 0) sql.append("   AND\t");

				sql.append(fieldAlias);
				sql.append(keyName).append(" = ");
				sql.append(variablePrefix);
				sql.append(javaKeyFields[i]);
				sql.append(variableSuffix);
				if(i+1 < keyFields.length) sql.append("\r\n\t\t");
			}
		}else if(fields.length > 0){
			if(useAlternateKey){
				sql.append("\r\n\t\t WHERE\t");
				String fieldName = fields[0];

				sql.append(fieldAlias);
				sql.append(fieldName).append(" = ");
				sql.append(variablePrefix);
				sql.append(javaFields[0]);
				sql.append(variableSuffix);
			}
		}

		return sql.toString();
	}

	/**
	 * @param tableName
	 * @param fieldExpression
	 * @return String
	 */
	protected String makeInsert(String tableName, FieldExpression fieldExpression) {
		if(fieldExpression == null)
			fieldExpression = new FieldExpression();

		StringBuffer sql = new StringBuffer();

		sql.append("<selectKey resultType=\"java.lang.Long\" keyProperty=\"" + toJavaFieldName(keyFields[0]) + "\" order=\"BEFORE\">\r\n\t\t\t");
		sql.append("SELECT seq_" +  keyFields[0] + ".NEXTVAL FROM DUAL\r\n\t\t");
		sql.append("</selectKey>\r\n\r\n\t\t");

		sql.append("INSERT INTO ");
		sql.append(tableName);
		sql.append(" (\r\n\t\t\t");

		for (int i = 0; i < fields.length; i++) {
			if(fieldExpression.isSkipField(fields[i])){
				continue;
			}

			sql.append(fields[i]);
			if(i+1 < fields.length) sql.append(", ");
			if((i+1)%5 == 0) sql.append("\r\n\t\t\t");
		}

		sql = new StringBuffer(sql.toString().trim());
		if(sql.toString().endsWith(", ")){
			sql = new StringBuffer(sql.toString().substring(0, sql.toString().length()-2));
		}
		if(sql.toString().endsWith(",")){
			sql = new StringBuffer(sql.toString().substring(0, sql.toString().length()-1));
		}
		sql.append("\r\n\t\t) VALUES (\r\n\t\t\t");

		for (int i = 0; i < fields.length; i++) {
			if(fieldExpression.isSkipField(fields[i])){
				continue;
			}

			String switchFieldVariableExpression = fieldExpression.getSwitchFieldVariableExpression(fields[i]);
			String switchFields = fieldExpression.getSwitchFieldExpression(fields[i]);
			if(switchFieldVariableExpression != null){
				switchFieldVariableExpression = switchFieldVariableExpression.replaceAll("\\$\\{system-date\\}", SYSTEM_DATE_RESERVED_WORD);
				sql.append(switchFieldVariableExpression);
			}else if(switchFields != null){
				sql.append(variablePrefix);
				sql.append(switchFields);
				sql.append(variableSuffix);
			}else{

				if(!FieldExpression.RESERVED_FIELD_CREATED_DT.equals(toJavaFieldName(fields[i]))) {
					sql.append(variablePrefix);
					if(isKeyField(fields[i])){
						if(variableDomainName != null && !isKeyFieldNotUseVariableDomainName){
							sql.append(variableDomainName+".");
						}
					}else{
						if(variableDomainName != null){
							sql.append(variableDomainName+".");
						}
					}

					sql.append(toJavaFieldName(fields[i]));
					sql.append(variableSuffix);
				} else {
					sql.append(DATE_RESERVE_KEYWORD);
				}
			}

			if(i+1 < fields.length) sql.append(", ");
			if((i+1)%5 == 0) sql.append("\r\n\t\t\t");
		}
		sql = new StringBuffer(sql.toString().trim());
		if(sql.toString().endsWith(", ")){
			sql = new StringBuffer(sql.toString().substring(0, sql.toString().length()-2));
		}
		if(sql.toString().endsWith(",")){
			sql = new StringBuffer(sql.toString().substring(0, sql.toString().length()-1));
		}
		sql.append("\r\n\t\t)");
		return sql.toString();
	}

	/**
	 *
	 * @return
	 * @throws SQLException
	 */
	public List<String> getTableNames()  throws SQLException {

		String query = "SELECT table_name FROM tabs ORDER BY table_name";

		return super.getTableNames(query);
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.AbstractSqlBuilder#getDummySelectQuery(java.lang.String, java.sql.DatabaseMetaData)
	 */
	protected String getDummySelectQuery(String tableName, DatabaseMetaData databaseMetaData){
		return "SELECT * FROM " + tableName + " WHERE ROWNUM < 1";
	}

	protected String getCommentsSelectQuery(String tableName) {
		return "SELECT COLUMN_NAME, COMMENTS FROM USER_COL_COMMENTS WHERE TABLE_NAME = '" + tableName + "'";
	}

	protected String getNlsLangSelectQuery() {
		return "SELECT VALUE FROM v$nls_parameters WHERE PARAMETER = 'NLS_CHARACTERSET'";
	}

}
