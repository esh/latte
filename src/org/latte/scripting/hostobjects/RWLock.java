package org.latte.scripting.hostobjects;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.mozilla.javascript.ScriptableObject;

public class RWLock extends ScriptableObject {
	private final Lock read, write;
	
	public RWLock() {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		read = lock.readLock();
		write = lock.writeLock();
	}
	
	public void jsFunction_readLock() {
		read.lock();
	}
	
	public void jsFunction_readUnlock() {
		read.unlock();
	}
	
	public void jsFunction_writeLock() {
		write.lock();
	}
	
	public void jsFunction_writeUnlock() {
		write.unlock();
	}

	@Override
	public String getClassName() {
		return "RWLock";
	}
}
