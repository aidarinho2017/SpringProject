package test;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;



import java.util.HashMap;
import java.util.Map;

public class TestSimulation extends Simulation {
    //Http configuration
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8081") // Base URL without the /users part
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Authorization header with a valid JWT token
    private Map<String, String> headers_1 = new HashMap<>();
    {
        headers_1.put("Authorization", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNixhZG1pbkBleGFtcGxlLmNvbSIsImlzcyI6IkNvZGVKYXZhIiwicm9sZXMiOiJbUk9MRV9BRE1JTl0iLCJpYXQiOjE3MzQxMTQwODksImV4cCI6MTczNDIwMDQ4OX0.Gm_TjhGKY5PgBJBB1hpRdm1i08P3iT0VZh8IDpFLvK_xhaU7ahgph3jfNnqPhu4qJSIYBfqPth4I1N6oQeMBr"); // Replace with a valid JWT token
    }

    //HTTP calls
    private static ChainBuilder authenticate =
            exec(http("Authenticate")
                    .post("/auth/login")
                    .body(StringBody("{\n" +
                            "  \"email\": \"admin@example.com\",\n" +
                            "  \"password\": \"admin123\"\n" +
                            "}"))
                    .check(jmesPath("accessToken").saveAs("jwtToken")));

    private static ChainBuilder getAllUsers =
            exec(http("Get All Users")
                    .get("/users") // Just the /users endpoint
                    .header("Authorization", "Bearer #{jwtToken}"));

    private static ChainBuilder createNewProduct =
            exec(http("Create New Product")
                    .post("/products")
                    .header("Authorization", "Bearer #{jwtToken}")
                    .body(ElFileBody("bodies/newProductTemplate.json")).asJson());


    // Scenario definition
    private ScenarioBuilder scn = scenario("E-commerce test")
            .exec(authenticate)
            .pause(2)
            .exec(getAllUsers);
//            .exec(http("Get All Users")
//                    .get("/users")  // Just the /users endpoint
//                    .headers(headers_1)  // Add the Authorization header here
//            );

    // Load Simulation
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))  // Inject 1 user to run the scenario
        ).protocols(httpProtocol);
    }




}
