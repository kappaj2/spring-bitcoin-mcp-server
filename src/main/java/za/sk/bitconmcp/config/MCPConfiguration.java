package za.sk.bitconmcp.config;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import za.sk.bitconmcp.service.BitcoinServiceClient;

import java.util.List;

@Configuration
public class MCPConfiguration {

    //     @Bean
//     public ToolCallbackProvider bitCoinTools(BitcoinServiceClient bitcoinServiceClient){
//          return MethodToolCallbackProvider.builder()
//                  .toolObjects(bitcoinServiceClient)
//                  .build();
//     }
    @Bean
    public List<ToolCallback> bitcointToolsToolCallback(BitcoinServiceClient bitcoinServiceClient) {
        return List.of(ToolCallbacks.from(bitcoinServiceClient));
    }
}
