package com.smartcampus.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.smartcampus.exception.GlobalExceptionMapper;
import com.smartcampus.exception.LinkedResourceNotFoundExceptionMapper;
import com.smartcampus.exception.RoomNotEmptyExceptionMapper;
import com.smartcampus.exception.SensorUnavailableExceptionMapper;
import com.smartcampus.filter.LoggingFilter;
import com.smartcampus.resource.DiscoveryResource;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorResource;

@ApplicationPath("/api/v1")
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        register(JacksonFeature.class);

        register(DiscoveryResource.class);
        register(RoomResource.class);
        register(SensorResource.class);

        register(LoggingFilter.class);

        register(RoomNotEmptyExceptionMapper.class);
        register(LinkedResourceNotFoundExceptionMapper.class);
        register(SensorUnavailableExceptionMapper.class);
        register(GlobalExceptionMapper.class);
    }
}