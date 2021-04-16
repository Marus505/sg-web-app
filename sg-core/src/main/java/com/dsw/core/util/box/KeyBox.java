package com.dsw.core.util.box;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

/**
 * Class Name : KeyBox.java
 * Description : KeyBox class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public interface KeyBox<K, V> {

	/**
	 *
	 * @param key
	 * @return boolean
	 */
	public boolean containsKey(K key);

	/**
	 *
	 * @return Set<K>
	 */
	public Set<K> keySet();

	/**
	 *
	 * @param key
	 * @return V
	 */
	public V get(K key);

	/**
	 *
	 * @param key
	 * @return Date
	 */
	public Date getDate(K key);

	/**
	 *
	 * @param key
	 * @param defaultCurrentDate
	 * @return Date
	 */
	public Date getDate(K key, boolean defaultCurrentDate);

	/**
	 *
	 * @param key
	 * @return String
	 */
	public String getString(K key);

	/**
	 *
	 * @param key
	 * @param defaultValue
	 * @return String
	 */
	public String getString(K key, String defaultValue);

	/**
	 *
	 * @param key
	 * @return short
	 */
	public short getShort(K key);

	/**
	 *
	 * @param key
	 * @param defaultValue
	 * @return short
	 */
	public short getShort(K key, short defaultValue);

	/**
	 *
	 * @param key
	 * @return int
	 */
	public int getInt(K key);

	/**
	 *
	 * @param key
	 * @param defaultValue
	 * @return int
	 */
	public int getInt(K key, int defaultValue);

	/**
	 *
	 * @param key
	 * @return long
	 */
	public long getLong(K key);

	/**
	 *
	 * @param key
	 * @param defaultValue
	 * @return long
	 */
	public long getLong(K key, long defaultValue);

	/**
	 *
	 * @param key
	 * @return double
	 */
	public double getDouble(K key);

	/**
	 *
	 * @param key
	 * @param defaultValue
	 * @return double
	 */
	public double getDouble(K key, double defaultValue);

	/**
	 *
	 * @param key
	 * @return float
	 */
	public float getFloat(K key);

	/**
	 *
	 * @param key
	 * @param defaultValue
	 * @return float
	 */
	public float getFloat(K key, float defaultValue);

	/**
	 *
	 * @param key
	 * @return boolean
	 */
	public boolean getBoolean(K key);

	/**
	 *
	 * @param key
	 * @param defaultValue
	 * @return boolean
	 */
	public boolean getBoolean(K key, boolean defaultValue);

	/**
	 *
	 * @param key
	 * @return BigDecimal
	 */
	public BigDecimal getBigDecimal(K key);

	/**
	 *
	 * @param key
	 * @param defaultValue
	 * @return BigDecimal
	 */
	public BigDecimal getBigDecimal(K key, BigDecimal defaultValue);

	/**
	 *
	 * @param key
	 * @param defaultValue
	 * @return BigDecimal
	 */
	public BigDecimal getBigDecimal(K key, String defaultValue);

	/**
	 *
	 * @param key
	 * @param defaultValue
	 * @return BigDecimal
	 */
	public BigDecimal getBigDecimal(K key, double defaultValue);

	/**
	 *
	 * @param key
	 * @return BigInteger
	 */
	public BigInteger getBigInteger(K key);

	/**
	 *
	 * @param key
	 * @param defaultValue
	 * @return BigInteger
	 */
	public BigInteger getBigInteger(K key, BigInteger defaultValue);

	/**
	 *
	 * @param key
	 * @param defaultValue
	 * @return BigInteger
	 */
	public BigInteger getBigInteger(K key, String defaultValue);

}
