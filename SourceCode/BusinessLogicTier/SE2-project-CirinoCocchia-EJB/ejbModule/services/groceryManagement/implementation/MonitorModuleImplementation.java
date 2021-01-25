package services.groceryManagement.implementation;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Grocery;
import model.Queue;
import services.groceryManagement.interfaces.MonitorModule;
import utils.GroceryData;

public class MonitorModuleImplementation extends MonitorModule {
	
	private final int DAYS_IN_A_MONTH = 30;
	private final int DAYS_IN_A_WEEK = 7;

	@Override
	public Map<GroceryData, Float> getGroceryStats(int idgrocery, Date date) {
		Map<GroceryData, Float> statsMap = new HashMap<GroceryData, Float>();
		
		for(GroceryData data: GroceryData.values()) {
			float value = getGroceryStats(idgrocery, data, date);
			statsMap.put(data, value);
		}
		
		return statsMap;
	}

	@Override
	public float getGroceryStats(int idgrocery, GroceryData groceryData, Date date) {
		
		Grocery grocery = em.find(Grocery.class, idgrocery);
		if(grocery == null) {
			return -1;
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
		
		
		if(groceryData == GroceryData.AVG_MONTH_CUSTOMERS || 
				groceryData == GroceryData.AVG_TIME_MONTH) {
			
			oneMonthAgo.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - DAYS_IN_A_MONTH);
			
			if(date == null || oneMonthAgo.before(calDate)) {
				calDate.set(Calendar.DAY_OF_YEAR, oneMonthAgo.get(Calendar.DAY_OF_YEAR));
			} else {
				calEnd.set(Calendar.DAY_OF_YEAR, calDate.get(Calendar.DAY_OF_YEAR) + DAYS_IN_A_MONTH);
			}
		}
		
		if(groceryData == GroceryData.AVG_WEEK_CUSTOMERS || 
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
		
		List<Integer> customersQuery = em.createNamedQuery("Reservation.TotalVisitsInInterval", Integer.class)
				.setParameter("queue", queue)
				.setParameter("start", startTime)
				.setParameter("end", endTime)
				.getResultList();
		
		if(customersQuery.isEmpty()) {
			return -1;
		} 
		
		int numCustomers = customersQuery.get(0);
			
		switch(groceryData) {
		case AVG_MONTH_CUSTOMERS:
		case AVG_WEEK_CUSTOMERS:
			return numCustomers;
			
		case AVG_TIME_MONTH:
		case AVG_TIME_WEEK:
			List<Integer> visitDurations = em.createNamedQuery("Reservation.TotalTimeSpentInInterval", Integer.class)
					.setParameter("queue", queue)
					.setParameter("start", startTime)
					.setParameter("end", endTime)
					.getResultList();
			
			return getSumOfList(visitDurations) / numCustomers;
		}
		
		return -1;
	}
	
	private int getSumOfList(List<Integer> list) {
		int sum = 0;
		for(int item : list) {
			sum += item;
		}
		
		return sum;
	}

}
