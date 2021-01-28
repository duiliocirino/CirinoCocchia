package services.groceryManagement.implementation;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.CLupException;
import model.Grocery;
import model.Queue;
import services.groceryManagement.interfaces.MonitorModule;
import utils.GroceryData;

public class MonitorModuleImplementation extends MonitorModule {
	
	private final int DAYS_IN_A_MONTH = 30;
	private final int DAYS_IN_A_WEEK = 7;

	@Override
	public Map<GroceryData, Float> getGroceryStats(int idgrocery, Date date) throws CLupException {
		Map<GroceryData, Float> statsMap = new HashMap<GroceryData, Float>();
		
		for(GroceryData data: GroceryData.values()) {
			float value = getGroceryStats(idgrocery, data, date);
			statsMap.put(data, value);
		}
		
		return statsMap;
	}

	@Override
	public float getGroceryStats(int idgrocery, GroceryData groceryData, Date date) throws CLupException {
		
		Grocery grocery = findGrocery(idgrocery);
		if(grocery == null) {
			throw new CLupException("Can't find the grocery");
		}
		if(groceryData == null) {
			throw new CLupException("Type of statistics request not specified");
		}
		
		Queue queue = grocery.getQueue();
		
		Date startTime;
		Date endTime;
		Calendar calDate = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		Calendar oneMonthAgo = Calendar.getInstance();
		Calendar oneWeekAgo = Calendar.getInstance();
		
		if(date != null) {
			calDate.setTime(date);
		}
		// else, CalDate remains that of the moment in which
		// the method were invoked
		
		
		if(groceryData == GroceryData.NUM_MONTH_CUSTOMERS || 
				groceryData == GroceryData.AVG_TIME_MONTH) {
			
			oneMonthAgo.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - DAYS_IN_A_MONTH);
			
			if(date == null || oneMonthAgo.before(calDate)) {
				calDate.set(Calendar.DAY_OF_YEAR, oneMonthAgo.get(Calendar.DAY_OF_YEAR));
			} else {
				calEnd.set(Calendar.DAY_OF_YEAR, calDate.get(Calendar.DAY_OF_YEAR) + DAYS_IN_A_MONTH);
			}
		}
		
		if(groceryData == GroceryData.NUM_WEEK_CUSTOMERS || 
				groceryData == GroceryData.AVG_TIME_WEEK) {
			
			oneWeekAgo.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - DAYS_IN_A_WEEK);
			
			if(date == null || oneWeekAgo.before(calDate)) {
				calDate.set(Calendar.DAY_OF_YEAR, oneWeekAgo.get(Calendar.DAY_OF_YEAR));
			} else {
				calEnd.set(Calendar.DAY_OF_YEAR, calDate.get(Calendar.DAY_OF_YEAR) + DAYS_IN_A_WEEK);
			}
		}
		
		startTime = calDate.getTime();
		endTime = calEnd.getTime();
		
		List<Integer> customersQuery = namedQueryReservationTotalVisitsInInterval(queue, startTime, endTime);
		
		if(customersQuery.isEmpty()) {
			return -1;
		} 
		
		int numCustomers = customersQuery.get(0);
			
		switch(groceryData) {
		case NUM_MONTH_CUSTOMERS:
		case NUM_WEEK_CUSTOMERS:
			return numCustomers;
			
		case AVG_TIME_MONTH:
		case AVG_TIME_WEEK:
			List<Integer> visitDurations = namedQueryReservationTotalTimeSpentInInterval(queue, startTime, endTime);			
			return getSumOfList(visitDurations) / numCustomers;
		}
		
		return -1;
	}
	
	protected Grocery findGrocery(int idgrocery) {
		return em.find(Grocery.class, idgrocery);
	}
	
	protected List<Integer> namedQueryReservationTotalVisitsInInterval(Queue queue, Date start, Date end){
		return em.createNamedQuery("Reservation.TotalVisitsInInterval", Integer.class)
				.setParameter("queue", queue)
				.setParameter("start", start)
				.setParameter("end", end)
				.getResultList();
	}
	
	protected List<Integer> namedQueryReservationTotalTimeSpentInInterval(Queue queue, Date start, Date end){
		return em.createNamedQuery("Reservation.TotalTimeSpentInInterval", Integer.class)
				.setParameter("queue", queue)
				.setParameter("start", start)
				.setParameter("end", end)
				.getResultList();
	}
	
	private int getSumOfList(List<Integer> list) {
		int sum = 0;
		for(int item : list) {
			sum += item;
		}
		
		return sum;
	}

}
