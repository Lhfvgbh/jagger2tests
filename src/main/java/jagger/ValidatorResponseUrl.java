package jagger;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidatorProvider;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidatorResponseUrl implements ResponseValidatorProvider {
    private static final Logger log = LoggerFactory.getLogger(ValidatorResponseStatus.class);
    private String path;

    public ValidatorResponseUrl(String path){
        this.path = path;
    }

    @Override
    public ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse> provide(String taskId, String sessionId, NodeContext kernelContext) {
        return new ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse>(taskId, sessionId, kernelContext) {
            @Override
            public String getName() {
                return "Correct URL validator";
            }

            @Override
            public boolean validate(JHttpQuery jHttpQuery, JHttpEndpoint jHttpEndpoint, JHttpResponse jHttpResponse, long l) {
                if (new GetResponseParser().getResponseParser(jHttpResponse).getUrl().equals(path)) {
                    log.error("Invalid query path " + jHttpQuery.getPath());
                    return false;
                } else return true;
            }
        };
    }
}

