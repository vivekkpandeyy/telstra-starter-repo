package au.com.telstra.simcardactivator.models;
// Jackson annotations for JSON mapping
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the response payload for the GET /query endpoint.
 * This DTO (Data Transfer Object) structures the data returned
 * from a database query about a SIM activation record.
 */
public class QueryResponse {

   @JsonProperty("iccid")
   private String iccid;

   @JsonProperty("customerEmail")
   private String customerEmail;

   @JsonProperty("active")
   private boolean active;

   /**
     * Default no-argument constructor for JSON serialization/deserialization.
     */
   public QueryResponse() {
   }

   /**
     * Parameterized constructor to create a QueryResponse object.
     * @param iccid The ICCID of the SIM card.
     * @param customerEmail The customer's email address.
     * @param active The activation status.
     */
   public QueryResponse(String iccid, String customerEmail, boolean active) {
      this.iccid = iccid;
      this.customerEmail = customerEmail;
      this.active = active;
   }

    // --- Getters ---

   public String getIccid() {
      return iccid;
   }

   public String getCustomerEmail() {
      return customerEmail;
   }

   public boolean isActive() {
      return active;
   }

    // --- Setters ---

   public void setIccid(String iccid) {
      this.iccid = iccid;
   }

   public void setCustomerEmail(String customerEmail) {
      this.customerEmail = customerEmail;
   }

      public void setActive(boolean active) {
         this.active = active;
      }

      /**
     * Provides a string representation of the QueryResponse object.
     * Useful for logging and debugging.
     * @return A string containing the response details.
     */
      @Override
      public String toString() {
         return "QueryResponse{" +
               "iccid='" + iccid + '\'' +
               ", customerEmail='" + customerEmail + '\'' +
               ", active=" + active +
               '}';
   }
}
