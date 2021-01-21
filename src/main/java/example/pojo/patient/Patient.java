
package example.pojo.patient;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Patient {

    @SerializedName("resourceType")
    @Expose
    private String resourceType;
    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("identifier")
    @Expose
    private List<Identifier> identifier = null;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("name")
    @Expose
    private List<Name> name = null;
    @SerializedName("telecom")
    @Expose
    private List<Telecom> telecom = null;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("birthDate")
    @Expose
    private String birthDate;
    @SerializedName("address")
    @Expose
    private List<Address> address = null;
    @SerializedName("generalPractitioner")
    @Expose
    private List<GeneralPractitioner> generalPractitioner = null;
    @SerializedName("managingOrganization")
    @Expose
    private ManagingOrganization managingOrganization;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Name> getName() {
        return name;
    }

    public void setName(List<Name> name) {
        this.name = name;
    }

    public List<Telecom> getTelecom() {
        return telecom;
    }

    public void setTelecom(List<Telecom> telecom) {
        this.telecom = telecom;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public List<GeneralPractitioner> getGeneralPractitioner() {
        return generalPractitioner;
    }

    public void setGeneralPractitioner(List<GeneralPractitioner> generalPractitioner) {
        this.generalPractitioner = generalPractitioner;
    }

    public ManagingOrganization getManagingOrganization() {
        return managingOrganization;
    }

    public void setManagingOrganization(ManagingOrganization managingOrganization) {
        this.managingOrganization = managingOrganization;
    }

}
