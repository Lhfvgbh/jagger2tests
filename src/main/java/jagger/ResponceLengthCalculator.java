package jagger;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.AvgMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;


public class ResponceLengthCalculator extends ServicesAware implements Provider<InvocationListener> {

    private final String metricName = "avg-response-content-length-counter";

    @Override
    protected void init() {
        getMetricService().createMetric(new MetricDescription(metricName)
                .displayName("Average length of HttpQuery")
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
                if (invocationInfo.getResult() != null){
                    JHttpResponse response = (JHttpResponse) invocationInfo.getResult();
                    HeadersResponseParser parser = new HeadersResponseParser();
                    HeadersResponseParser.HeaderResponse restResponse = parser.getResponseParser(response);
                    int counter = Integer.parseInt(restResponse.getContentlength());
                    //int i = restResponse.getKey().getBytes().length;
                    getMetricService().saveValue(metricName, counter);
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
