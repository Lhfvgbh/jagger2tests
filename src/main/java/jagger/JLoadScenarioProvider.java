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
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.ThreadCount;
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

        Long maxDurationInSecondsGeneral = Long.valueOf(getTestPropertyValue("jagger.load.scenario.tests.termination.max.duration.seconds"));
        String url = getTestPropertyValue("jagger.load.scenario.tests.url");

        Long iterationsNumberForTest1 = Long.valueOf(getTestPropertyValue("jagger.load.scenario.test1.termination.iterations"));
        Long userNumberForTest1 = Long.valueOf(getTestPropertyValue("jagger.load.scenario.test1.users"));
        Integer delayInvocationDurationForTest1 = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test1.delay.invocation.in.milliseconds"));
        String pathForTest1 = getTestPropertyValue("jagger.load.scenario.test1.url.path");

        JTestDefinition testDefinitionForTest1 = JTestDefinition
                .builder(Id.of("JaggerTestDefinition1"), new EndpointsProvider(url))
                .withInvoker(CustomHttpInvokerProvider.nonVerbose())
                .withQueryProvider(new QueriesProvider(pathForTest1))
                .withLoadBalancer(new RoundRobinLoadBalancer())
                .addValidator(new ValidatorResponseStatus())
                .addValidator(new ValidatorResponseUrl(pathForTest1))
                .addListener(new HeadersCalculator())
                .addListener(new NotNullInvocationListener())
                .build();

        JTerminationCriteria terminationCriteriaForTest1 = JTerminationCriteriaIterations
                .of(IterationsNumber.of(iterationsNumberForTest1), MaxDurationInSeconds.of(maxDurationInSecondsGeneral));

        JLoadProfileUsers loadProfileUsersForTest1 = JLoadProfileUsers
                .builder(NumberOfUsers.of(userNumberForTest1))
                .build();

        JLoadProfileUserGroups loadProfileUserGroupsForTest1 = JLoadProfileUserGroups
                .builder(loadProfileUsersForTest1)
                .withDelayBetweenInvocationsInMilliseconds(delayInvocationDurationForTest1)
                .build();

        JLoadTest jLoadTest1 = JLoadTest
                .builder(Id.of("JaggerLoadTest1"), testDefinitionForTest1, loadProfileUserGroupsForTest1, terminationCriteriaForTest1)
                .addListener(new CollectThreadsTestListener())
                .build();

        JParallelTestsGroup jParallelTestsGroupTest1 = JParallelTestsGroup
                .builder(Id.of("JaggerParallelTestsGroup1"), jLoadTest1)
                .build();


        //120
        Long terminationDurationForTest2 = Long.valueOf(getTestPropertyValue("jagger.load.scenario.test2.termination.duration.in.seconds"));
        //3
        Integer userNumberForTest2 = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test2.users"));
        //1500
        Integer delayInvocationDurationForTest2 = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test2.delay.invocation.in.milliseconds"));
        //20
        Integer delayStartDurationForTest2 = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test2.delay.start.in.milliseconds"));
        String pathForTest2 = getTestPropertyValue("jagger.load.scenario.test2.url.path");

        JTestDefinition testDefinitionForTest2 = JTestDefinition
                .builder(Id.of("JaggerTestDefinition2"), new EndpointsProvider(url))
                .withInvoker(CustomHttpInvokerProvider.nonVerbose())
                .withQueryProvider(new QueriesProvider(pathForTest2))
                .withLoadBalancer(new RoundRobinLoadBalancer())
                .addValidator(new ValidatorResponseStatus())
                .addValidator(new ValidatorResponseUrl(pathForTest2))
                .addListener(new ResponseCalculator())
                .build();

        JTerminationCriteria terminationCriteriaForTest2 = JTerminationCriteriaDuration.of(DurationInSeconds.of(terminationDurationForTest2));

        JLoadProfileInvocation loadProfileInvocationForTest2 = JLoadProfileInvocation
                .builder(InvocationCount.of(1), ThreadCount.of(userNumberForTest2))
                //.builder(InvocationCount.of(userNumberForTest2), ThreadCount.of(userNumberForTest2))
                .withDelayBetweenInvocationsInMilliseconds(delayInvocationDurationForTest2)
                .withPeriodBetweenLoadInSeconds(delayStartDurationForTest2)
                .build();

        JLoadTest jLoadTest2 = JLoadTest
                .builder(Id.of("JaggerLoadTest2"), testDefinitionForTest2, loadProfileInvocationForTest2, terminationCriteriaForTest2)
                .addListener(new CollectThreadsTestListener())
                .build();

        JParallelTestsGroup jParallelTestsGroupTest2 = JParallelTestsGroup
                .builder(Id.of("JaggerParallelTestsGroup2"), jLoadTest2)
                .build();


        Long terminationDurationForTest3 = Long.valueOf(getTestPropertyValue("jagger.load.scenario.test3.termination.duration.in.seconds"));
        Long userNumberForTest3= Long.valueOf(getTestPropertyValue("jagger.load.scenario.test3.users"));
        //2000
        Integer delayInvocationDurationForTest3User1 = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test3.delay.in.milliseconds.user1"));
        //1500
        Integer delayInvocationDurationForTest3User2 = Integer.valueOf(getTestPropertyValue("jagger.load.scenario.test3.delay.in.milliseconds.user2"));
        String pathForTest3 = getTestPropertyValue("jagger.load.scenario.test3.url.path");

        JTestDefinition testDefinitionForTest3 = JTestDefinition
                .builder(Id.of("JaggerTestDefinition3"), new EndpointsProvider(url))
                .withInvoker(CustomHttpInvokerProvider.nonVerbose())
                .withQueryProvider(new QueriesProvider(pathForTest3))
                .withLoadBalancer(new RoundRobinLoadBalancer())
                .addValidator(new ValidatorResponseStatus())
                .addValidator(new ValidatorResponseValue())
                //.addListener(new NotNullInvocationListener())
                .addListener(new ResponceLengthCalculator())
                .build();

        JTerminationCriteria terminationCriteriaForTest3User1 = JTerminationCriteriaDuration.of(DurationInSeconds.of(terminationDurationForTest3));

        JTerminationCriteria terminationCriteriaForTest3User2 = JTerminationCriteriaBackground.getInstance();

        JLoadProfileUsers loadProfileUsersForTest3User1 = JLoadProfileUsers
                .builder(NumberOfUsers.of(userNumberForTest3))
                .build();

        JLoadProfileUsers loadProfileUsersForTest3User2 = JLoadProfileUsers
                .builder(NumberOfUsers.of(userNumberForTest3))
                .build();

        JLoadProfileUserGroups loadProfileUserGroupsForTest31 = JLoadProfileUserGroups
                .builder(loadProfileUsersForTest3User1,loadProfileUsersForTest3User2)
                .withDelayBetweenInvocationsInMilliseconds(delayInvocationDurationForTest3User1)
                .build();

        JLoadProfileUserGroups loadProfileUserGroupsForTest32 = JLoadProfileUserGroups
                .builder(loadProfileUsersForTest3User1,loadProfileUsersForTest3User2)
                .withDelayBetweenInvocationsInMilliseconds(delayInvocationDurationForTest3User2)
                .build();

        JLoadTest jLoadTest3User1 = JLoadTest
                .builder(Id.of("JaggerLoadTest31"), testDefinitionForTest3, loadProfileUserGroupsForTest31, terminationCriteriaForTest3User1)
                .addListener(new CollectThreadsTestListener())
                .build();

        JLoadTest jLoadTest3User2 = JLoadTest
                .builder(Id.of("JaggerLoadTest32"), testDefinitionForTest3, loadProfileUserGroupsForTest32, terminationCriteriaForTest3User2)
                .addListener(new CollectThreadsTestListener())
                .build();

        JParallelTestsGroup jParallelTestsGroupTest3 = JParallelTestsGroup
                .builder(Id.of("JaggerParallelTestsGroup3"), jLoadTest3User1, jLoadTest3User2)
                .build();



        return JLoadScenario.builder(Id.of("testJaggerLoadScenario"), jParallelTestsGroupTest1,jParallelTestsGroupTest2,jParallelTestsGroupTest3)
                .addListener(new ExampleLoadScenarioListener())
                .withLatencyPercentiles(Arrays.asList(85D, 90D, 95D))
                .build();
    }
}