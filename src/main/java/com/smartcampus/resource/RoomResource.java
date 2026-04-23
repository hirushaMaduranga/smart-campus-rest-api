package com.smartcampus.resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    @GET
    public Collection<Room> getAllRooms() {
        return DataStore.getRooms().values();
    }

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null) {
            return errorResponse(400, "Bad Request", "Request body is required.");
        }
        if (isBlank(room.getId()) || isBlank(room.getName()) || room.getCapacity() <= 0) {
            return errorResponse(400, "Bad Request", "Room id, name, and positive capacity are required.");
        }
        if (DataStore.getRooms().containsKey(room.getId())) {
            return errorResponse(409, "Conflict", "Room with this id already exists.");
        }
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        DataStore.getRooms().put(room.getId(), room);
        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();

        return Response.created(location)
                .entity(Map.of("message", "Room created.", "room", room))
                .build();
    }

    @GET
    @Path("{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRooms().get(roomId);
        if (room == null) {
            return errorResponse(404, "Not Found", "Room not found.");
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRooms().get(roomId);
        if (room == null) {
            return errorResponse(404, "Not Found", "Room not found.");
        }
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room cannot be deleted because it still contains assigned sensors.");
        }
        DataStore.getRooms().remove(roomId);
        return Response.ok(Map.of("message", "Room deleted."))
                .build();
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
