package za.sk.bitconmcp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class BitcoinServiceClient {
     private static final String BASE_URL = "";
     private final RestClient restClient;

     public BitcoinServiceClient(RestClient.Builder restClient) {
          this.restClient = restClient.build();
     }

     public int getBitcoinPrice() {
          return 0;
     }
}
