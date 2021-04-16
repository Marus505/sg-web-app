package com.dsw.core.util.db.sql.builder;

/**
 * Class Name : SqlSource.java
 * Description : SqlSource class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public class SqlSource {
    final static short RESULTMAP = 0;
    final static short DEFAULTSELECTCLAUSE = 1;
	final static short SELECTLIST = 2;
	final static short SELECT = 3;
	final static short INSERT = 4;
	final static short UPDATE = 5;
	final static short DELETE = 6;

	String tableName;
	String alias;
	String[] sqls;
	String[] keys;
	int[] keyTypes;
	String[] fields;
	int[] fieldTypes;
	int[] scales;
	int[] precisions;
	boolean[] nullables;
	String[] comments;
	String[] javaFields;
	String[] javaKeyFields;
	String[] javaTypes;
	String[] javaKeyTypes;
	FieldExpression selectQueryFieldExpression;
	FieldExpression insertQueryFieldExpression;
	FieldExpression updateQueryFieldExpression;

	/**
	 * @param tableName
	 */
	public SqlSource(String tableName){
		this.tableName = tableName;
	}

	/**
	 * @return String
	 */
	public String getSelectSql(){
		return sqls[SELECT];
	}

	/**
	 * @return String
	 */
	public String getInsertSql(){
		return sqls[INSERT];
	}

	/**
	 * @return String
	 */
	public String getUpdateSql(){
		return sqls[UPDATE];
	}

	/**
	 * @return String
	 */
	public String getDeleteSql(){
		return sqls[DELETE];
	}

	/**
	 * @return String
	 */
	public String getSelectListSql() {
		return sqls[SELECTLIST];
	}

    /**
     * @return String
     */
    public String getResultMapSql() {
        return sqls[RESULTMAP];
    }

    /**
     * @return String
     */
    public String getDefaultSelectClauseSql() {
        return sqls[DEFAULTSELECTCLAUSE];
    }

	/**
	 * @return String[]
	 */
	public String[] getKeys(){
		return keys;
	}

	/**
	 * @return int[]
	 */
	public int[] getKeyTypes(){
		return keyTypes;
	}

	/**
	 * @param keys
	 */
	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	/**
	 * @param keyTypes
	 */
	public void setKeyTypes(int[] keyTypes) {
		this.keyTypes = keyTypes;
	}

	/**
	 * @param sqls
	 */
	public void setSqls(String[] sqls) {
		this.sqls = sqls;
	}

	/**
	 * @param tableName
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return String
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @return String
	 */
	public String getAlias() {
		return this.alias;
	}

	/**
	 * @param alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return String[]
	 */
	public String[] getFields() {
		return fields;
	}

	/**
	 * @param fields
	 */
	public void setFields(String[] fields) {
		this.fields = fields;
	}

	/**
	 * @return int[]
	 */
	public int[] getFieldTypes() {
		return fieldTypes;
	}

	/**
	 * @param fieldTypes
	 */
	public void setFieldTypes(int[] fieldTypes) {
		this.fieldTypes = fieldTypes;
	}

	/**
	 * @param precisions
	 */
	public void setPrecisions(int[] precisions){
		this.precisions = precisions;
	}

	/**
	 * @return int[]
	 */
	public int[] getPrecisions(){
		return this.precisions;
	}

	/**
	 * @param scales
	 */
	public void setScales(int[] scales){
		this.scales = scales;
	}

	/**
	 * @return int[]
	 */
	public int[] getScales(){
		return this.scales;
	}

	/**
	 * @return String[]
	 */
	public String[] getJavaTypes() {
		return this.javaTypes;
	}

	/**
	 * @param javaTypes
	 */
	public void setJavaTypes(String[] javaTypes){
		this.javaTypes = javaTypes;
	}


	/**
	 * @return String[]
	 */
	public String[] getJavaKeyTypes() {
		return this.javaKeyTypes;
	}

	/**
	 * @param javaKeyTypes
	 */
	public void setJavaKeyTypes(String[] javaKeyTypes) {
		this.javaKeyTypes = javaKeyTypes;
	}

	/**
	 * @return the comments
	 */
	public String[] getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String[] comments) {
		this.comments = comments;
	}

	/**
	 * @return String[]
	 */
	public String[] getJavaFields() {
		return this.javaFields;
	}

	/**
	 * @param javaFields
	 */
	public void setJavaFields(String[] javaFields) {
		this.javaFields = javaFields;
	}

	/**
	 * @return String[]
	 */
	public String[] getJavaKeyFields() {
		return this.javaKeyFields;
	}

	/**
	 * @param javaKeyFields
	 */
	public void setJavaKeyFields(String[] javaKeyFields) {
		this.javaKeyFields = javaKeyFields;
	}

	/**
	 * @return boolean[]
	 */
	public boolean[] getNullables() {
		return this.nullables;
	}

	/**
	 * @param nullables
	 */
	public void setNullables(boolean[] nullables) {
		this.nullables = nullables;
	}

	/**
	 * @return the selectQueryFieldExpression
	 */
	public FieldExpression getSelectQueryFieldExpression() {
		return this.selectQueryFieldExpression;
	}

	/**
	 * @return the insertQueryFieldExpression
	 */
	public FieldExpression getInsertQueryFieldExpression() {
		return this.insertQueryFieldExpression;
	}

	/**
	 * @return the updateQueryFieldExpression
	 */
	public FieldExpression getUpdateQueryFieldExpression() {
		return this.updateQueryFieldExpression;
	}

	/**
	 * @param selectQueryFieldExpression
	 */
	public void setSelectQueryFieldExpression(FieldExpression selectQueryFieldExpression) {
		this.selectQueryFieldExpression = selectQueryFieldExpression;
	}

	/**
	 * @param insertQueryFieldExpression
	 */
	public void setInsertQueryFieldExpression(FieldExpression insertQueryFieldExpression) {
		this.insertQueryFieldExpression = insertQueryFieldExpression;
	}

	/**
	 * @param updateQueryFieldExpression
	 */
	public void setUpdateQueryFieldExpression(FieldExpression updateQueryFieldExpression) {
		this.updateQueryFieldExpression = updateQueryFieldExpression;
	}

}
