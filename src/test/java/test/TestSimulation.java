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

    //Feeder for test data
    private static FeederBuilder.FileBased<Object> jsonFeeder = jsonFile("data/productJsonFile.json").random();


   // Runtime parameters
    private static final int USER_COUNT = 100;
    private static final int RAMP_DURATION = 10;

    // Before block
    @Override
    public void before(){
        System.out.printf("Running test with %d users%n", USER_COUNT);
        System.out.printf("Ramping users over %d seconds%n", RAMP_DURATION);
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
            feed(jsonFeeder)
                    .exec(http("Create New Product - #{name}")
                    .post("/products")
                    .header("Authorization", "Bearer #{jwtToken}")
                    .body(ElFileBody("bodies/newProductTemplate.json")).asJson());

    private static ChainBuilder deleteProduct =
            exec(http("Delete Product - #{name}")
                    .delete("/admin/products/#{id}")
                    .header("Authorization", "Bearer #{jwtToken}"));

    // Scenario definition
    private ScenarioBuilder scn = scenario("E-commerce test")
            .exec(authenticate)
            .pause(2)
            .exec(getAllUsers)
            .pause(2)
            .exec(createNewProduct)
            .pause(2)
            .exec(deleteProduct);
    // Load Simulation
    {
        setUp(
//                scn.injectOpen(atOnceUsers(1))  // Inject 1 user to run the scenario
                scn.injectOpen(
                        nothingFor(5), // give test to warm up - to do nothing for 5 sec
                        rampUsers(USER_COUNT).during(RAMP_DURATION)
                )
        ).protocols(httpProtocol);
    }




}
