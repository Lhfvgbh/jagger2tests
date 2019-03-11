package jagger;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.AvgMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class GetHeadersCalculator extends ServicesAware implements Provider<InvocationListener> {
    private final String metricName = "avg-headers-counter-for-default-response-type";

    @Override
    protected void init() {
        getMetricService().createMetric(new MetricDescription(metricName)
                .displayName("Average number of HTML headers for default response type")
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
                if (invocationInfo.getResult() != null) {
                    byte[] data = (byte[]) ((JHttpResponse) invocationInfo.getResult()).getBody();
                    String body = new String(data, StandardCharsets.UTF_8);
                    GetResponseParser parser = new GetResponseParser();
                    GetResponseParser.GetResponse restResponse = parser.getResponseParser(body);
                    Map<String, Object> headers = restResponse.getHeaders();
                    getMetricService().saveValue(metricName, headers.size());
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
