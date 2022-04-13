package ai.cloudsight.androidsdk;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CloudSightResponse {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("ttl")
    @Expose
    private int ttl;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("flags")
    @Expose
    private List<String> flags = null;
    @SerializedName("nsfw")
    @Expose
    private Boolean nsfw;
    @SerializedName("categories")
    @Expose
    private List<String> categories = null;
    @SerializedName("similar_objects")
    @Expose
    private List<String> similarObjects = null;
    @SerializedName("structured_output")
    @Expose
    private StructuredOutput structuredOutput;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public Boolean getNsfw() {
        return nsfw;
    }

    public void setNsfw(Boolean nsfw) {
        this.nsfw = nsfw;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getSimilarObjects() {
        return similarObjects;
    }

    public void setSimilarObjects(List<String> similarObjects) {
        this.similarObjects = similarObjects;
    }

    public StructuredOutput getStructuredOutput() {
        return structuredOutput;
    }

    public void setStructuredOutput(StructuredOutput structuredOutput) {
        this.structuredOutput = structuredOutput;
    }

    public class StructuredOutput {

        @SerializedName("quantity")
        @Expose
        private List<String> quantity = null;
        @SerializedName("color")
        @Expose
        private List<String> color = null;

        public List<String> getQuantity() {
            return quantity;
        }

        public void setQuantity(List<String> quantity) {
            this.quantity = quantity;
        }

        public List<String> getColor() {
            return color;
        }

        public void setColor(List<String> color) {
            this.color = color;
        }

    }
}
