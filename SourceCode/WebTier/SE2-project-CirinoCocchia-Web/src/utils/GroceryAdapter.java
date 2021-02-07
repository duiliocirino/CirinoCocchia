package utils;

import src.main.java.model.Grocery;

public class GroceryAdapter {
	public String name;
	public Double latitude;
	public Double longitude;
	public Integer idgrocery;
	public Integer openingHour;
	public Integer closingHour;
	
	public GroceryAdapter(Grocery grocery) {
		this.name = grocery.getName();
		this.latitude = grocery.getLatitude();
		this.longitude = grocery.getLongitude();
		this.idgrocery = grocery.getIdgrocery();
		this.closingHour = grocery.getClosingHour();
		this.openingHour = grocery.getOpeningHour();
	}
}
