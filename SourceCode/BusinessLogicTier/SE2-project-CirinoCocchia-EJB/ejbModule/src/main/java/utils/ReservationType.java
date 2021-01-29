package src.main.java.utils;

public enum ReservationType {

	LINEUP("LINEUP"), NONE("NONE");
	
	private String type;
	
	private ReservationType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return this.type;
	}
	
	public static ReservationType getResTypeByString(String type) {
		if(type.equals("LINEUP")) {
			return LINEUP;
		} else {
			return NONE;
		}
	}
	
}
