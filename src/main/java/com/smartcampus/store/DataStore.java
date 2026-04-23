package com.smartcampus.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

public final class DataStore {
    private static final Map<String, Room> ROOMS = new HashMap<>();
    private static final Map<String, Sensor> SENSORS = new HashMap<>();
    private static final Map<String, List<SensorReading>> READINGS_BY_SENSOR_ID = new HashMap<>();

    private DataStore() {
    }

    public static Map<String, Room> getRooms() {
        return ROOMS;
    }

    public static Map<String, Sensor> getSensors() {
        return SENSORS;
    }

    public static Map<String, List<SensorReading>> getReadingsBySensorId() {
        return READINGS_BY_SENSOR_ID;
    }

}
