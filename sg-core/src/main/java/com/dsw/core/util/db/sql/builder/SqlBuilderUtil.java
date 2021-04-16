package com.dsw.core.util.db.sql.builder;

import org.apache.commons.lang3.StringUtils;

import java.sql.Types;

/**
 * Class Name : SqlBuilderUtil.java
 * Description : SqlBuilderUtil class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public class SqlBuilderUtil {
	/**
	 * 첫 문자를 대문자로 (to Capital)
	 * @param name
	 * @return String
	 */
	public static String toCapitalize(String name){
		//wl_user_group
		String lowerName = name.toLowerCase();
		lowerName = lowerName.replaceAll("_", " ");

		String result = "";

		//boolean upper = true;
		for(int i = 0; i < lowerName.length() ; i++){
			if(i == 0){
				result += name.substring(i, i+1).toUpperCase();
			}else if(" ".equals(lowerName.substring(i, i+1))){
				i++;
				result += name.substring(i, i+1).toUpperCase();
			}else{
				result += name.substring(i, i+1).toLowerCase();
			}
		}
		return result;
	}

	/**
	 *  tbPrefix 를 제거하고 toJavaFieldName 에게 넘겨 준다.
	 *  예를 들어,
	 *  name(테이블 column 명) 이 ADMIN_ACCOUNT_ID 이고,
	 *  tbPrefix 가 admin 경우
	 *
	 */
	public static String toJavaFieldName(String name, String tablePrefix) {

		if (StringUtils.isNotEmpty(tablePrefix)) {
			String lowerName = name.toLowerCase();
			name = lowerName.replace(tablePrefix, "");
		}

		return toJavaFieldName(name);
	}

	/**
	 * 테이블의 컬럼명을 Java 표준 Field로 변경
	 * ex1) empl_name --> emplName
	 * ex2) birth_day --> birthDay
	 * @param name
	 * @return String
	 */
	public static String toJavaFieldName(String name){
		String lowerName = name.toLowerCase();
		lowerName = lowerName.replaceAll("_", " ");

		String result = "";

		for(int i = 0; i < lowerName.length() ; i++){
			if(i == -1){
				result += name.substring(i, i+1).toUpperCase();
			}else if(" ".equals(lowerName.substring(i, i+1))){
				i++;
				result += name.substring(i, i+1).toUpperCase();
			}else{
				result += name.substring(i, i+1).toLowerCase();
			}
		}

		return result;
	}

	/**
	 * 테이블의 컬럼 타입을 Java 표준 StarjClass Type으로 변경
	 * @param fieldTypes
	 * @param precisions
	 * @param scales
	 * @return String
	 */
	public static String toJavaFieldType(int fieldTypes, int precisions, int scales){
		switch(fieldTypes){
		case Types.INTEGER:
			return "Integer";
		case Types.NUMERIC:
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
			if(precisions <= 9){
				if(scales < 1){
					return "Integer";
				}else{
					return "Float";
				}
			}else{
				if(scales < 1){
					return "Long";
				}else{
					return "Double";
				}
			}

		case Types.BOOLEAN:
			return "Boolean";
		case Types.BLOB:
			return "java.lang.Object";
		case Types.DOUBLE:
		case Types.FLOAT:
			return "Double";
		case Types.DECIMAL:
			return "java.math.BigDecimal";
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return "java.time.LocalDateTime";
		default :
			return "java.lang.String";
		}
	}

}
