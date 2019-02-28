package jagger;

import com.fasterxml.jackson.annotation.*;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HeadersResponseParser {

    private ObjectMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(GetResponseParser.class);

    public HeadersResponseParser() {
        this.mapper = new ObjectMapper();
    }


    public HeaderResponse getResponseParser(JHttpResponse response) {
        try {
            String s =response.getBody().toString().replaceAll("-","").toLowerCase();
            return mapper.readValue(s, HeaderResponse.class);
        } catch (IOException e) {
            log.error("Invalid response body " + e.getMessage());
        }
        return null;
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "contentlength", "contenttype", "key"
    })
    public static class HeaderResponse {
        public HeaderResponse() {
        }

        @JsonProperty("contentlength")
        private String contentlength;
        @JsonProperty("contenttype")
        private String contenttype;
        @JsonProperty("key")
        private String key;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("contentlength")
        public String getContentlength() {
            return contentlength;
        }

        @JsonProperty("contentlength")
        public void setContentlength(String contentlength) {
            this.contentlength = contentlength;
        }

        @JsonProperty("contenttype")
        public String getContenttype() { return contenttype; }

        @JsonProperty("contenttype")
        public void setContenttype(String contenttype) {
            this.contenttype = contenttype;
        }

        @JsonProperty("key")
        public String getKey() {
            return key;
        }

        @JsonProperty("key")
        public void setKey(String key) {
            this.key = key;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }
}
