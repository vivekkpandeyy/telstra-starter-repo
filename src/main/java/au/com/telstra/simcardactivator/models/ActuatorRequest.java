package au.com.telstra.simcardactivator.models;
// Imports for JSON mapping
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the request payload sent to the external actuator microservice
 * for SIM card activation.
 */
public class ActuatorRequest {

   // The ICCID of the SIM card to be activated by the actuator
   @JsonProperty("iccid") // Maps the JSON field "iccid" to this Java field
   private String iccid;

   /**
     * Default constructor for JSON deserialization.
     */
   public ActuatorRequest() {
        // No-argument constructor
   }

   /**
     * Parameterized constructor to create an instance with the provided ICCID.
     * @param iccid The ICCID of the SIM card.
     */
   public ActuatorRequest(String iccid) {
   this.iccid = iccid;
   }

    // --- Getter ---

   /**
     * Retrieves the ICCID to be sent to the actuator.
     * @return The ICCID string.
     */
   public String getIccid() {
   return iccid;
   }

    // --- Setter ---

   /**
     * Sets the ICCID for the actuator request.
     * @param iccid The ICCID string to set.
     */
   public void setIccid(String iccid) {
   this.iccid = iccid;
   }

   /**
     * Provides a string representation of the ActuatorRequest object.
     * Useful for logging and debugging.
     * @return A string containing the ICCID.
     */
      @Override
      public String toString() {
         return "ActuatorRequest{" +
               "iccid='" + iccid + '\'' +
               '}';
      }
}