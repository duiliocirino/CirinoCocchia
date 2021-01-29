package src.main.java.services.reservationManagement.implementation;

import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import src.main.java.model.Position;
import src.main.java.model.User;
import src.main.java.services.reservationManagement.interfaces.NotificationModule;

@Stateless
public class NotificationModuleImplementation extends NotificationModule{

	@Override
	public double rideTime(Position origin, Position end)
		throws ProcessingException{
		// https://openrouteservice.org/dev/#/api-docs/v2/matrix/{profile}/post
		// IMPORTANT!!!! This key api is Lorenzo Cocchia's property
		// Contact me at lorenzo.cocchia@mail.polimi.it		
		// 5b3ce3597851110001cf6248cfa7da2f1b6742ce8054fc74984f37aa&
		
		Client client = ClientBuilder.newClient();
		Entity<String> payload = Entity.json("{\"locations\":"
				+ "[[" + origin.getLat() + "," + origin.getLon() + "],"
				+ "[" + end.getLat() + "," + end.getLon() + "]]}");
		Response response = client.target("https://api.openrouteservice.org/v2/matrix/driving-car")
		  .request()
		  .header("Authorization", "5b3ce3597851110001cf6248cfa7da2f1b6742ce8054fc74984f37aa")
		  .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
		  .header("Content-Type", "application/json; charset=utf-8")
		  .post(payload);

		String stringResponse = response.readEntity(String.class);
		JsonObject obj = new JsonParser().parse(stringResponse).getAsJsonObject();
		double distance = obj.getAsJsonArray("durations")
				.get(1).getAsJsonArray()
				.get(0).getAsDouble();
		
		return distance;
	}

	@Override
	public void notify(List<User> users, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notify(User user, String message) {
		// TODO Auto-generated method stub
		
	}

}
