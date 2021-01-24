package services.groceryManagement.implementation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.Grocery;
import services.groceryManagement.interfaces.MonitorModule;
import utils.GroceryData;

public class MonitorModuleImplementation extends MonitorModule {

	@Override
	public Map<GroceryData, Float> getGroceryStats(int idgrocery, Date date) {
		Grocery grocery = em.find(Grocery.class, idgrocery);
		Map<GroceryData, Float> statsMap = new HashMap<GroceryData, Float>();
		
		for(GroceryData data: GroceryData.values()) {
			float value = getGroceryStats(idgrocery, data);
			statsMap.put(data, value);
		}
		
		return statsMap;
	}

	@Override
	public float getGroceryStats(int idgrocery, GroceryData groceryData) {
		switch(groceryData) {
		}
		return 0;
	}

}
