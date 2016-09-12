Akka-Jersey
=================

An example asynchronous REST API written in Java using Jersey 2 and Akka 2.4.10

Key concepts
------------
* Instantiating an Akka ActorSystem at server startup and injecting it into each request
* Fulfilling an asynchronous Jersey REST service invocation using Akka actors.

Main Modules
------------
* Financial: the system used to process the transactions
* Rest-Service: contains the REST Endpoint
* Transaction-generator: used to test the application (Should be added proper IT)

How to run the example
----------------------
1. Compile the projet using mvn clean install
2. Run the financial services: mvn exec:java -pl financial
3. Run the rest-service: mvn jetty:run -pl rest-service
4. Open the webpage at http://127.0.0.1:8080/test.html
5. Launch transaction-generator: mvn exec:java -pl transaction-generator
6. See the trends on http://127.0.0.1:8080/test.html. The page is automatically updated every 5 seconds

Prerequisites
-------------
* JDK 8
* Maven 3.x
