package src.test.resources;

import static src.test.resources.TestDataGroceryToolbox.POS_GROCERY_NOT_DB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.Queue;
import src.main.java.model.Reservation;
import src.main.java.model.User;
import src.main.java.utils.ReservationStatus;
import src.main.java.utils.ReservationType;
import src.main.java.utils.Roles;

@Stateless
public class TestDataGroceryToolbox {
	
	protected EntityManager em;
		
	public static int IDGROCERY1;
	public static final String NAME_GROCERY1 = "grocery-1";
	public static final Position POS_GROCERY1 = new Position(0, 0);
	public static int IDGROCERY2;
	public static final String NAME_GROCERY2 = "grocery-2";
	public static final Position POS_GROCERY2 = new Position(10, 10);
	public static int IDGROCERY_NOT_DB = 3;
	public static final String NAME_GROCERY_NOT_DB = "not-db";
	public static final Position POS_GROCERY_NOT_DB = new Position(20, 20);
	
	public static int IDOWNER;
	public static final Roles OWNER_ROLE = Roles.MANAGER;
	public static final String TELEPHONE_NUM = "000000000";
	public static final int IDOWNER_NOT_DB = 2;
	
	// favourite grocery data --------------- 
	// new grocery
	public static int ID_GROCERY3;
	private final String NAME_GROCERY3 = "grocery-3";
	private final Position POS_GROCERY3 = new Position(30, 30);
	// reservation skeleton
	private final int RESERVATION_NUM = 6;
	private int[] RES_IDS = new int[RESERVATION_NUM];
	private final ReservationType TYPE = ReservationType.LINEUP;
	private final ReservationStatus STATUS = ReservationStatus.CLOSED; 
	// customer
	public static int ID_CUSTOMER;
	private final String TELEPHONE_NUM_CUSOMER = "11111111";
	private final Roles ROLE_CUSTOMER = Roles.REG_CUSTOMER;	
	// end of favourite grocery data ------------
	
	public TestDataGroceryToolbox(EntityManager em) {
		this.em = em;
	}
	
	public void createTestData() {
		
		User owner = new User();
		owner.setTelephoneNumber(TELEPHONE_NUM);
		owner.setRole(OWNER_ROLE);
		
		Grocery grocery1 = new Grocery();
		Queue queue1 = new Queue();
		grocery1.setName(NAME_GROCERY1);
		grocery1.setOwner(owner);
		grocery1.setQueue(queue1);
		grocery1.setLatitude(POS_GROCERY1.getLat());
		grocery1.setLongitude(POS_GROCERY1.getLon());
		queue1.setGrocery(grocery1);
		Grocery grocery2 = new Grocery();
		Queue queue2 = new Queue();
		grocery2.setName(NAME_GROCERY2);
		grocery2.setOwner(owner);
		grocery2.setQueue(queue2);
		grocery2.setLatitude(POS_GROCERY2.getLat());
		grocery2.setLongitude(POS_GROCERY2.getLon());
		queue2.setGrocery(grocery2);
		
		em.getTransaction().begin();
		
		em.persist(owner);
		em.persist(grocery1);
		em.persist(grocery2);
		
		em.getTransaction().commit();
		
		IDOWNER = owner.getIduser();
		IDGROCERY1 = grocery1.getIdgrocery();
		IDGROCERY2 = grocery2.getIdgrocery();
	}
	
	public void removeTestData() {
				
		em.getTransaction().begin();
		
		User owner = em.find(User.class, IDOWNER);
		if(owner != null) {
			em.remove(owner);
		}
		
		Grocery grocery1 = em.find(Grocery.class, IDGROCERY1);
		if(grocery1 != null) {
			em.remove(grocery1);
		}
		
		Grocery grocery2 = em.find(Grocery.class, IDGROCERY2);
		if(grocery2 != null) {
			em.remove(grocery2);
		}
		
		em.getTransaction().commit();
	}

