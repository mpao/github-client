package io.github.mpao.githubclienttest;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import static java.time.temporal.ChronoUnit.SECONDS;

final public class TestUtils {

    private static final int TIMEOUT = 10;
    private TestUtils(){}

    static CompletableFuture<String> asyncHttpGetRequest(String url) throws URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.of(TIMEOUT, SECONDS))
                .GET()
                .build();
        return HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }

}
