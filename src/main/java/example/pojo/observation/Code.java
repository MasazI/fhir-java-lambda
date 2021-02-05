
package example.pojo.observation;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Code {

    @SerializedName("coding")
    @Expose
    private List<Coding_> coding = null;
    @SerializedName("text")
    @Expose
    private String text;

    public List<Coding_> getCoding() {
        return coding;
    }

    public void setCoding(List<Coding_> coding) {
        this.coding = coding;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
