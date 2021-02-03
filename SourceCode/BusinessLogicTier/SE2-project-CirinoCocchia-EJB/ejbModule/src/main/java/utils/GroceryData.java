package src.main.java.utils;

public enum GroceryData {
	NUM_WEEK_CUSTOMERS("Average customers per week"), NUM_MONTH_CUSTOMERS("Average customers per month"),
	AVG_TIME_WEEK("Average time per week"), AVG_TIME_MONTH("Average time per month");
	
	private final String value;
	
	GroceryData(String string) {
		value = string;
	}

	public static GroceryData getMissionStatusFromInt(String value) {
		switch (value) {
		case "Average customers per week":
			return GroceryData.NUM_WEEK_CUSTOMERS;
		case "Average customers per month":
			return GroceryData.NUM_MONTH_CUSTOMERS;
		case "Average time per week":
			return GroceryData.AVG_TIME_WEEK;
		case "Average time per month":
			return GroceryData.AVG_TIME_MONTH;
		}
		return null;
	}

	public String getValue() {
		return value;
	}
}