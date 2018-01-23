package hello;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import au.com.dius.pact.soap.provider.SoapTarget;


@RunWith(PactRunner.class) // Say JUnit to run tests with custom Runner
@Provider("Country-Data-Provider") // Set up name of tested provider
@PactFolder("pacts") // Point where to find pacts (See also section Pacts source in documentation)
public class ContractTest {
    // NOTE: this is just an example of embedded service that listens to requests, you should start here real service
    @BeforeClass //Method will be run once: before whole contract test suite
    public static void setUpService() {
        //Run DB, create schema
        //Run service
        //...
        // TODO: Start Provider
        // TODO: Fix Content Type Header Missmatch Error (Lines deleted from contract)
    }

    @Before //Method will be run before each test of interaction
    public void before() {
        // Rest data
        // Mock dependent service responses
        // ...
    }

    @State("provider is available") // Method will be run before testing interactions that require "default" or "no-data" state
    public void toDefaultState() {
        // Prepare service before interaction that require "default" state
        // ...
        System.out.println("Now service in default state");
    }

    @TestTarget // Annotation denotes Target that will be used for tests
    public final Target target = new SoapTarget(8083,8084); // Out-of-the-box implementation of Target (for more information take a look at Test Target section)
}