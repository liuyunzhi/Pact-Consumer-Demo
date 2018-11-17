package com.demo.pact.consumer.pactTests;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "articles-provider", port = "8080")
public class ArticlesTest {

    @Pact(provider = "articles-provider", consumer = "articles-consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder
                .given("pact for articles")
                .uponReceiving("contract for consumer getting users")
                .path("/users")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(new PactDslJsonBody()
                        .minArrayLike("users", 1)
                        .stringType("name", "name")
                        .stringType("password", "password")
                        .closeArray()
                )
                .toPact();
    }

    @Test
    void test(MockServer mockServer) throws IOException {
        HttpResponse httpResponse = Request.Get(mockServer.getUrl() + "/users").execute().returnResponse();

        assertThat(httpResponse.getStatusLine().getStatusCode(), is(equalTo(200)));
    }
}
