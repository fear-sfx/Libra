package com.fearsfx.libra.models;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9082468481565487570L;
	private int id;
	private int userId;
	private Date date;
	private String note;
	private String status;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
