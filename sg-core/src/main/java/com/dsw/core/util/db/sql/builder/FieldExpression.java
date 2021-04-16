package com.dsw.core.util.db.sql.builder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class Name : FieldExpression.java
 * Description : FieldExpression class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public class FieldExpression {
	public final static String RESERVED_EXPR_SYSTEM_DATE = "#{_cur.dt}";
	public final static String RESERVED_EXPR_FIELD_NAME = "${field-name}";

	/**
	 * 	등록자, 등록일, 수정자, 수정일, 삭제여부, 사용여부 등 공통필드명은 여기를 수정
	 */
	public final static String RESERVED_FIELD_CREATED_BY = "created_by";
	public final static String RESERVED_FIELD_CREATED_DT = "created_date";
	public final static String RESERVED_FIELD_MODIFIED_BY = "modified_by";
	public final static String RESERVED_FIELD_MODIFIED_DT = "modified_date";
	public final static String RESERVED_FIELD_IS_REMOVED = "del_yn";
	public final static String RESERVED_FIELD_IS_ENABLED = "use_yn";

	public final static String RESERVED_EXPR_CREATED_BY = SqlBuilderUtil.toJavaFieldName(RESERVED_FIELD_CREATED_BY);
	public final static String RESERVED_EXPR_CREATED_DT = SqlBuilderUtil.toJavaFieldName(RESERVED_FIELD_CREATED_DT);
	public final static String RESERVED_EXPR_MODIFIED_BY = SqlBuilderUtil.toJavaFieldName(RESERVED_FIELD_MODIFIED_BY);
	public final static String RESERVED_EXPR_MODIFIED_DT = SqlBuilderUtil.toJavaFieldName(RESERVED_FIELD_MODIFIED_DT);
	public final static String RESERVED_EXPR_IS_REMOVED = SqlBuilderUtil.toJavaFieldName(RESERVED_FIELD_IS_REMOVED);
	public final static String RESERVED_EXPR_IS_ENABLED = SqlBuilderUtil.toJavaFieldName(RESERVED_FIELD_IS_ENABLED);

	private final Map<String, String> skipFields = new HashMap<String, String>();
	private final Map<String, String> switchFields = new HashMap<String, String>();
	private final Map<String, String> startsWithSwitchFields = new HashMap<String, String>();
	private final Map<String, String> endsWithSwitchFields = new HashMap<String, String>();
	private final Map<String, String> switchFieldVariables = new HashMap<String, String>();

	/**
	 *
	 * @param fieldName
	 * @return boolean
	 */
	public boolean isSkipField(String fieldName){
		return skipFields.containsKey(fieldName);
	}

	/**
	 *
	 * @param fieldName
	 * @return String
	 */
	public String getSwitchFieldExpression(String fieldName){
		return switchFields.get(fieldName);
	}

	/**
	 *
	 * @param fieldName
	 * @return String
	 */
	public String getSwitchFieldVariableExpression(String fieldName){
		return switchFieldVariables.get(fieldName);
	}

	/**
	 *
	 * @param fieldName
	 * @return String
	 */
	public String getSwitchFieldExpressionForStartsWith(String fieldName){
		Iterator<String> keys = startsWithSwitchFields.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			if(fieldName.startsWith(key)){
				return startsWithSwitchFields.get(key);
			}
		}
		return null;
	}

	/**
	 *
	 * @param fieldName
	 * @return String
	 */
	public String getSwitchFieldExpressionForEndsWith(String fieldName){
		Iterator<String> keys = endsWithSwitchFields.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			if(fieldName.startsWith(key)){
				return endsWithSwitchFields.get(key);
			}
		}
		return null;
	}

	/**
	 *
	 * @param fieldName
	 */
	public void addFieldSkip(String fieldName){
		skipFields.put(fieldName, null);
	}

	/**
	 *
	 * @param fieldName
	 * @param expression
	 */
	public void addFieldSwitch(String fieldName, String expression){
		switchFields.put(fieldName, expression);
	}

	/**
	 * @param fieldNamePrefix
	 * @param expression
	 */
	public void addFieldSwitchStartsWith(String fieldNamePrefix, String expression){
		startsWithSwitchFields.put(fieldNamePrefix, expression);
	}

	/**
	 * @param fieldNameSuffix
	 * @param expression
	 */
	public void addFieldSwitchEndsWith(String fieldNameSuffix, String expression){
		endsWithSwitchFields.put(fieldNameSuffix, expression);
	}

	/**
	 * @param fieldName
	 * @param reservedExpression
	 */
	public void addFieldVariableSwitch(String fieldName, String reservedExpression){
		switchFieldVariables.put(fieldName, reservedExpression);
	}
}
