
package com.neewrobert.helidonse;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

import io.helidon.media.jackson.JacksonSupport;
import io.helidon.common.http.Http;
import io.helidon.webclient.WebClient;
import io.helidon.webclient.WebClientResponse;
import io.helidon.webserver.WebServer;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class MainTest {


    private static WebServer webServer;
    private static WebClient webClient;

    @BeforeAll
    static void startTheServer() {
        webServer = Main.startServer().await(Duration.ofSeconds(10));

        webClient = WebClient.builder()
                .baseUri("http://localhost:" + webServer.port())
                .addMediaSupport(JacksonSupport.create())
                .build();
    }

    @AfterAll
    static void stopServer() {
        if (webServer != null) {
            webServer.shutdown().await(10, TimeUnit.SECONDS);
        }
    }


    @Test
    void testMicrometerMetrics() {
        String get = webClient.get()
                .path("/micrometer-greet/greet-count")
                .request(String.class)
                .await(Duration.ofSeconds(5));

        assertThat(get, containsString("Hello World!"));

        String openMetricsOutput = webClient.get()
                .path("/micrometer")
                .request(String.class)
                .await(Duration.ofSeconds(5));

        assertThat("Metrics output", openMetricsOutput, containsString("allRequests_total 1"));
    }

    @Test
    void testMetrics() {
        WebClientResponse response = webClient.get()
                .path("/metrics")
                .request()
                .await(Duration.ofSeconds(5));
        assertThat(response.status().code(), is(200));
    }
    @Test
    void testSimpleGreet() {
        Message json = webClient.get()
                .path("/simple-greet")
                .request(Message.class)
                .await(Duration.ofSeconds(5));
        assertThat(json.getMessage(), is("Hello World!"));
    }
}
