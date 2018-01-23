# JavaSeifenBenutzer

This is a project to create Soap/Xml support for pact-jvm via a reverse proxy that converts XML to JSON and vice versa.

This project was inspired by the need for Consumer Driven Contract Testing at a client with mostly Soap based webservices.

![Converting Reverse Proxy Model](/pact-soap-extension.png?raw=true "Soap Consumer Pact Test")

## Structure

The consuming webservice shows how a soap service consumer is tested with pact.
 
 
The producing webservice verifies the consumers pact.
  
  
The pact-soap-extension contains everything necessary to test a soap web service.
A .jar is created by using mvn package. It is mandatory to exclude some dependencies in the .pom however.

Use undertow instead of tomcat as the embedded web-server:
```sh
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web-services</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>
```


Exclude android-json and other JSON librarys as the extension requires org.json:

```sh
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>com.vaadin.external.google</groupId>
            <artifactId>android-json</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20160212</version>
</dependency>
```
        

Exclude pact-jvm-consumer in case you are a provider
```sh
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>pact-soap-extension</artifactId>
    <version>0.1.0</version>
    <exclusions>
        <exclusion>
            <groupId>au.com.dius</groupId>
            <artifactId>pact-jvm-consumer_2.11</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>au.com.dius</groupId>
    <artifactId>pact-jvm-provider-junit_2.12</artifactId>
    <version>3.5.10</version>
</dependency>
```

