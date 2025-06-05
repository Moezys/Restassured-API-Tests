package com.mycompany.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ApiTests {

    static String jdbcURL = "URL";
    static String dbUser = "your_db_user";
    static String dbPass = "your_db_password";

    @BeforeAll
    public static void setupRestAssured() {
        // (No base URI for JSONPlaceholder since we use full URL in each test)
        RestAssured.useRelaxedHTTPSValidation(); 
    }

    // This method reads test rows from MySQL and returns a Stream of TestCase objects
    static Stream<TestCase> testData() throws SQLException {
        List<TestCase> list = new ArrayList<>();
        Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPass);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM api_tests");
        while (rs.next()) {
            String method = rs.getString("method");
            String endpoint = rs.getString("endpoint");
            String payload = rs.getString("payload");
            int expectedStatus = rs.getInt("expected_status");
            String jsonPath = rs.getString("expected_json_path");
            String expectedVal = rs.getString("expected_value");
            list.add(new TestCase(method, endpoint, payload, expectedStatus, jsonPath, expectedVal));
        }
        rs.close();
        conn.close();
        return list.stream();
    }

    @ParameterizedTest
    @MethodSource("testData")
    public void runApiTest(TestCase tc) {
        Response resp;
        if (tc.method.equalsIgnoreCase("GET")) {
            resp = given().when().get(tc.endpoint).andReturn();
        } else {
            resp = given()
                      .header("Content-type", "application/json")
                      .body(tc.payload)
                   .when().post(tc.endpoint)
                   .andReturn();
        }
        assertThat(resp.getStatusCode(), equalTo(tc.expectedStatus));

        if (tc.expectedJsonPath != null) {
            String actualVal = resp.jsonPath().getString(tc.expectedJsonPath);
            assertThat(actualVal, equalTo(tc.expectedValue));
        }
    }

    // Simple POJO to hold one row of test data
    static class TestCase {
        String method, endpoint, payload, expectedJsonPath, expectedValue;
        int expectedStatus;
        TestCase(String method, String endpoint, String payload, int expectedStatus,
                 String expectedJsonPath, String expectedValue) {
            this.method = method;
            this.endpoint = endpoint;
            this.payload = payload;
            this.expectedStatus = expectedStatus;
            this.expectedJsonPath = expectedJsonPath;
            this.expectedValue = expectedValue;
        }
    }
}
