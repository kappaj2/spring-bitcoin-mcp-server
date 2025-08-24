# Bitcoin MCP Server

This project provides a Model Context Protocol (MCP) server for Bitcoin price information.

## Building the Project

To build the project, run:

```bash
mvn clean package
```

This will create a JAR file at `target/bitcoinmcp-0.0.1-SNAPSHOT.jar`.

## Running the MCP Server

The server is configured to use standard input/output (STDIO) for communication, as specified in the `application.yaml` file.

## Using the ClientSTDIO Test Client

The project includes a test client (`ClientSTDIO`) that demonstrates how to connect to the MCP server.

### Why ClientSTDIO Needs to See the Server JAR

The ClientSTDIO class uses the Model Context Protocol (MCP) client library to communicate with the MCP server. It needs to reference the server JAR file because:

1. The MCP client uses a subprocess mechanism to start the server
2. It launches the server by executing the JAR file as a separate Java process
3. The client and server communicate via standard input/output (STDIO)

This architecture allows the client to interact with the server without requiring the server to be started separately or to expose network ports. However, it means the client needs to know the location of the server JAR file to start it.

### Configuring the JAR File Path

The client needs to know the location of the server JAR file. There are three ways to specify this:

1. **System Property**:
   ```bash
   java -Dmcp.server.jar.path=/path/to/bitcoinmcp-0.0.1-SNAPSHOT.jar -cp target/test-classes za.sk.bitconmcp.ClientSTDIO
   ```

2. **Environment Variable**:
   ```bash
   export MCP_SERVER_JAR_PATH=/path/to/bitcoinmcp-0.0.1-SNAPSHOT.jar
   java -cp target/test-classes za.sk.bitconmcp.ClientSTDIO
   ```

3. **Default Path**:
   If neither the system property nor the environment variable is set, the client will look for the JAR file at `target/bitcoinmcp-0.0.1-SNAPSHOT.jar` relative to the current working directory.

### Running the Client

To run the client, use Maven's exec plugin to ensure all dependencies are included in the classpath:

```bash
# Make sure you're in the project root directory
cd /opt/mcp/server/bitcoinmcp

# Run the client using Maven
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass="za.sk.bitconmcp.ClientSTDIO"
```

You can also specify the JAR path as a system property:

```bash
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass="za.sk.bitconmcp.ClientSTDIO" -Dmcp.server.jar.path=/path/to/bitcoinmcp-0.0.1-SNAPSHOT.jar
```

The client will:
1. Connect to the MCP server
2. List available tools
3. Call the `getBitcoinProceByCurrency` tool with USD as the currency
4. Display the result
5. Close the connection

## Troubleshooting

If you see an error like "MCP server JAR file not found", make sure:
1. You've built the project with `mvn clean package`
2. You're running the client from the correct directory
3. The JAR file path is correctly specified

You can also specify the absolute path to the JAR file using one of the configuration methods described above.
