package jagger;

import com.griddynamics.jagger.engine.e1.collector.CollectThreadsTestListener;
import com.griddynamics.jagger.engine.e1.collector.invocation.NotNullInvocationListener;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.ExampleLoadScenarioListener;
import com.griddynamics.jagger.invoker.RoundRobinLoadBalancer;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileInvocation;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUserGroups;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUsers;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.InvocationCount;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.NumberOfUsers;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaDuration;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.DurationInSeconds;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jagger.util.JaggerPropertiesProvider;

import java.util.Arrays;

@Configuration
public class JLoadScenarioProvider extends JaggerPropertiesProvider {

    @Bean
    public JLoadScenario testJaggerLoadScenario() {

        //90
        Integer maxDurationInSecondsGeneral = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.tests.termination.max.duration.seconds"));
        //https://httpbin.org:443
        String url = getTestPropertyValue("jagger.load.scenario.tests.url");

        //5
        Integer iterationsNumberForGetTest = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test1.termination.iterations"));
        //2
        Integer userNumberForGetTest = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test1.users"));
        //3000
        Integer delayInvocationForGetTest = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test1.delay.invocation.in.milliseconds"));
        ///get
        String pathForGetTest = getTestPropertyValue("jagger.load.scenario.test1.url.path");

        JTestDefinition getTestDefinition = JTestDefinition
                .builder(Id.of("GetTestDefinition"), new EndpointsProvider(url))
                .withInvoker(CustomHttpInvokerProvider.nonVerbose())
                .withQueryProvider(new QueriesProvider(pathForGetTest))
                .withLoadBalancer(new RoundRobinLoadBalancer())
                .addValidator(new ValidatorResponseStatus())
                .addValidator(new ValidatorResponseUrl(pathForGetTest))
                .addListener(new HeadersCalculator())
                .addListener(new NotNullInvocationListener())
                .build();

        JTerminationCriteria getTerminationCriteria = JTerminationCriteriaIterations
                .of(IterationsNumber.of(iterationsNumberForGetTest), MaxDurationInSeconds.of(maxDurationInSecondsGeneral));

        JLoadProfileUsers getLoadProfileUser = JLoadProfileUsers
                .builder(NumberOfUsers.of(userNumberForGetTest))
                .build();

        JLoadProfileUserGroups getLoadProfileGroup = JLoadProfileUserGroups
                .builder(getLoadProfileUser)
                .withDelayBetweenInvocationsInMilliseconds(delayInvocationForGetTest)
                .build();

        JLoadTest getLoadTest = JLoadTest
                .builder(Id.of("GetLoadTest"), getTestDefinition, getLoadProfileGroup, getTerminationCriteria)
                .addListener(new CollectThreadsTestListener())
                .build();

        JParallelTestsGroup getTestsGroup = JParallelTestsGroup
                .builder(Id.of("GetTestsGroup"), getLoadTest)
                .build();


        //120
        Integer terminationDurationForXMLTest = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test2.termination.duration.in.seconds"));
        //3
        Integer userNumberForXMLTest = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test2.users"));
        //15000
        Integer delayInvocationForXMLTest = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test2.delay.invocation.in.milliseconds"));
        //20
        Integer delayStartForXMLTest = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test2.delay.start.in.milliseconds"));
        ///xml
        String pathForXMLTest = getTestPropertyValue("jagger.load.scenario.test2.url.path");

        JTestDefinition xmlTestDefinition = JTestDefinition
                .builder(Id.of("XMLTestDefinition"), new EndpointsProvider(url))
                .withInvoker(CustomHttpInvokerProvider.nonVerbose())
                .withQueryProvider(new QueriesProvider(pathForXMLTest))
                .withLoadBalancer(new RoundRobinLoadBalancer())
                .addValidator(new ValidatorResponseStatus())
                .addValidator(new ValidatorResponseXML())
                .addListener(new ResponseCalculator())
                .build();

        JTerminationCriteria xmlTerminationCriteria = JTerminationCriteriaDuration.of(DurationInSeconds.of(terminationDurationForXMLTest));

        JLoadProfileUsers xmlLoadProfileUser1 = JLoadProfileUsers
                .builder(NumberOfUsers.of(1))
                .build();
        JLoadProfileUsers xmlLoadProfileUser2 = JLoadProfileUsers
                .builder(NumberOfUsers.of(1))
                .withStartDelayInSeconds(delayStartForXMLTest)
                .build();
        JLoadProfileUsers xmlLoadProfileUser3 = JLoadProfileUsers
                .builder(NumberOfUsers.of(1))
                .withStartDelayInSeconds(delayStartForXMLTest*2)
                .build();

        JLoadProfileUserGroups xmlLoadProfileGroup = JLoadProfileUserGroups
                .builder(xmlLoadProfileUser1,xmlLoadProfileUser2,xmlLoadProfileUser3)
                .withDelayBetweenInvocationsInMilliseconds(delayInvocationForXMLTest)
                .build();

        JLoadTest xmlLoadTest = JLoadTest
                .builder(Id.of("XMLLoadTest"), xmlTestDefinition, xmlLoadProfileGroup, xmlTerminationCriteria)
                .addListener(new CollectThreadsTestListener())
                .build();

        JParallelTestsGroup xmlTestsGroup = JParallelTestsGroup
                .builder(Id.of("XMLTestsGroup"), xmlLoadTest)
                .build();


        //180
        Integer terminationDurationForResponseTest = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test3.termination.duration.in.seconds"));
        //2
        Integer userNumberForResponseTest= Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test3.users"));
        //20000
        Integer delayInvocationForResponseTest = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test3.delay.in.milliseconds.user1"));
        //15000
        Integer delayStartForResponseTest = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test3.delay.in.milliseconds.user2"));
        ///response-headers
        String pathForResponseTest = getTestPropertyValue("jagger.load.scenario.test3.url.path");

        JTestDefinition responseTestDefinition = JTestDefinition
                .builder(Id.of("ResponseTestDefinition"), new EndpointsProvider(url))
                .withInvoker(CustomHttpInvokerProvider.nonVerbose())
                .withQueryProvider(new QueriesProvider(pathForResponseTest))
                .withLoadBalancer(new RoundRobinLoadBalancer())
                .addValidator(new ValidatorResponseStatus())
                .addValidator(new ValidatorResponseValue())
                .addListener(new ResponseLengthCalculator())
                .build();

        JTerminationCriteria responseTerminationCriteriaForUser1 = JTerminationCriteriaDuration.of(DurationInSeconds.of(terminationDurationForResponseTest));

        JTerminationCriteria responseTerminationCriteriaForUser2 = JTerminationCriteriaBackground.getInstance();

        JLoadProfileUsers responseLoadProfileUser1 = JLoadProfileUsers
                .builder(NumberOfUsers.of(1))
                .build();

        JLoadProfileUsers responseLoadProfileUser2 = JLoadProfileUsers
                .builder(NumberOfUsers.of(1))
                .withStartDelayInSeconds(delayStartForResponseTest)
                .build();

        JLoadProfileUserGroups responseLoadProfileGroup1 = JLoadProfileUserGroups
                .builder(responseLoadProfileUser1,responseLoadProfileUser2)
                .withDelayBetweenInvocationsInMilliseconds(delayInvocationForResponseTest)
                .build();

        JLoadProfileUserGroups responseLoadProfileGroup2 = JLoadProfileUserGroups
                .builder(responseLoadProfileUser1,responseLoadProfileUser2)
                .withDelayBetweenInvocationsInMilliseconds(delayInvocationForResponseTest)
                .build();

        JLoadTest responseLoadTestUser1 = JLoadTest
                .builder(Id.of("ResponseLoadTestUser1"), responseTestDefinition, responseLoadProfileGroup1, responseTerminationCriteriaForUser1)
                .addListener(new CollectThreadsTestListener())
                .build();

        JLoadTest responseLoadTestUser2 = JLoadTest
                .builder(Id.of("ResponseLoadTestUser2"), responseTestDefinition, responseLoadProfileGroup2, responseTerminationCriteriaForUser2)
                .addListener(new CollectThreadsTestListener())
                .build();

        JParallelTestsGroup responseTestsGroup = JParallelTestsGroup
                .builder(Id.of("ResponseTestsGroup"), responseLoadTestUser1, responseLoadTestUser2)
                .build();



        return JLoadScenario.builder(Id.of("testJaggerLoadScenario"), getTestsGroup,xmlTestsGroup,responseTestsGroup)
                .addListener(new ExampleLoadScenarioListener())
                .withLatencyPercentiles(Arrays.asList(85D, 90D, 95D))
                .build();
    }
}