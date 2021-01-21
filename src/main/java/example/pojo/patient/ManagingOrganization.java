
package example.pojo.patient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ManagingOrganization {

    @SerializedName("reference")
    @Expose
    private String reference;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

}
