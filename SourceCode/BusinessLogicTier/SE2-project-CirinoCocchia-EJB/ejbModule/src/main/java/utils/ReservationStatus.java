package src.main.java.utils;

/**
 * Reservation status can be OPEN, ALLOWED, ENTERED, CLOSED or NONE.  <br>
 * OPEN means that the reservation is valid and the user will have the allowance to get inside the store in some moment in the future.<br> 
 * ALLOWED means that the reservation is valid to be used at the grocery to get into the store.<br>
 * ENTERED means that the user successfully used the reservation to get into the store and is currently inside the store.<br>
 * CLOSED means that this reservation is no more valid.<br>
 * NONE is an emergency state that should not be included in a reservation lifecycle.<br>
 */
public enum ReservationStatus {

	OPEN("OPEN"), ALLOWED("ALLOWED"), ENTERED("ENTERED"), CLOSED("CLOSED"), NONE("NONE");
	
	/**
	 * Status of the reservation
	 */
	private String status;
	
	private ReservationStatus(String type) {
		this.status = type;
	}
	
	public String toString() {
		return this.status;
	}
	
	/**
	 * Serves as translator from a human-readable String to a ReservationStatus entity
	 * @param status to translate
	 * @return ReservationStatus instance transalted
	 */
	public static ReservationStatus getResStatusByString(String status) {
		if(status.equals("OPEN")) {
			return OPEN;
		} else if(status.equals("ALLOWED")) {
			return ALLOWED;
		}else if(status.equals("ENTERED")) {
			return ENTERED;
		} else if(status.equals("CLOSED")) {
			return CLOSED;
		} else {
			return NONE;
		}
	}
	
}