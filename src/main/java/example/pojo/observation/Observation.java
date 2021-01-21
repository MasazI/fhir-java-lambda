
package example.pojo.observation;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Observation {

    @SerializedName("resourceType")
    @Expose
    private String resourceType;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("text")
    @Expose
    private Text text;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("category")
    @Expose
    private List<Category> category = null;
    @SerializedName("code")
    @Expose
    private Code code;
    @SerializedName("subject")
    @Expose
    private Subject subject;
    @SerializedName("effectiveDateTime")
    @Expose
    private String effectiveDateTime;
    @SerializedName("valueQuantity")
    @Expose
    private ValueQuantity valueQuantity;
    @SerializedName("derivedFrom")
    @Expose
    private List<DerivedFrom> derivedFrom = null;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getEffectiveDateTime() {
        return effectiveDateTime;
    }

    public void setEffectiveDateTime(String effectiveDateTime) {
        this.effectiveDateTime = effectiveDateTime;
    }

    public ValueQuantity getValueQuantity() {
        return valueQuantity;
    }

    public void setValueQuantity(ValueQuantity valueQuantity) {
        this.valueQuantity = valueQuantity;
    }

    public List<DerivedFrom> getDerivedFrom() {
        return derivedFrom;
    }

    public void setDerivedFrom(List<DerivedFrom> derivedFrom) {
        this.derivedFrom = derivedFrom;
    }

}
