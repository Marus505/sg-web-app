package com.dsw.core.util.db.sql.builder.postgresql;

import com.dsw.core.util.db.sql.builder.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dsw.core.util.db.sql.builder.SqlBuilderUtil.toJavaFieldName;

/**
 * Class Name : PostgresqlBuilder.java
 * Description : PostgresqlBuilder class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 20211. 03. 04.
 * @version 1.0
 *
 */
public class PostgreSqlBuilder extends AbstractSqlBuilder implements SqlBuilder {

  final static String DATE_RESERVE_KEYWORD = "current_timestamp";

  /**
   * @param dataSouce
   */
  public PostgreSqlBuilder(DataSource dataSouce) {
    super(dataSouce, DATE_RESERVE_KEYWORD);
  }

  /**
   * @param dsName
   */
  public PostgreSqlBuilder(String dsName) {
    super(dsName, DATE_RESERVE_KEYWORD);
  }

  /**
   * @param driverClass
   * @param url
   * @param user
   * @param password
   */
  public PostgreSqlBuilder(String driverClass, String url, String user, String password){
    super(driverClass, url, user, password, DATE_RESERVE_KEYWORD);
  }

  public SqlSource build(String tableName, String alias, String tablePrefix, FieldExpression selectQueryFieldExpression, FieldExpression insertQueryFieldExpression, FieldExpression updateQueryFieldExpression) throws SQLException {
    SqlSource sqlSource = null;

    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    ResultSetMetaData resultSetMetaData = null;
    try{
      connection = getConnection();

      DatabaseMetaData databaseMetaData = connection.getMetaData();

      String _tableName = tableName;
      String _schemeName = "hbep";
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

      sqls[0] = makeResultMap();
      sqls[1] = makeDefaultSelectClause(_schemeName, _tableName);
      sqls[2] = makeSelectList();
      sqls[3] = makeSelect();
      sqls[4] = makeInsert(_schemeName, _tableName, insertQueryFieldExpression);
      sqls[5] = makeUpdate(_schemeName, _tableName, updateQueryFieldExpression, tablePrefix);
      sqls[6] = makeUpdateDelete(_schemeName, _tableName, tablePrefix);

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

  private String makeSelectList() {
    StringBuffer sql = new StringBuffer();
    sql.append("<if test=\"conditions != null\">");
    for ( int i = 0; i < fields.length; i++ ) {
      sql.append("\r\n      ");
      if(isKeyField(fields[i])) {
        /* id (equal) */
        sql.append("<if test=\"@kr.co.hbep.core.util.CommonUtil@isNotEmpty(conditions." + javaFields[i] + ")\">\r\n      ");
        sql.append("  AND " + fields[i] + " = #{conditions." + javaFields[i] + "}\r\n      ");
        sql.append("</if>");
      } else if(javaTypes[i].contains("Date") || javaTypes[i].contains("Time")) {
        /* date */
        String fieldPrefix = javaFields[i].substring(0, javaFields[i].lastIndexOf("Date"));
        String startField = fieldPrefix + "StartDate";
        String endField = fieldPrefix + "EndDate";
        sql.append("<if test=\"@kr.co.hbep.core.util.CommonUtil@isNotEmpty(conditions." + startField + ")\">\r\n      ");
        sql.append("  AND " + fields[i] + " = #{conditions." + startField + "}\r\n      ");
        sql.append("</if>\r\n          ");
        sql.append("<if test=\"@kr.co.hbep.core.util.CommonUtil@isNotEmpty(conditions." + endField + ")\">\r\n      ");
        sql.append("  AND " + fields[i] + " = #{conditions." + endField + "}\r\n      ");
        sql.append("</if>");
      } else {
        /* etc (like) */
        sql.append("<if test=\"@kr.co.hbep.core.util.CommonUtil@isNotEmpty(conditions." + javaFields[i] + ")\">\r\n      ");
        sql.append("  AND " + fields[i] + " LIKE '%' || #{conditions." + javaFields[i] + "} || '%'\r\n      ");
        sql.append("</if>");
      }
    }
    sql.append("\r\n    </if>");

    return sql.toString();
  }

  /**
   * @return String
   */
  protected String makeSelect(){
    StringBuffer sql = new StringBuffer();

    for ( int i = 0; i < keyFields.length; i++ ) {
      if(i != 0) {
        sql.append("\r\n    ");
      }
      sql.append("AND " + keyFields[i] + " = #{" + javaKeyFields[i] + "}");
    }

    return sql.toString();
  }

  /**
   *
   * @return
   * @throws SQLException
   */
  public List<String> getTableNames()  throws SQLException {

    String query = "SELECT table_name\n" +
        "  FROM information_schema.tables\n" +
        " WHERE table_schema='hbep'\n" +
        "   AND table_type='BASE TABLE'";

    return super.getTableNames(query);
  }

  /* (non-Javadoc)
   * @see com.dsw.core.util.db.sql.builder.AbstractSqlBuilder#getDummySelectQuery(java.lang.String, java.sql.DatabaseMetaData)
   */
  protected String getDummySelectQuery(String tableName, DatabaseMetaData databaseMetaData){
    return "SELECT * FROM " + tableName + " LIMIT 1";
  }

  protected String getCommentsSelectQuery(String tableName) {

    return "SELECT\n" +
        "    attname,\n" +
        "    pg_catalog.col_description(attrelid, attnum)\n" +
        "FROM\n" +
        "    pg_catalog.pg_attribute\n" +
        "WHERE\n" +
        "    attrelid = (SELECT oid FROM pg_catalog.pg_class WHERE relname = '" + tableName + "') AND\n" +
        "    attnum > 0 AND\n" +
        "    NOT attisdropped";
  }

  protected String getNlsLangSelectQuery() {
    return "SELECT pg_encoding_to_char(encoding) FROM pg_database WHERE datname = 'hbep_db'";
  }
}
