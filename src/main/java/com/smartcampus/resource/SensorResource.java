package com.smartcampus.resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        if (type == null || type.trim().isEmpty()) {
            return new ArrayList<>(DataStore.getSensors().values());
        }
        String filter = type.trim();
        return DataStore.getSensors().values().stream()
                .filter(sensor -> sensor.getType() != null && sensor.getType().equalsIgnoreCase(filter))
                .collect(Collectors.toList());
    }

    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null) {
            return errorResponse(400, "Bad Request", "Request body is required.");
        }
        if (isBlank(sensor.getId()) || isBlank(sensor.getType()) || isBlank(sensor.getStatus()) || isBlank(sensor.getRoomId())) {
            return errorResponse(400, "Bad Request", "Sensor id, type, status, and roomId are required.");
        }
        if (DataStore.getSensors().containsKey(sensor.getId())) {
            return errorResponse(409, "Conflict", "Sensor with this id already exists.");
        }

        Room room = DataStore.getRooms().get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room not found for roomId: " + sensor.getRoomId());
        }

        DataStore.getSensors().put(sensor.getId(), sensor);
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }
        room.getSensorIds().add(sensor.getId());
        DataStore.getReadingsBySensorId().put(sensor.getId(), new ArrayList<>());

        URI location = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        return Response.created(location)
                .entity(Map.of("message", "Sensor created.", "sensor", sensor))
                .build();
    }

    @Path("{sensorId}/readings")
    public SensorReadingResource readings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }

    private Response errorResponse(int status, String error, String message) {
        return Response.status(status)
                .entity(Map.of("status", status, "error", error, "message", message))
                .build();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
