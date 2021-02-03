package src.test.resources;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.Queue;
import src.main.java.model.Reservation;
import src.main.java.model.User;
import src.main.java.utils.ReservationStatus;
import src.main.java.utils.ReservationType;
import src.main.java.utils.Roles;

@Stateless
public class TestDataReservationToolbox {
	
	protected EntityManager em;
	
	// groceries and related
	public int IDGROCERY1;
	public final Position POS_GROCERY1 = new Position(0, 0);
	public final String NAMEGROCERY1 = "Grocery_1";
	public final int MAX_SPOTS_GROC1 = 20;
	public int IDQUEUE1;
	// customers
	public int IDCUSTOMER1;
	public final String TEL_NUM_CUST1 = "xxxxx";
	public final Roles ROLE_CUST1 = Roles.VISITOR;
	public int IDCUSTOMER2;
	public final String TEL_NUM_CUST2 = "yyyyyy";
	public final Roles ROLE_CUST2 = Roles.VISITOR;
	// reservation 1 data
	public int IDRESERVATION1;
	public final ReservationType TYPE1 = ReservationType.LINEUP;
	public final ReservationStatus STATUS1 = ReservationStatus.OPEN; 
	public final Date DATE1 = Calendar.getInstance().getTime();
	public Date DATE1_f;

	// reservation 2 data
	public int IDRESERVATION2;
	public final ReservationType TYPE2 = ReservationType.LINEUP;
	public final ReservationStatus STATUS2 = ReservationStatus.OPEN; 
	public final Date DATE2;
	public Date DATE2_f;
	
	public final Date START_INTERVAL;
	public final Date END_INTERVAL;	
	private int minuteDifference = 0;
	
	public final int IDRESERVATION_NOT_DB = 1000;
	
	public TestDataReservationToolbox(EntityManager em) {
		this.em = em;
		
		Calendar calDate1 = Calendar.getInstance();
		calDate1.setTime(DATE1);
		Calendar calDate2 = Calendar.getInstance();
		int y = calDate2.get(Calendar.YEAR);
		calDate2.set(Calendar.YEAR, y + 2);
		
		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();;
		int h = calStart.get(Calendar.HOUR);
		calStart.set(Calendar.HOUR, h - 1);
		calEnd.set(Calendar.HOUR, h + 1);
		
		DATE1_f = DATE1;
		DATE2 = calDate2.getTime();
		DATE2_f = DATE2;
		START_INTERVAL = calStart.getTime();
		END_INTERVAL = calEnd.getTime();
	}
	
	public void createTestData() {
		Grocery grocery1 = new Grocery();
		grocery1.setLatitude(POS_GROCERY1.getLat());
		grocery1.setLongitude(POS_GROCERY1.getLon());
		grocery1.setName(NAMEGROCERY1);
		grocery1.setMaxSpotsInside(MAX_SPOTS_GROC1);
		Queue queue1 = new Queue();
		queue1.setGrocery(grocery1);
		grocery1.setQueue(queue1);
		User customer1 = new User();
		customer1.setTelephoneNumber(TEL_NUM_CUST1);
		customer1.setRole(ROLE_CUST1);
		grocery1.setOwner(customer1);
		Reservation res1 = new Reservation();
		res1.setQueue(queue1);
		queue1.addReservation(res1);
		res1.setCustomer(customer1);
		res1.setEstimatedTime(DATE1);
		res1.setTimeEntrance(DATE1);
		res1.setTimeExit(DATE1_f);
		res1.setType(TYPE1.name());
		res1.setStatus(STATUS1);
		
		em.getTransaction().begin();
		
		em.persist(grocery1);
		em.persist(queue1);
		em.persist(customer1);
		em.persist(res1);		
		
		em.getTransaction().commit();
		
		IDGROCERY1 = grocery1.getIdgrocery();
		IDQUEUE1 = queue1.getIdqueue();
		IDCUSTOMER1 = customer1.getIduser();
		IDRESERVATION1 = res1.getIdreservation();
	}
	
	public void createAdditionalReservation() {
		User customer2 = new User();
		customer2.setTelephoneNumber(TEL_NUM_CUST2);
		customer2.setRole(ROLE_CUST2);
		Queue queue = em.find(Queue.class, IDQUEUE1);
		Reservation res1 = em.find(Reservation.class, IDRESERVATION1);;
		Reservation res2 = new Reservation();
		res2.setQueue(queue);
		queue.addReservation(res2);
		res2.setCustomer(customer2);
		res2.setEstimatedTime(DATE2);
		res2.setTimeEntrance(DATE2);
		res1.setTimeExit(DATE1_f);
		res2.setTimeExit(DATE2_f);
		res2.setType(TYPE2.name());
		res2.setStatus(STATUS2);
		
		em.getTransaction().begin();
		
		em.persist(customer2);
		em.persist(res2);
		
		em.getTransaction().commit();
		
		IDCUSTOMER2 = customer2.getIduser();
		IDRESERVATION2 = res2.getIdreservation();
	}
	
	public void createAdditionalReservation(int minDiff) {
		this.minuteDifference = minDiff;
		
		Calendar calDate1 = Calendar.getInstance();
		calDate1.setTime(DATE1);
		
		Calendar calDate2 = Calendar.getInstance();
		calDate2.setTime(DATE2);
		
		int min1 = calDate1.get(Calendar.MINUTE);
		int min2 = calDate2.get(Calendar.MINUTE);
		
		calDate1.set(Calendar.MINUTE, min1 + minuteDifference);
		calDate2.set(Calendar.MINUTE, min2 + minuteDifference);
		DATE1_f = calDate1.getTime();
		DATE2_f = calDate2.getTime();
		createAdditionalReservation();
	}
	
	public void removeAdditionalReservation() {
		User customer2 = em.find(User.class, IDCUSTOMER2);
		Reservation res2 = em.find(Reservation.class, IDRESERVATION2);
		
		em.getTransaction().begin();

		if(customer2 != null) {
			em.remove(customer2);
		}
		if(res2 != null) {
			em.remove(res2);
		}
		
		em.getTransaction().commit();
	}
	
	public void removeTestData() {
		em.getTransaction().begin();

		Grocery grocery1 = em.find(Grocery.class, IDGROCERY1);
		User customer1 = em.find(User.class, IDCUSTOMER1);
		Reservation res1 = em.find(Reservation.class, IDRESERVATION1);
		if(grocery1 != null) {
			em.remove(grocery1);
		}
		if(customer1 != null) {
			em.remove(customer1);
		}
		if(res1 != null) {
			em.remove(res1);
		}

		
		em.getTransaction().commit();
	}
}
