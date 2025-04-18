package za.sk.bitconmcp.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import za.sk.bitconmcp.service.BitcoinServiceClient;

@Configuration
public class MCPConfiguration {

     @Bean
     public ToolCallbackProvider bitCoinTools(BitcoinServiceClient bitcoinServiceClient){
          return MethodToolCallbackProvider.builder()
                  .toolObjects(bitcoinServiceClient)
                  .build();
     }
}
