package src.main.java.services.groceryManagement.interfaces;

import java.util.Date;
import java.util.Map;

import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.services.groceryManagement.implementation.MonitorModuleImplementation;
import src.main.java.services.macrocomponents.GroceryManagement;
import src.main.java.utils.GroceryData;

/**
 * This module can be used from all users, it lets the store 
 * workers take a look on the grocery’s queue and bookings and the 
 * customers to see the situation about the queue 
 */
@Stateless
public abstract class MonitorModule extends GroceryManagement {
	/**
	 * This method retrieves all the statistics for a certain grocery defined in the 
	 * DD.
	 * @param idgrocery id of the grocery for which retrieve the statistics
	 * @param date date from which calculate the statistics. If the period of time is 
	 * not sufficient to calculate statistics, then the minimum period of time is taken.
	 * Even null can be passed: in this case for each field the minimum period of time 
	 * is taken.
	 * @return all possible GroceryData mapped with its relative number:
	 * AVG_WEEK_CUSTOMERS: average number of customers per day in the week 
	 * starting from the date passed as a parameter <br>
	 * AVG_MONTH_CUSTOMERS: average number of customers per day in the month 
	 * starting from the date passed as a parameter <br>
	 * AVG_TIME_WEEK: average time spent by the customer inside the store
	 * in the week starting from the date passed as an argument <br>
	 * AVG_TIME_MONTH: average time spent by the customer inside the store
	 * in the month starting from the date passed as an argument <br>
	 * @throws CLupException if the grocery can not be found on the DB
	 */
	public abstract Map<GroceryData, Float> getGroceryStats(int idgrocery, Date date) throws CLupException;
	/**
	 * Retrieves the statistics for a certain GroceryData
	 * @param idgrocery id of the grocery for which retrieve the statistics
	 * @param groceryData GroceryData instance for which retrieve the data
	 * @param date date from which calculate the statistics. If the period of time is 
	 * not sufficient to calculate statistics, then the minimum period of time is taken.
	 * Even null can be passed: in this case for each field the minimum period of time 
	 * is taken. 
	 * @return statistic relative to the groceryData passed as an argument for 
	 * the grocery store specified. -1 if data is not sufficient to do the computation
	 *  (for example, there are no visits for the selected grocery)
	 * @throws CLupException if the idgrocery is not relative to a grocery in the db or groceryData
	 *  is not specified
	 */
	public abstract float getGroceryStats (int idgrocery, GroceryData groceryData, Date date) throws CLupException;
	
	public static MonitorModule getInstance() {
		return new MonitorModuleImplementation();
	}
}
