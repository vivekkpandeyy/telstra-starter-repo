package au.com.telstra.simcardactivator.controllers;

// Spring Framework imports for RESTful web services
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import au.com.telstra.simcardactivator.models.ActivationRequest;
import au.com.telstra.simcardactivator.models.ActuatorRequest;
import au.com.telstra.simcardactivator.models.ActuatorResponse;

/**
 * REST Controller to handle SIM card activation requests.
 * This controller exposes an endpoint to receive activation requests from Telstra store locations,
 * forwards them to the actuator microservice, and logs the outcome.
 */
@RestController // Marks this class as a Spring REST controller, handling incoming web requests
public class SimActivationController {

    // URL of the actuator microservice endpoint
    // This is where our service will send the SIM activation request.
   private static final String ACTUATOR_URL = "http://localhost:8444/actuate";

   /**
     * Handles POST requests to the "/activate" endpoint.
     * This method receives a JSON payload representing a SIM activation request,
     * processes it, interacts with the actuator, and returns a response.
     *
     * @param request The ActivationRequest object, automatically populated from the JSON body of the incoming POST request.
     * @return A ResponseEntity indicating the status of the request (e.g., success, error).
     */
    @PostMapping("/activate") // Maps HTTP POST requests to "/activate" to this method
   public ResponseEntity<String> activateSim(@RequestBody ActivationRequest request) {
        // Log the incoming request details for debugging and tracking
   System.out.println("Received activation request: " + request.toString());

        // Create an instance of RestTemplate.
        // RestTemplate is a synchronous client for making HTTP requests to other services.
        // For production applications, consider using Spring's WebClient for non-blocking I/O.
      RestTemplate restTemplate = new RestTemplate();

      try {
            // Prepare the request payload for the actuator.
            // The actuator only needs the ICCID.
            ActuatorRequest actuatorRequest = new ActuatorRequest(request.getIccid());
            System.out.println("Forwarding request to actuator: " + actuatorRequest.toString());

            // Send the POST request to the actuator and receive its response.
            // postForObject sends a POST request and converts the response body directly to ActuatorResponse.
            ActuatorResponse actuatorResponse = restTemplate.postForObject(
                ACTUATOR_URL,          // The URL of the actuator endpoint
                actuatorRequest,       // The request body to send to the actuator
                ActuatorResponse.class // The expected type of the response body from the actuator
            );

            // Check the success status returned by the actuator.
            if (actuatorResponse != null && actuatorResponse.isSuccess()) {
                // If activation was successful, log it and return a success response to the client.
               System.out.println("SIM activation SUCCESS for ICCID: " + request.getIccid());
               return new ResponseEntity<>("SIM activation successful.", HttpStatus.OK);
            }
            else {
                // If activation failed (or response was null), log it and return an internal server error.
                // In a real application, you might return a more specific error based on the actuator's response.
               System.out.println("SIM activation FAILED for ICCID: " + request.getIccid());
               return new ResponseEntity<>("SIM activation failed.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
      } catch (HttpClientErrorException e) {
            // Catches HTTP client errors (e.g., 4xx or 5xx status codes from the actuator).
            System.err.println("Error from actuator (HTTP status " + e.getStatusCode() + "): " + e.getMessage());
            return new ResponseEntity<>("Error communicating with actuator: " + e.getResponseBodyAsString(), HttpStatus.BAD_GATEWAY);
      } catch (ResourceAccessException e) {
            // Catches errors related to network issues (e.g., actuator not running, connection refused).
            System.err.println("Could not connect to actuator microservice: " + e.getMessage());
            return new ResponseEntity<>("Failed to connect to SIM activation service. Please check if the actuator is running.", HttpStatus.SERVICE_UNAVAILABLE);
      } catch (Exception e) {
            // Catches any other unexpected exceptions during the process.
            System.err.println("An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>("An unexpected error occurred during SIM activation.", HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }
}
