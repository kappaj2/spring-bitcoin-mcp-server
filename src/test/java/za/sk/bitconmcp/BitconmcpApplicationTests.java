package za.sk.bitconmcp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.sk.bitconmcp.service.BitcoinServiceClient;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

@SpringBootTest
class BitconmcpApplicationTests {

     @Autowired
     private BitcoinServiceClient bitcoinServiceClient;

     @Test
     void contextLoads() {
     }

     @Test
     void testBitcoinCall() throws Exception {
          var respo = bitcoinServiceClient.getBitcoinPriceByCurrency("USD");
          System.out.println("Price: " + respo);
     }

     @Test
     void testBitcoinHistory() throws Exception {
          var resp = bitcoinServiceClient.getHistoricalBitcoinPrice(7, "USD");
          System.out.println(resp);
     }

     @Test
     void testClientSTDIO() throws Exception {
          // Redirect System.out to capture output
          ByteArrayOutputStream outContent = new ByteArrayOutputStream();
          PrintStream originalOut = System.out;
          System.setOut(new PrintStream(outContent));

          try {
               // Set system property for jar path to use the one in target directory
               System.setProperty("mcp.server.jar.path", "target/bitcoinmcp-0.0.1-SNAPSHOT.jar");

               // Run the ClientSTDIO main method
               ClientSTDIO.main(new String[]{});

               // Get the output
               String output = outContent.toString();
               System.setOut(originalOut);

               // Print the output for debugging
               System.out.println("[DEBUG_LOG] ClientSTDIO output: " + output);

               // Assert that the output contains expected text indicating successful call
               assert(output.contains("Tools:"));
               assert(output.contains("Bitcoin price:"));
               assert(!output.contains("Error:"));
          } finally {
               // Restore original System.out
               System.setOut(originalOut);
          }
     }
}
