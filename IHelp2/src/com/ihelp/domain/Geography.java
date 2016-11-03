package com.ihelp.domain;

public class Geography {

	private String geographyName;
	private double latitude;
	private double longitude;
	private int id;
	public Geography(){
		
	}
	public String getGeographyName() {
		return geographyName;
	}
	public void setGeographyName(String geographyName) {
		this.geographyName = geographyName;
	}
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
