package au.com.telstra.simcardactivator.models;

// Imports for JSON mapping
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the response payload received from the external actuator microservice.
 * This object indicates whether the SIM card activation was successful or not.
 */
public class ActuatorResponse {

    // Boolean indicating the success or failure of the SIM activation
    @JsonProperty("success") // Maps the JSON field "success" to this Java field
   private boolean success;

   /**
     * Default constructor for JSON deserialization.
     */
   public ActuatorResponse() {
        // No-argument constructor
   }

   /**
     * Parameterized constructor to create an instance with the provided success status.
     * @param success The boolean success status.
     */
   public ActuatorResponse(boolean success) {
   this.success = success;
   }

    // --- Getter ---

   /**
     * Checks if the SIM card activation was successful.
     * @return true if activation was successful, false otherwise.
     */
   public boolean isSuccess() {
   return success;
   }

    // --- Setter ---

   /**
     * Sets the success status of the SIM card activation.
     * @param success The boolean success status to set.
     */
   public void setSuccess(boolean success) {
   this.success = success;
   }

   /**
     * Provides a string representation of the ActuatorResponse object.
     * Useful for logging and debugging.
     * @return A string indicating the success status.
     */
   @Override
   public String toString() {
      return "ActuatorResponse{" +
               "success=" + success +
               '}';
   }
}
