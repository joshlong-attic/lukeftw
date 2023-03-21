package com.example.microservice;

import org.springframework.cloud.bus.event.Destination;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

public class ParadosRemoteApplicationEvent extends RemoteApplicationEvent {

    protected ParadosRemoteApplicationEvent(Object source, String originService, Destination destination) {
        super(source, originService, destination);
    }

    public String getMessage() {
        return (String) getSource();
    }
}
