
package example.pojo.observation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DerivedFrom {

    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("display")
    @Expose
    private String display;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

}
