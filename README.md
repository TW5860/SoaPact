# JavaSeifenBenutzer

This is a project to create Soap/Xml support for pact-jvm via a reverse proxy that converts XML to JSON and vice versa.

This project was inspired by the need for Consumer Driven Contract Testing at a client with mostly Soap based webservices.

![Alt text](/pact-soap-extension.png?raw=true "Soap Consumer Pact Test")

## Structure

The consuming webservice shows how a service consumer that is generated from a .wsdl file is tested with pact. 
The /src/ directory contains the proxy, a converter and a readable hash generator.
In the /test/ directory are the examples on how to test a consumer as well as the tests for the pact-soap-extension.

The producing webservices are all of the same kind with additional, missing or changed fields on the interface in order to demonstrate a breaking of the client by the producer. In the /test/ directory is a sample on how to test a producer with the pact-soap-extension.
