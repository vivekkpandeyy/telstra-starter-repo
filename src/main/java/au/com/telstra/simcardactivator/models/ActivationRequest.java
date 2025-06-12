package au.com.telstra.simcardactivator.models;

// Imports for JSON mapping
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the incoming request payload for SIM card activation.
 * This object will be automatically populated from the JSON body
 * of the POST request sent by Telstra store locations.
 */
public class ActivationRequest {

   // Unique global identifier for the SIM card
   @JsonProperty("iccid") // Maps the JSON field "iccid" to this Java field
   private String iccid;

   // Email address of the customer who owns the new SIM card
   @JsonProperty("customerEmail") // Maps the JSON field "customerEmail" to this Java field
   private String customerEmail;

    /**
     * Default constructor for JSON deserialization.
     * Spring's Jackson library requires a no-argument constructor to create instances
     * of this class when converting JSON to Java objects.
     */
    public ActivationRequest() {
        // No-argument constructor
    }

    /**
     * Parameterized constructor to create an instance with provided data.
     * @param iccid The ICCID of the SIM card.
     * @param customerEmail The customer's email address.
     */
    public ActivationRequest(String iccid, String customerEmail) {
        this.iccid = iccid;
        this.customerEmail = customerEmail;
    }

    // --- Getters ---

    /**
     * Retrieves the ICCID of the SIM card.
     * @return The ICCID string.
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Retrieves the customer's email address.
     * @return The customer email string.
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    // --- Setters ---

    /**
     * Sets the ICCID of the SIM card.
     * @param iccid The ICCID string to set.
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Sets the customer's email address.
     * @param customerEmail The customer email string to set.
     */
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    /**
     * Provides a string representation of the ActivationRequest object.
     * Useful for logging and debugging.
     * @return A string containing the ICCID and customer email.
     */
    @Override
    public String toString() {
        return "ActivationRequest{" +
               "iccid='" + iccid + '\'' +
               ", customerEmail='" + customerEmail + '\'' +
               '}';
    }
}