package za.sk.bitconmcp;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.File;
import java.util.Map;

public class ClientSTDIO {
     public static void main(String[] args) {
          // Get the JAR file path from system property or environment variable, or use default
          String jarPath = System.getProperty("mcp.server.jar.path", 
                  System.getenv().getOrDefault("MCP_SERVER_JAR_PATH", 
                          "target/bitcoinmcp-0.0.1-SNAPSHOT.jar"));

          // Verify the JAR file exists
          File jarFile = new File(jarPath);
          if (!jarFile.exists()) {
               System.err.println("Error: MCP server JAR file not found at: " + jarPath);
               System.err.println("Please specify the correct path using -Dmcp.server.jar.path=<path> or MCP_SERVER_JAR_PATH environment variable");
               System.exit(1);
          }

          System.out.println("Using MCP server JAR: " + jarFile.getAbsolutePath());

          var stdioParams = ServerParameters.builder("java")
                  .args("-jar", jarPath)
                  .build();

          var transport = new StdioClientTransport(stdioParams);
          var client = McpClient.sync(transport).build();
          client.initialize();

          McpSchema.ListToolsResult toolList = client.listTools();
          System.out.println("Tools: "+toolList);

          McpSchema.CallToolResult result = client.callTool(new McpSchema.CallToolRequest("get_bitcoin_price_per_currency", Map.of("currency", "USD")));

          System.out.println("Bitcoin price: "+result);
          client.closeGracefully();
     }
}
