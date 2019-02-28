package jagger;

import com.fasterxml.jackson.annotation.*;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GetResponseParser {

    private ObjectMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(GetResponseParser.class);

    public GetResponseParser() {
        this.mapper = new ObjectMapper();
    }

    public GetResponse getResponseParser(JHttpResponse response) {
        try {
            return mapper.readValue(response.getBody().toString(), GetResponse.class);
        } catch (IOException e) {
            log.error("Invalid response body " + e.getMessage());
        }
        return null;
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "args", "headers", "origin", "url"
    })
    public static class GetResponse {
        public GetResponse() {
        }

        @JsonProperty("args")
        private Map<String, Object> args;
        @JsonProperty("headers")
        private Map<String, Object> headers = new HashMap<String, Object>();
        @JsonProperty("origin")
        private String origin;
        @JsonProperty("url")
        private String url;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("args")
        public Map<String, Object> getArgs() {
            return args;
        }

        @JsonProperty("args")
        public void setArgs(Map<String, Object> args) {
            this.args = args;
        }

        @JsonProperty("headers")
        public Map<String, Object> getHeaders() {
            return headers;
        }

        @JsonProperty("headers")
        public void setHeaders(Map<String, Object> headers) {
            this.headers = headers;
        }

        @JsonProperty("origin")
        public String getOrigin() {
            return origin;
        }

        @JsonProperty("origin")
        public void setOrigin(String origin) {
            this.origin = origin;
        }
        @JsonProperty("url")
        public String getUrl() {
            return url;
        }

        @JsonProperty("url")
        public void setUrl(String url) {
            this.url = url;
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
