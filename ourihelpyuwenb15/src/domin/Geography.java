package domin;

public class Geography {
	private String name;
	private double longitude;
	private double latitude;
	private int id;
	public Geography(int id,String name, double longitude, double latitude) {
		super();
		this.id=id;
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	public Geography(){
		
	}
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
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	
	
}