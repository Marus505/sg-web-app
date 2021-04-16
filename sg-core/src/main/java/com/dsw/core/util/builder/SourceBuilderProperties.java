package com.dsw.core.util.builder;

import com.dsw.core.util.box.AbstractKeyBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class Name : SourceBuilderProperties.java
 * Description : SourceBuilderProperties class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public class SourceBuilderProperties<K extends Object, V extends Object> extends AbstractKeyBox<K, V> {

	private Map<K, V> properties = new HashMap<K, V>();

	/* (non-Javadoc)
	 * @see com.dsw.core.util.box.KeyBox#get(java.lang.Object)
	 */
	public V get(K key) {
		return properties.get(key);
	}

	/**
	 *
	 * @param key
	 * @param value
	 */
	public void set(K key, V value){
		this.properties.put(key, value);
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.box.KeyBox#containsKey(java.lang.Object)
	 */
	public boolean containsKey(K key) {
		return properties.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see com.dsw.core.util.box.KeyBox#keySet()
	 */
	public Set<K> keySet() {
		return properties.keySet();
	}

}
