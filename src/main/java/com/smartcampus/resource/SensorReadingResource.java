package com.smartcampus.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            return errorResponse(404, "Not Found", "Sensor not found.");
        }
        List<SensorReading> readings = DataStore.getReadingsBySensorId()
                .computeIfAbsent(sensorId, key -> new ArrayList<>());
        return Response.ok(readings).build();
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            return errorResponse(404, "Not Found", "Sensor not found.");
        }
        if (reading == null) {
            return errorResponse(400, "Bad Request", "Request body is required.");
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor is in MAINTENANCE and cannot accept readings.");
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() <= 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        List<SensorReading> readings = DataStore.getReadingsBySensorId()
                .computeIfAbsent(sensorId, key -> new ArrayList<>());
        readings.add(reading);
        sensor.setCurrentValue(reading.getValue());

        return Response.status(201)
                .entity(Map.of("message", "Reading created.", "reading", reading))
                .build();
    }

    private Response errorResponse(int status, String error, String message) {
        return Response.status(status)
                .entity(Map.of("status", status, "error", error, "message", message))
                .build();
    }
}
