package com.dsw.core.util.db.sql.builder.mysql;

import com.dsw.core.util.db.sql.builder.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MySqlBuilder extends AbstractSqlBuilder implements SqlBuilder {

	final static String DATE_RESERVE_KEYWORD = "NOW()";

	/**
	 * @param dataSouce
	 */
	public MySqlBuilder(DataSource dataSouce) {
		super(dataSouce, DATE_RESERVE_KEYWORD);
	}

	/**
	 * @param dsName
	 */
	public MySqlBuilder(String dsName) {
		super(dsName, DATE_RESERVE_KEYWORD);
	}

	/**
	 * @param driverClass
	 * @param url
	 * @param user
	 * @param password
	 */
	public MySqlBuilder(String driverClass, String url, String user, String password){
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

			//comments 받아오는 부분
			String commentsQuery = getCommentsSelectQuery(tableName);
			resultSet = statement.executeQuery(commentsQuery);
			HashMap<String, String> commentsMap = getResultMap(resultSet, 9);
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
				String _field = pkFields.get(i);
				keyFields[Integer.parseInt(_field.substring(0, 1))-1] = _field.substring(2);
				javaKeyFields[Integer.parseInt(_field.substring(0, 1))-1] = SqlBuilderUtil.toJavaFieldName(_field.substring(2), tablePrefix);

				for (int j = 0; j < fields.length; j++) {
					if(fields[j].equals(_field.substring(2))){
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
		if( fieldExpression == null ) {
			fieldExpression = new FieldExpression();
		}

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT\t");

		sql = super.appendSelectListField(sql, fieldExpression, fieldAlias, "\r\n\t\t\t\t");

		sql.append("\r\n\t\t  FROM\t");
		sql.append(tableName);
		sql.append(tableAlias);
		sql.append("\r\n\t\t LIMIT	#{firstIndex}, #{recordCountPerPage}");

		return sql.toString();
	}

	/**
	 *
	 * @return
	 * @throws SQLException
	 */
	public List<String> getTableNames()  throws SQLException {

		String query = "show tables";

		return super.getTableNames(query);
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.db.sql.builder.AbstractSqlBuilder#getDummySelectQuery(java.lang.String, java.sql.DatabaseMetaData)
	 */
	protected String getDummySelectQuery(String tableName, DatabaseMetaData databaseMetaData){
		return "SELECT * FROM " + tableName + " LIMIT 0, 1";
	}

	protected String getCommentsSelectQuery(String tableName) {
		String rtn =
				"SHOW FULL COLUMNS FROM " + tableName;
		return rtn;
	}

	protected String getNlsLangSelectQuery() {
		return "SELECT VALUE FROM v$nls_parameters WHERE PARAMETER = 'NLS_CHARACTERSET'";
	}

}
