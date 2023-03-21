package com.example.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.cloud.bus.event.PathDestinationFactory;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

@RemoteApplicationEventScan
@SpringBootApplication
public class MicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceApplication.class, args);
    }


}

@RestController
class EventBusController {

    private final AtomicInteger port = new AtomicInteger();

    private final ApplicationEventPublisher eventPublisher;

    EventBusController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    void portListener(WebServerInitializedEvent event) {
        System.out.println("the java process started on port " + event.getWebServer().getPort());
        this.port.set(event.getWebServer().getPort());
    }

    @GetMapping("/send")
    String send() {
        var event = new ParadosRemoteApplicationEvent("hello from " + this.port.get(),
               "microservice:" +  port.get() + "", new PathDestinationFactory().getDestination(null));

        this.eventPublisher.publishEvent(event);
        return "sent";
    }

    @EventListener
    void listenForParados(ParadosRemoteApplicationEvent event) {
        System.out.println("got a new event: " + event);
    }
}

@RestController
class InstancesController {

    private final DiscoveryClient discoveryClient;

    InstancesController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/services/{id}")
    Collection<String> instancesById(@PathVariable String id) {
        return this.discoveryClient.getInstances(id).stream()
                .map(si -> si.getHost() + ':' + si.getPort()).toList();
    }

    @GetMapping("/services")
    Collection<String> services() {
        return this.discoveryClient.getServices();
    }
}

