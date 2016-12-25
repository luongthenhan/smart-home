package com.hcmut.smarthome.utils;

import java.util.Objects;

public class Pair<U,V> {
	private U first;
	private V second;
	
	public Pair(U first, V second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	public U getFirst() {
		return first;
	}
	public void setFirst(U first) {
		this.first = first;
	}
	public V getSecond() {
		return second;
	}
	public void setSecond(V second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return String.format("[%s, %s]", this.getFirst().toString(), this.getSecond().toString());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if( obj == null || !(obj instanceof Pair))
			return false;
		if(obj == this)
			return true;
		Pair that = (Pair)obj;
		return Objects.equals(this.first, that.first) && Objects.equals(this.second, that.second);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first,this.second);
	}
}
