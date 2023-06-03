package com.neewrobert.helidonse;

import java.util.logging.Logger;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

/**
 * A simple service to greet you. Examples:
 *
 * Get default greeting message:
 * curl -X GET http://localhost:8080/simple-greet
 *
 * The message is returned as a JSON object
 */
public class SimpleGreetService implements Service {

    private static final Logger LOGGER = Logger.getLogger(SimpleGreetService.class.getName());

    private Counter requestCounter;

    private final String greeting;

    SimpleGreetService(Config config) {
        greeting = config.get("app.greeting").asString().orElse("Ciao");
    }


    SimpleGreetService(Config config, MeterRegistry registry) {
        this(config);
        this.requestCounter = registry.counter("allRequests");
    }

    /**
     * A service registers itself by updating the routing rules.
     *
     * @param rules the routing rules.
     */
    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", this::getDefaultMessageHandler);
        rules.get("/greet-count", this::countAccess, this::getDefaultMessageHandler);
    }

    /**
     * Return a worldly greeting message.
     *
     * @param request the server request
     * @param response the server response
     */
    private void getDefaultMessageHandler(ServerRequest request, ServerResponse response) {
        String msg = String.format("%s %s!", greeting, "World");
        LOGGER.info("Greeting message is " + msg);

        Message message = new Message();
        message.setMessage(msg);

        response.send(message);
    }


    private void countAccess(ServerRequest request, ServerResponse response) {
        requestCounter.increment();
        request.next();
    }
}
