package com.dsw.core.util.box;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Class Name : AbstractKeyBox.java
 * Description : AbstractKeyBox class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013.11.06
 * @version 1.0
 *
 */

public abstract class AbstractKeyBox<K, V> implements KeyBox<K, V> {

	/**
	 * Converts key for Date type.
	 * @param key
	 * @return Date
	 */
	public final Date getDate(K key){
		return (Date)get(key);
	}

	/**
	 * Converts key for Date type.
	 * @param key
	 * @param defaultCurrentDate
	 * @return Date
	 */
	public final Date getDate(K key, boolean defaultCurrentDate){
		Object obj = get(key);
		if(obj == null)
			return new Date();
		return (Date)obj;
	}

	/**
	 * Converts key for String type.
	 * @param key
	 * @return String
	 */
	public final String getString(K key){
		return (String)get(key);
	}

	/**
	 * Converts key for String type.
	 * @param key
	 * @param defaultValue
	 * @return String
	 */
	public final String getString(K key, String defaultValue){
		String str = getString(key);
		if(str != null) return str;
		else return defaultValue;
	}

	/**
	 * Converts key for short type.
	 * @param key
	 * @return short
	 */
	public final short getShort(K key){
		Object obj = get(key);
		if(obj == null)
			return -1;
		if (obj instanceof String) {
			String str = (String) obj;
			return Short.parseShort(str);
		}
		return ((Number)obj).shortValue();
	}

	/**
	 * Converts key for short type.
	 * @param key
	 * @param defaultValue
	 * @return short
	 */
	public final short getShort(K key, short defaultValue){
		Object obj = get(key);
		if(obj == null)
			return defaultValue;
		if (obj instanceof String) {
			String str = (String) obj;
			return Short.parseShort(str);
		}
		return ((Number)obj).shortValue();
	}

	/**
	 * Converts key for int type.
	 * @param key
	 * @return int
	 */
	public final int getInt(K key){
		Object obj = get(key);
		if(obj == null)
			return -1;
		if (obj instanceof String) {
			String str = (String) obj;
			return Integer.parseInt(str);
		}
		return ((Number)obj).intValue();
	}

	/**
	 * Converts key for int type.
	 * @param key
	 * @param defaultValue
	 * @return int
	 */
	public final int getInt(K key, int defaultValue){
		Object obj = get(key);
		if(obj == null)
			return defaultValue;
		if (obj instanceof String) {
			String str = (String) obj;
			return Integer.parseInt(str);
		}
		return ((Number)obj).intValue();
	}

	/**
	 * Converts key for long type.
	 * @param key
	 * @return long
	 */
	public final long getLong(K key){
		Object obj = get(key);
		if(obj == null)
			return -1L;
		if (obj instanceof String) {
			String str = (String) obj;
			return Long.parseLong(str);
		}
		return ((Number)obj).longValue();
	}

	/**
	 * Converts key for long type.
	 * @param key
	 * @param defaultValue
	 * @return Date
	 */
	public final long getLong(K key, long defaultValue){
		Object obj = get(key);
		if(obj == null)
			return defaultValue;
		if (obj instanceof String) {
			String str = (String) obj;
			return Long.parseLong(str);
		}
		return ((Number)obj).longValue();
	}

	/**
	 * Converts key for float type.
	 * @param key
	 * @return float
	 */
	public final float getFloat(K key){
		Object obj = get(key);
		if(obj == null)
			return -1F;
		if (obj instanceof String) {
			String str = (String) obj;
			return Float.parseFloat(str);
		}
		return ((Number)obj).floatValue();
	}

	/**
	 * Converts key for float type.
	 * @param key
	 * @param defaultValue
	 * @return float
	 */
	public final float getFloat(K key, float defaultValue){
		Object obj = get(key);
		if(obj == null)
			return defaultValue;
		if (obj instanceof String) {
			String str = (String) obj;
			return Float.parseFloat(str);
		}
		return ((Number)obj).floatValue();
	}

	/**
	 * Converts key for double type.
	 * @param key
	 * @return double
	 */
	public final double getDouble(K key){
		Object obj = get(key);
		if(obj == null)
			return -1D;
		if (obj instanceof String) {
			String str = (String) obj;
			return Double.parseDouble(str);
		}
		return ((Number)obj).doubleValue();
	}

