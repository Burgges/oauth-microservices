package org.baeldung.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ResourceServerLiveTest {

    @Test
    public void givenAccessToken_whenConsumingFoos_thenOK() {
        final String accessToken = obtainAccessTokenViaPasswordGrant("john", "123");

        final Response resourceServerResponse = RestAssured.given().header("Authorization", "Bearer " + accessToken).get("http://localhost:8082/oauth-resource-server/foos/100");
        assertThat(resourceServerResponse.getStatusCode(), equalTo(200));
    }

    @Test
    public void givenUserWithNoAdminAccess_whenConsumingAdminOperation_thenForbidden() {
        final String accessToken = obtainAccessTokenViaPasswordGrant("john", "123");

        final Response resourceServerResponse = RestAssured.given().header("Authorization", "Bearer " + accessToken).get("http://localhost:8082/oauth-resource-server/bars/100");
        assertThat(resourceServerResponse.getStatusCode(), equalTo(403));
    }

    @Test
    public void givenUserWithAdminAccess_whenConsumingAdminOperation_thenOK() {
        final String accessToken = obtainAccessTokenViaPasswordGrant("tom", "111");

        final Response resourceServerResponse = RestAssured.given().header("Authorization", "Bearer " + accessToken).get("http://localhost:8082/oauth-resource-server/bars/100");
        assertThat(resourceServerResponse.getStatusCode(), equalTo(200));
    }

    //

    private String obtainAccessTokenViaPasswordGrant(final String username, final String password) {
        final Response authServerResponse = obtainAccessTokenViaPasswordGrantRaw("fooClientIdPassword", "secret", username, password);
        final String accessToken = authServerResponse.jsonPath().getString("access_token");
        return accessToken;
    }

    private Response obtainAccessTokenViaPasswordGrantRaw(final String clientId, final String clientSecret, final String username, final String password) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "password");
        params.put("client_id", clientId);
        params.put("username", username);
        params.put("password", password);
        return RestAssured.given().auth().preemptive().basic(clientId, clientSecret).and().with().params(params).when().post("http://localhost:8081/oauth-authorization-server/oauth/token");
        // response.jsonPath().getString("refresh_token");
        // response.jsonPath().getString("access_token")
    }

}