package com.fearsfx.libra.models;

import java.io.Serializable;

public class Manufacturer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6231245906367022464L;
	private int id;
	private String name;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
