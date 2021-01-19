package test;

import static org.junit.jupiter.api.Assertions.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.Position;

class NotificationTest {

	@Test
	void test() {
		Gson gson = new Gson();
		Position origin = new Position(37.573242, 55.801279);
		Position end = new Position(115.665017, 38.100717);
		
		Client client = ClientBuilder.newClient();
		Entity<String> payload = Entity.json("{\"locations\":"
				+ "[[" + origin.getLat() + "," + origin.getLon() + "],"
				+ "[" + end.getLat() + "," + end.getLon() + "]]}");
		System.out.println(payload);
		
		Response response = client.target("https://api.openrouteservice.org/v2/matrix/driving-car")
		  .request()
		  .header("Authorization", "5b3ce3597851110001cf6248cfa7da2f1b6742ce8054fc74984f37aa")
		  .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
		  .header("Content-Type", "application/json; charset=utf-8")
		  .post(payload);

		System.out.println("OSM status: " + response.getStatus());
		System.out.println("OSM headers: " + response.getHeaders());
		System.out.println("OSM body:" + response.readEntity(String.class));
		
		String stringResponse = response.readEntity(String.class);
		JsonObject obj = new JsonParser().parse(stringResponse).getAsJsonObject();
		double distance = obj.getAsJsonArray("durations")
				.get(1).getAsJsonArray()
				.get(0).getAsDouble();

		assertEquals(200, response.getStatus());
		assertTrue(obj.isJsonObject());
		assertEquals(316006.41, distance);
		
		
	}

}
