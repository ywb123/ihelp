package domin;

public class UserInfoGeography {

	
	private int userID;
	private int geographyID;
	private double latitude;
	private double longitude;
	public UserInfoGeography(){
		
	}
	public UserInfoGeography(int userID,int geographyID){
		this.userID = userID;
		this.geographyID = geographyID;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public int getGeographyID() {
		return geographyID;
	}
	public void setGeographyID(int geographyID) {
		this.geographyID = geographyID;
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
	
}