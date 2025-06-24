// src/test/java/stepDefinitions/SimCardActivatorStepDefinitions.java

package stepDefinitions;

// Cucumber annotations for defining test steps
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import au.com.telstra.simcardactivator.models.ActivationRequest;
import au.com.telstra.simcardactivator.models.QueryResponse;
import io.cucumber.java.en.Given; // Needed for Cucumber to pick up Spring context
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

/**
 * Step Definitions for the SIM Card Activation Cucumber features.
 * This class bridges the Gherkin syntax (from sim_card_activation.feature)
 * to executable Java code, allowing behavior-driven tests to be run
 * against the Spring Boot microservice.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // Starts Spring Boot app on a defined port (8080 by default) for tests
@CucumberContextConfiguration // Tells Cucumber to use Spring's test context
public class SimCardActivatorStepDefinitions {

    // TestRestTemplate is a convenient client for Spring Boot integration tests.
    // It's typically autowired, but we'll instantiate it directly here as a fallback
    // if your boilerplate doesn't provide automatic injection, ensuring it's available.
    // If your project uses @Autowired TestRestTemplate restTemplate;, remove the '= new TestRestTemplate()'.
    private TestRestTemplate restTemplate = new TestRestTemplate();

    // Base URL of your Spring Boot microservice, typically running on port 8080 during development.
    private String baseUrl = "http://localhost:8080";

    // Variables to hold responses between different steps within a single Cucumber scenario.
    // This allows subsequent steps to inspect the results of previous actions.
    private ResponseEntity<String> currentActivationResponse; // Holds the response from the /activate endpoint
    private QueryResponse currentQueryResponse;             // Holds the response from the /query endpoint

    /**
     * GIVEN step: Ensures the SIM card activation service is running.
     * In this setup, we assume the service is already launched externally before running tests.
     * This step primarily serves as a clear precondition in the BDD feature file.
     */
    @Given("the SIM card activation service is running")
    public void the_sim_card_activation_service_is_running() {
        // Log for debugging purposes to confirm this step is executed.
        System.out.println("Cucumber GIVEN: Verifying SIM card activation service is assumed to be running at " + baseUrl);
        // In a more robust test setup, you might add a call to a health endpoint here
        // to programmatically ensure the service is truly up and responsive.
        // For example: Assertions.assertEquals(HttpStatus.OK, restTemplate.getForEntity(baseUrl + "/actuator/health", String.class).getStatusCode());
    }

    /**
     * WHEN step: Submits an activation request to the microservice.
     * This step simulates a client sending a POST request to your /activate endpoint.
     *
     * @param iccid The ICCID of the SIM card to activate.
     * @param customerEmail The customer's email associated with the SIM.
     */
    @When("an activation request is submitted for ICCID {string} and customer email {string}")
    public void an_activation_request_is_submitted_for_iccid_and_customer_email(String iccid, String customerEmail) {
        // Set up HTTP headers for a JSON request.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the request body using your ActivationRequest DTO.
        ActivationRequest requestBody = new ActivationRequest(iccid, customerEmail);
        // Wrap the request body and headers into an HttpEntity.
        HttpEntity<ActivationRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Send the POST request to your microservice's /activate endpoint.
            // The response entity (status code, headers, body) is stored for assertion in THEN steps.
            currentActivationResponse = restTemplate.postForEntity(baseUrl + "/activate", requestEntity, String.class);
            // Log the outcome for visibility during test execution.
            System.out.println("Cucumber WHEN: Submitted activation request for ICCID: " + iccid + ", Service Response Status: " + currentActivationResponse.getStatusCode());
        } catch (HttpClientErrorException e) {
            // Catch specific HTTP client errors (e.g., 4xx or 5xx responses from the microservice itself).
            // Store the error response to allow subsequent steps to potentially assert on errors.
            currentActivationResponse = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
            System.err.println("Cucumber WHEN: Error submitting activation request: " + e.getResponseBodyAsString() + ", Status: " + e.getStatusCode());
        } catch (Exception e) {
            // Catch any other unexpected exceptions during the HTTP call.
            System.err.println("Cucumber WHEN: An unexpected error occurred during activation request: " + e.getMessage());
            // Store a generic internal server error response for consistency.
            currentActivationResponse = new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * THEN step: Verifies the state of the SIM activation record in the database
     * by querying the microservice's /query endpoint.
     *
     * @param recordId The expected database ID of the activation record.
     * @param expectedIccid The expected ICCID to be found in the record.
     * @param expectedActiveStatus The expected active status (true/false) in the record.
     */
    @Then("the SIM activation record with ID {long} should show ICCID {string} and active status {boolean}")
    public void the_sim_activation_record_with_id_should_show_iccid_and_active_status(long recordId, String expectedIccid, boolean expectedActiveStatus) {
        try {
            // Send a GET request to the /query endpoint, using the recordId from the Gherkin scenario.
            // The response is mapped directly to the QueryResponse DTO.
            currentQueryResponse = restTemplate.getForObject(baseUrl + "/query?simCardId=" + recordId, QueryResponse.class);
            // Log the retrieved data.
            System.out.println("Cucumber THEN: Queried record ID: " + recordId + ", Retrieved Data: " + currentQueryResponse);

            // Assertions: Validate the content of the retrieved record.
            // 1. Ensure the response object itself is not null.
            Assertions.assertNotNull(currentQueryResponse, "Query response object should not be null for record ID " + recordId);
            // 2. Assert that the ICCID in the retrieved record matches the expected ICCID.
            Assertions.assertEquals(expectedIccid, currentQueryResponse.getIccid(), "ICCID in queried record should match expected for ID " + recordId);
            // 3. Assert that the 'active' status in the retrieved record matches the expected status.
            Assertions.assertEquals(expectedActiveStatus, currentQueryResponse.isActive(), "Active status in queried record should match expected for ID " + recordId);

            System.out.println("Cucumber THEN: All assertions passed for record ID: " + recordId);

        } catch (HttpClientErrorException e) {
            // Catch HTTP errors (e.g., 404 Not Found if record doesn't exist, or 500 if internal server error).
            System.err.println("Cucumber THEN: Error querying record ID " + recordId + ": " + e.getResponseBodyAsString() + ", Status: " + e.getStatusCode());
            // Fail the test if an unexpected HTTP error occurs during the query.
            Assertions.fail("Cucumber THEN: Failed to query record due to HTTP error (Status: " + e.getStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (Exception e) {
            // Catch any other unexpected exceptions (e.g., network issues, deserialization problems).
            System.err.println("Cucumber THEN: An unexpected error occurred during record query for ID " + recordId + ": " + e.getMessage());
            // Fail the test for any unhandled exceptions.
            Assertions.fail("Cucumber THEN: An unexpected error occurred during record query: " + e.getMessage());
        }
    }
}
