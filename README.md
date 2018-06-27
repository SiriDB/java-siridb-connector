# SiriDB Connector for Java

This manual describes how to install and configure SiriDB Connector for Java, a self-contained Java driver for communicating with SiriDB servers, and how to use it for developing database applications.

## Installation

In order to get the connector to work properly, you need to install the Java QPack library. You can find the repository [here](https://github.com/SiriDB/java-siridb-connector). Add this library to your project in your favorite Java IDE.

After you've installed QPack succesfully, you need to install the connector itself. You can grab a copy of the compiled jar file [here](https://github.com/SiriDB/java-siridb-connector/releases/latest) or clone this repository and compile the code yourself. Add this jar file as library to your project as well.

## Example

The code shown in the example makes use of a countDownLatch which waits until requests have been completed. In this case, the connector functions asynchronously and shouldn't block the main thread. The result of the query and insertion will be stored in a blocking queue. This blocking queue waits until the completionHandlers returns a result. This result will be printed afterwards. You may need to replace the input values of the connection if you want to verify this demo. The example can be found [here](example/Example.java).

The example supports a single connection. If you want to support multiple connections at the same time, you can create a Client object. The same methods apply for the Client as for the Connection object. An example of how to create a Client can be found below.

The hostlist is a two dimensional array containing host, port and priority. A higher priority means that there is a higher change that this connection will be chosen. The connection with priority -1 will be always chosen (if it is connected).

```Java
hostlist = new String[][]{{"localhost", "9000", "-1"}, {"localhost", "9001", "5"}, {"localhost", "9002", "1"}, {"localhost", "9003", "2"}};
client = new Client("iris", "siri", "test", hostlist, true);```
