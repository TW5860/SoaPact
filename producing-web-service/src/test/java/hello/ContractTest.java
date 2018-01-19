package hello;

import java.util.Map;

import org.apache.http.HttpRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.TargetRequestFilter;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;


@RunWith(PactRunner.class)
@Provider("Country-Data-Provider")
@PactFolder("pacts")
public class ContractTest {
    // NOTE: this is just an example of embedded service that listens to requests, you should start here real service
    public static final Logger LOGGER = LoggerFactory.getLogger(ContractTest.class);
    @TestTarget
    public final Target target = new HttpTarget(61600);

    @BeforeClass
    public static void setUpService() {
        //Run DB, create schema
        //Run service
        //...
    }

    @Before
    public void before() {
        // Rest data
        // Mock dependent service responses
        // ...
    }

    @State("provider is available")
    public void toDefaultState() {
        // Prepare service before interaction that require "default" state
        // ...
      LOGGER.info("Now service in default state");
    }

    @State("state 2")
    public void toSecondState(Map<?, ?> params) {
        // Prepare service before interaction that require "state 2" state
        // ...
        LOGGER.info("Now service in 'state 2' state: " + params);
    }

    @TargetRequestFilter
    public void exampleRequestFilter(HttpRequest request) {
      LOGGER.info("exampleRequestFilter called: " + request);
    }
}