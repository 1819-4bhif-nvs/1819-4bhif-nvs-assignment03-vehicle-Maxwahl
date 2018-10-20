package at.htl.vehiclest.rest;
import org.junit.Before;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class VehicleEndpointIT {
    private Client client;
    private WebTarget target;


    @Before
    public void initClient(){
        this.client= ClientBuilder.newClient();
        this.target=client.target("http://localhost:8080/vehicle/rs/vehicle/");
    }

    @Test
    public void fetchVehicle(){
        Response response = this.target.request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(),is(200));
        JsonArray payload = response.readEntity(JsonArray.class);
        System.out.println("payload = "+payload);
        JsonObject vehicle = payload.getJsonObject(0);
        assertThat(vehicle.getString("brand"),is("Opel 42"));
        assertThat(vehicle.getString("type"),is("Commodore"));

    }

    @Test
    public void crud(){
        Response response = this.target.request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(),is(200));
        JsonArray payload = response.readEntity(JsonArray.class);
        System.out.println("payload = "+payload);
        assertThat(payload,not(empty()));
        JsonObject vehicle = payload.getJsonObject(0);
        assertThat(vehicle.getString("brand"),is("Opel 42"));
        assertThat(vehicle.getString("type"),is("Commodore"));

        JsonObject dedicatedVehicle = this
                .target
                .path("43")
                .request(MediaType.APPLICATION_JSON)
                .get(JsonObject.class);
        assertThat(dedicatedVehicle.getString("brand"),containsString("43"));
        assertThat(dedicatedVehicle.getString("brand"),equalTo("Opel 43"));

        Response deleteResponse = this.target
                .path("42")
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertThat(deleteResponse.getStatus(),is(204));
    }
}
