
package example.pojo.observation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subject {

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
