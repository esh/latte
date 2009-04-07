package org.latte.util;

public class Tuple<K, V> {
	private K key;
	private V value;
	
	public Tuple(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public K getKey() {
		return key;
	}
	
	public V getValue() {
		return value;
	}
}
