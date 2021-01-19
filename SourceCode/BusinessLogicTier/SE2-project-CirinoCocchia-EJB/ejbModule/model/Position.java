package model;

/**
 * This class represents a position into the system
 *
 */
public class Position {
	/**
	 * Represents the latitude of the position
	 */
	private double lat;
	/**
	 * Represents the longitude of the position
	 */
	private double lon;
	
	/**
	 * Creates a new instance of the Position class
	 * @param lat latitude of the position
	 * @param lon longitude of the position
	 */
	public Position(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}
	
	
}
