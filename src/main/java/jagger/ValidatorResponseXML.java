package jagger;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidatorProvider;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ValidatorResponseXML implements ResponseValidatorProvider {
    private static final Logger log = LoggerFactory.getLogger(ValidatorResponseStatus.class);

    @Override
    public ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse> provide(String taskId, String sessionId, NodeContext kernelContext) {
        return new ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse>(taskId, sessionId, kernelContext) {
            @Override
            public String getName() {
                return "Correct XML-response-type validator";
            }

            @Override
            public boolean validate(JHttpQuery jHttpQuery, JHttpEndpoint jHttpEndpoint, JHttpResponse jHttpResponse, long l) {

                try {
                    String response = jHttpResponse.getBody().toString();
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    builder.parse(new ByteArrayInputStream(response.getBytes()));
                    return true;
                } catch (SAXException | IOException | ParserConfigurationException e) {
                    log.error("Invalid response format" + jHttpQuery.getQueryParams().toString());
                    e.printStackTrace();
                }
                return false;
            }
        };
    }

}
