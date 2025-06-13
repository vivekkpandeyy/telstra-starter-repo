package au.com.telstra.simcardactivator.controllers;
// Spring Framework imports for RESTful web services
import java.util.Optional; // For dependency injection

import org.springframework.beans.factory.annotation.Autowired; // For handling GET requests
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping; // For handling query parameters
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import au.com.telstra.simcardactivator.models.ActivationRequest;
import au.com.telstra.simcardactivator.models.ActuatorRequest;
import au.com.telstra.simcardactivator.models.ActuatorResponse; // Your new Entity
import au.com.telstra.simcardactivator.models.QueryResponse; // Your new Query DTO
import au.com.telstra.simcardactivator.models.SimActivationRecord; // Your new Repository
import au.com.telstra.simcardactivator.repositories.SimActivationRecordRepository; // For handling results from findById

/**
 * REST Controller to handle SIM card activation requests and query records.
 * This controller exposes endpoints to:
 * 1. Receive activation requests, forward them to the actuator, and record the outcome.
 * 2. Query activation records by ID.
 */
@RestController // Marks this class as a Spring REST controller
public class SimActivationController {

    // URL of the actuator microservice endpoint
private static final String ACTUATOR_URL = "http://localhost:8444/actuate";

    // Inject the SimActivationRecordRepository to interact with the database
    @Autowired // Spring will automatically create and provide an instance of SimActivationRecordRepository
private SimActivationRecordRepository repository;

/**
     * Handles POST requests to the "/activate" endpoint.
     * Receives a SIM activation request, forwards it to the actuator,
     * and records the transaction outcome in the H2 database.
     *
     * @param request The ActivationRequest object containing ICCID and customer email.
     * @return A ResponseEntity indicating the status of the activation and persistence.
     */
@PostMapping("/activate")
public ResponseEntity<String> activateSim(@RequestBody ActivationRequest request) {
      System.out.println("Received activation request: " + request.toString());

      RestTemplate restTemplate = new RestTemplate();
        boolean activationSuccess = false; // Default to false

      try {
            ActuatorRequest actuatorRequest = new ActuatorRequest(request.getIccid());
            System.out.println("Forwarding request to actuator: " + actuatorRequest.toString());

            ActuatorResponse actuatorResponse = restTemplate.postForObject(
            ACTUATOR_URL,
            actuatorRequest,
            ActuatorResponse.class
            );

            if (actuatorResponse != null && actuatorResponse.isSuccess()) {
            activationSuccess = true;
            System.out.println("SIM activation SUCCESS for ICCID: " + request.getIccid());
            } else {
            System.out.println("SIM activation FAILED for ICCID: " + request.getIccid());
            }

      } catch (HttpClientErrorException e) {
            System.err.println("Error from actuator (HTTP status " + e.getStatusCode() + "): " + e.getMessage());
            // activationSuccess remains false
            return new ResponseEntity<>("Error communicating with actuator: " + e.getResponseBodyAsString(), HttpStatus.BAD_GATEWAY);
      } catch (ResourceAccessException e) {
            System.err.println("Could not connect to actuator microservice: " + e.getMessage());
            // activationSuccess remains false
            return new ResponseEntity<>("Failed to connect to SIM activation service. Please check if the actuator is running.", HttpStatus.SERVICE_UNAVAILABLE);
      } catch (Exception e) {
            System.err.println("An unexpected error occurred during actuator communication: " + e.getMessage());
            // activationSuccess remains false
            return new ResponseEntity<>("An unexpected error occurred during SIM activation.", HttpStatus.INTERNAL_SERVER_ERROR);
      } finally {
            // This block ensures the record is saved REGARDLESS of actuator communication success/failure
            SimActivationRecord record = new SimActivationRecord();
            record.setIccid(request.getIccid());
            record.setCustomerEmail(request.getCustomerEmail());
            record.setActive(activationSuccess); // Set based on the actual outcome

            try {
                SimActivationRecord savedRecord = repository.save(record); // Save the record to the database
            System.out.println("Saved activation record to database: " + savedRecord.toString());
            } catch (Exception dbException) {
            System.err.println("Failed to save activation record to database: " + dbException.getMessage());
                // Consider returning a different status if DB save is critical
            return new ResponseEntity<>("SIM activation process completed, but failed to record transaction.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
      }

        // Return appropriate HTTP status based on activation outcome
      if (activationSuccess) {
            return new ResponseEntity<>("SIM activation successful and record saved.", HttpStatus.OK);
      } else {
            return new ResponseEntity<>("SIM activation failed, but record saved.", HttpStatus.OK); // Return OK if record saved despite activation failure
      }
}

/**
     * Handles GET requests to the "/query" endpoint to retrieve a SIM activation record by ID.
     *
     * @param simCardId The ID of the SIM activation record to retrieve from the database.
     * @return A ResponseEntity containing the queried record as a JSON object, or a 404 if not found.
     */
@GetMapping("/query")
public ResponseEntity<QueryResponse> getSimActivationRecord(@RequestParam long simCardId) {
      System.out.println("Received query request for simCardId: " + simCardId);

        // Use the repository to find the record by its ID.
        // Optional is a container object which may or may not contain a non-null value.
        // It helps to avoid NullPointerExceptions.
      Optional<SimActivationRecord> recordOptional = repository.findById(simCardId);

      if (recordOptional.isPresent()) {
            // If the record is found, retrieve it
            SimActivationRecord record = recordOptional.get();

            // Map the entity to the QueryResponse DTO as per the requirement
            QueryResponse response = new QueryResponse(
            record.getIccid(),
            record.getCustomerEmail(),
            record.isActive()
            );
            System.out.println("Found record: " + record.toString());
            return new ResponseEntity<>(response, HttpStatus.OK); // Return 200 OK with the record data
      } else {
            // If no record is found for the given ID
            System.out.println("No record found for simCardId: " + simCardId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 Not Found
      }
}
}
