package org.latte.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is NOT threadsafe.
 * @author esh
 *
 * @param <K>
 * @param <V>
 */
public class LRUCache<K,V> {
	private static final float	LOADFACTOR = 0.75f;

	private LinkedHashMap<K, V> map;
	private int cacheSize;

	public LRUCache(int cacheSize) {
	   this.cacheSize = cacheSize;
	   int hashTableCapacity = (int)Math.ceil(cacheSize / LOADFACTOR) + 1;
	   map = new LinkedHashMap<K, V>(hashTableCapacity, LOADFACTOR, true) {
		   private static final long serialVersionUID = 1505773448412852025L;
		   
		   @Override
		   protected boolean removeEldestEntry (Map.Entry<K, V> eldest) {
			   return size() > LRUCache.this.cacheSize;
		   }
		};
	 }

	public V get (K key) {
		V o = map.get(key);
		
		return o;
	}

	public void put (K key, V value) {
		map.put (key,value);
	}

	public void remove(K key) {
		map.remove(key);
	}
	
	public void clear() {
		map.clear();
	}

	public int usedEntries() {
	   return map.size();
	}
}