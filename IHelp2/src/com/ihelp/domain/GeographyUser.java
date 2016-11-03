package com.ihelp.domain;

public class GeographyUser {

	private int id;
	private int userId;
	private int geographyId;
	private double latitude;
	private double longitude;
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public GeographyUser(){
		
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
	public int getGeographyId() {
		return geographyId;
	}
	public void setGeographyId(int geographyId) {
		this.geographyId = geographyId;
	}
	
}