	/**
	 * Created 6 reservations to three different groceries in order to have 
	 * a ranking of reservations grocery1-1°, grocer2-2°, grocery3-3°
	 */
	public void createFavouriteGroceriesData() {
		if(RESERVATION_NUM != 6) {
			return;
		}
		
		User customer = new User();
		customer.setTelephoneNumber(TELEPHONE_NUM_CUSOMER);
		customer.setRole(ROLE_CUSTOMER);
		
		Grocery grocery1 = em.find(Grocery.class, IDGROCERY1);
		Grocery grocery2 = em.find(Grocery.class, IDGROCERY2);
		Grocery grocery3 = new Grocery();
		Queue queue = new Queue();
		User owner = em.find(User.class, IDOWNER);
		grocery3.setOwner(owner);
		grocery3.setQueue(queue);
		grocery3.setName(NAME_GROCERY3);
		grocery3.setLatitude(POS_GROCERY3.getLat());
		grocery3.setLongitude(POS_GROCERY3.getLon());
		queue.setGrocery(grocery3);
		
		em.getTransaction().begin();
		
		em.persist(customer);
		em.persist(grocery3);
				
		Reservation res1 = new Reservation();
		res1.setQueue(grocery1.getQueue());
		res1.setCustomer(customer);
		res1.setType(TYPE.name());
		res1.setStatus(STATUS);
		em.persist(res1);
		
		Reservation res2 = new Reservation();
		res2.setQueue(grocery1.getQueue());
		res2.setCustomer(customer);
		res2.setType(TYPE.name());
		res2.setStatus(STATUS);
		em.persist(res2);
		
		Reservation res3 = new Reservation();
		res3.setQueue(grocery1.getQueue());
		res3.setCustomer(customer);
		res3.setType(TYPE.name());
		res3.setStatus(STATUS);
		em.persist(res3);
		
		Reservation res4 = new Reservation();
		res4.setQueue(grocery2.getQueue());
		res4.setCustomer(customer);
		res4.setType(TYPE.name());
		res4.setStatus(STATUS);
		em.persist(res4);
		
		Reservation res5 = new Reservation();
		res5.setQueue(grocery2.getQueue());
		res5.setCustomer(customer);
		res5.setType(TYPE.name());
		res5.setStatus(STATUS);
		em.persist(res5);
		
		Reservation res6 = new Reservation();
		res6.setQueue(grocery3.getQueue());
		res6.setCustomer(customer);
		res6.setType(TYPE.name());
		res6.setStatus(STATUS);
		em.persist(res6);
		
		em.getTransaction().commit();
		
		ID_GROCERY3 = grocery3.getIdgrocery();
		ID_CUSTOMER = customer.getIduser();
		RES_IDS[0] = res1.getIdreservation();
		RES_IDS[1] = res2.getIdreservation();
		RES_IDS[2] = res3.getIdreservation();
		RES_IDS[3] = res4.getIdreservation();
		RES_IDS[4] = res5.getIdreservation();
		RES_IDS[5] = res6.getIdreservation();
	}

	public void removeFavouriteGroceriesData() {
		Grocery grocery3 = em.find(Grocery.class, ID_GROCERY3);
		User customer = em.find(User.class, ID_CUSTOMER);
		Reservation res1 = em.find(Reservation.class, RES_IDS[0]);
		Reservation res2 = em.find(Reservation.class, RES_IDS[1]);
		Reservation res3 = em.find(Reservation.class, RES_IDS[2]);
		Reservation res4 = em.find(Reservation.class, RES_IDS[3]);
		Reservation res5 = em.find(Reservation.class, RES_IDS[4]);
		Reservation res6 = em.find(Reservation.class, RES_IDS[5]);
		
		em.getTransaction().begin();
		
		if(grocery3 != null) {
			em.remove(grocery3);
		}
		
		if(customer != null) {
			em.remove(customer);
		}
		
		if(res1 != null) {
			em.remove(res1);
		}	
		if(res2 != null) {
			em.remove(res2);
		}
		if(res3 != null) {
			em.remove(res3);
		}
		if(res4 != null) {
			em.remove(res4);
		}
		if(res5 != null) {
			em.remove(res5);
		}
		if(res6 != null) {
			em.remove(res6);
		}
		
		em.getTransaction().commit();
	}
}
