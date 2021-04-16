package com.dsw.core.util.db.sql.builder.sqlserver;

import com.dsw.core.util.db.sql.builder.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class SqlServerBuilder extends AbstractSqlBuilder implements SqlBuilder {

	final static String DATE_RESERVE_KEYWORD = "GETDATE()";

	/**
	 * @param dataSouce
	 */
	public SqlServerBuilder(DataSource dataSouce) {
		super(dataSouce, "GETDATE()");
	}

	/**
	 * @param dsName
	 */
	public SqlServerBuilder(String dsName) {
		super(dsName, "GETDATE()");
	}

	/**
	 * @param driverClass
	 * @param url
	 * @param user
	 * @param password
	 */
	public SqlServerBuilder(String driverClass, String url, String user, String password){
		super(driverClass, url, user, password, "GETDATE()");
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

			//comments 받아오는 부분
			String commentsQuery = getCommentsSelectQuery(tableName);
			resultSet = statement.executeQuery(commentsQuery);
			HashMap<String, String> commentsMap = getResultMap(resultSet);
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
				javaFields[i] = SqlBuilderUtil.toJavaFieldName(fields[i], tablePrefix);
				javaTypes[i] = SqlBuilderUtil.toJavaFieldType(types[i], precisions[i], scales[i]);
			}

			//Keys
			int pkFieldCount = pkFields.size();
			keyFields = new String[pkFieldCount];
			keyTypes = new int[pkFieldCount];
			javaKeyFields = new String[pkFieldCount];
			javaKeyTypes = new String[pkFieldCount];
			for(int i = 0; i < pkFieldCount; i++){
				String _field = (String)pkFields.get(i);
				keyFields[Integer.parseInt(_field.substring(0, 1))-1] = _field.substring(2);
				javaKeyFields[Integer.parseInt(_field.substring(0, 1))-1] = SqlBuilderUtil.toJavaFieldName(_field.substring(2), tablePrefix);

				for (int j = 0; j < fields.length; j++) {
					if(fields[j].equals(_field.substring(2))){
						keyTypes[Integer.parseInt(_field.substring(0, 1))-1] = types[j];
						javaKeyTypes[Integer.parseInt(_field.substring(0, 1))-1] = SqlBuilderUtil.toJavaFieldType(types[j], precisions[j], scales[j]);
					}
				}
			}

            sqls[0] = makeSelect(_schemeName, tableName, alias, selectQueryFieldExpression);
			sqls[1] = makeInsert(_schemeName, tableName, insertQueryFieldExpression);
			sqls[2] = makeUpdate(_schemeName, tableName, updateQueryFieldExpression, tablePrefix);
			sqls[3] = makeUpdateDelete(_schemeName, tableName, tablePrefix);
			sqls[4] = makeSelectCount(tableName, alias);
			sqls[5] = makeSelectList(tableName, alias, selectQueryFieldExpression);
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
		String fieldName = fields[0];

		sql.append("SELECT\tAA.*");
		sql.append("\r\n\t\t  FROM\t(");
		sql.append("\r\n\t\t\t\tSELECT	ROW_NUMBER() OVER ");
		sql.append("(ORDER BY ");
		sql.append(fieldName);
		sql.append(" DESC)");
		sql.append(" RN, ");
		sql.append(" COUNT(*) OVER() CNT,\r\n\t\t\t\t\t\t");

		super.appendSelectListField(sql, fieldExpression, fieldAlias, "\r\n\t\t\t\t\t\t");

		sql.append("\r\n\t\t\t\t  FROM\t");
		sql.append(tableName);
		sql.append(tableAlias);
		sql.append("\r\n\t\t\t\t) AA");
		sql.append("\r\n\t\t WHERE\t");
		sql.append("RN BETWEEN #{firstIndex} AND #{lastIndex}");


		return sql.toString();
	}

	/**
	 *
	 * @return
	 * @throws SQLException
	 */
	public List<String> getTableNames()  throws SQLException {

		String query = "SELECT table_name FROM information_schema.tables ORDER BY table_name";

		return super.getTableNames(query);
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.AbstractSqlBuilder#getDummySelectQuery(java.lang.String, java.sql.DatabaseMetaData)
	 */
	protected String getDummySelectQuery(String tableName, DatabaseMetaData databaseMetaData){
		return "SELECT TOP 1 * FROM " + tableName;
	}

	protected String getCommentsSelectQuery(String tableName) {
		String rtn =
				"SELECT OBJNAME AS COLUMN_NAME, cast(value as varchar) AS COMMENTS "
				+ "FROM fn_listextendedproperty ('MS_DESCRIPTION', 'schema', 'dbo', 'table', '" + tableName + "', 'column', null)";
		return rtn;
	}

	protected String getNlsLangSelectQuery() {
		return "SELECT VALUE FROM v$nls_parameters WHERE PARAMETER = 'NLS_CHARACTERSET'";
	}



}
