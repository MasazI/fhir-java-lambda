
package example.pojo.patient;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Name {

    @SerializedName("use")
    @Expose
    private String use;
    @SerializedName("family")
    @Expose
    private String family;
    @SerializedName("given")
    @Expose
    private List<String> given = null;
    @SerializedName("period")
    @Expose
    private Period period;

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public List<String> getGiven() {
        return given;
    }

    public void setGiven(List<String> given) {
        this.given = given;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

}