	/**
	 * Converts key for double type.
	 * @param key
	 * @param defaultValue
	 * @return double
	 */
	public final double getDouble(K key, double defaultValue){
		Object obj = get(key);
		if(obj == null)
			return defaultValue;
		if (obj instanceof String) {
			String str = (String) obj;
			return Double.parseDouble(str);
		}
		return ((Number)obj).doubleValue();
	}

	/**
	 * Converts key for boolean type.
	 * @param key
	 * @return boolean
	 */
	public final boolean getBoolean(K key){
		Object obj = get(key);
		if(obj == null)
			return false;
		if (obj instanceof String) {
			String str = (String) obj;
			if("true".equalsIgnoreCase(str))
				return true;
			return false;
		}
		return ((Boolean)obj).booleanValue();
	}

	/**
	 * Converts key for boolean type.
	 * @param key
	 * @param defaultValue
	 * @return boolean
	 */
	public final boolean getBoolean(K key, boolean defaultValue){
		Object obj = get(key);
		if(obj == null)
			return defaultValue;
		if (obj instanceof String) {
			String str = (String) obj;
			if("true".equalsIgnoreCase(str))
				return true;
			return false;
		}
		return ((Boolean)obj).booleanValue();
	}

	/**
	 * Converts key for BigDecimal type.
	 * @param key
	 * @return BigDecimal
	 */
	public final BigDecimal getBigDecimal(K key){
		Object obj = get(key);
		if (obj instanceof String) {
			String str = (String) obj;
			return new BigDecimal(str);
		}
		return (BigDecimal)obj;
	}

	/**
	 * Converts key for BigDecimal type.
	 * @param key
	 * @param defaultValue
	 * @return BigDecimal
	 */
	public final BigDecimal getBigDecimal(K key, BigDecimal defaultValue){
		Object obj = get(key);
		if(obj == null)
			return defaultValue;
		if (obj instanceof String) {
			String str = (String) obj;
			return new BigDecimal(str);
		}
		return (BigDecimal)obj;
	}

	/**
	 * Converts key for BigDecimal type.
	 * @param key
	 * @param defaultValue
	 * @return BigDecimal
	 */
	public final BigDecimal getBigDecimal(K key, String defaultValue){
		Object obj = get(key);
		if(obj == null)
			return new BigDecimal(defaultValue);
		if (obj instanceof String) {
			String str = (String) obj;
			return new BigDecimal(str);
		}
		return (BigDecimal)obj;
	}

	/**
	 * Converts key for BigDecimal type.
	 * @param key
	 * @param defaultValue
	 * @return BigDecimal
	 */
	public final BigDecimal getBigDecimal(K key, double defaultValue){
		Object obj = get(key);
		if(obj == null)
			return new BigDecimal(defaultValue);
		if (obj instanceof String) {
			String str = (String) obj;
			return new BigDecimal(str);
		}
		return (BigDecimal)obj;
	}

	/**
	 * Converts key for BigInteger type.
	 * @param key
	 * @return BigInteger
	 */
	public final BigInteger getBigInteger(K key){
		Object obj = get(key);
		if (obj instanceof String) {
			String str = (String) obj;
			return new BigInteger(str);
		}
		return (BigInteger)obj;
	}

	/**
	 * Converts key for BigInteger type.
	 * @param key
	 * @param defaultValue
	 * @return BigInteger
	 */
	public final BigInteger getBigInteger(K key, BigInteger defaultValue){
		Object obj = get(key);
		if(obj == null)
			return defaultValue;
		if (obj instanceof String) {
			String str = (String) obj;
			return new BigInteger(str);
		}
		return (BigInteger)obj;
	}

	/**
	 * Converts key for BigInteger type.
	 * @param key
	 * @param defaultValue
	 * @return BigInteger
	 */
	public final BigInteger getBigInteger(K key, String defaultValue){
		Object obj = get(key);
		if(obj == null)
			return new BigInteger(defaultValue);
		if (obj instanceof String) {
			String str = (String) obj;
			return new BigInteger(str);
		}
		return (BigInteger)obj;
	}
}
