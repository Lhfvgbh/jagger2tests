package jagger;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.AvgMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ResponseCalculator extends ServicesAware implements Provider<InvocationListener> {
    private final String metricName = "avg-inner-response-parameters-counter";

    @Override
    protected void init() {
        getMetricService().createMetric(new MetricDescription(metricName)
                .displayName("Average number of responses inner parameters")
                .showSummary(true)
                .addAggregator(new AvgMetricAggregatorProvider()));
    }

    @Override
    public InvocationListener provide() {
        return new InvocationListener() {
            @Override
            public void onStart(InvocationInfo invocationInfo) {
            }

            @Override
            public void onSuccess(InvocationInfo invocationInfo) {
                try {
                    String expectedNode ="/slideshow/slide";

                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document xmlDocument = builder.parse(new ByteArrayInputStream(((JHttpResponse)invocationInfo.getResult()).getBody().toString().getBytes()));
                    XPath xPath = XPathFactory.newInstance().newXPath();

                    NodeList nodeList = (NodeList) xPath.compile(expectedNode).evaluate(xmlDocument, XPathConstants.NODESET);
                    int size = nodeList.getLength();

                    if (size > 0) {
                        getMetricService().saveValue(metricName, size);
                    }
                } catch (ParserConfigurationException | IOException | XPathExpressionException | SAXException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(InvocationInfo invocationInfo, InvocationException e) {
            }

            @Override
            public void onError(InvocationInfo invocationInfo, Throwable error) {
            }
        };
    }
}
