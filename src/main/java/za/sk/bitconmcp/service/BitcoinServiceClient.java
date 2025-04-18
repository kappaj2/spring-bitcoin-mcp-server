package za.sk.bitconmcp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class BitcoinServiceClient {
     private static final String COINGECKO_API_BASE_URL = "https://api.coingecko.com/api/v3";

     private final RestClient restClient;
     private final ObjectMapper objectMapper;

     public BitcoinServiceClient(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
          this.restClient = restClientBuilder.build();
          this.objectMapper = objectMapper;
     }

     /**
      * Gets the current Bitcoin price in the specified currency
      *
      * @param currency The currency code (e.g., "usd", "eur", "jpy")
      * @return The current Bitcoin price in the specified currency
      * @throws IOException If an I/O error occurs or API returns invalid data
      */
     @Tool(name = "Get Bitcoin price by currency")
     public double getBitcoinPriceByCurrency(String currency) throws IOException {
          String endpoint = "/simple/price";
          String queryParams = "?ids=bitcoin&vs_currencies=" + currency.toLowerCase();

          String responseBody = restClient.get()
                  .uri(COINGECKO_API_BASE_URL + endpoint + queryParams)
                  .retrieve()
                  .body(String.class);

          JsonNode rootNode = objectMapper.readTree(responseBody);
          return rootNode.get("bitcoin").get(currency.toLowerCase()).asDouble();
     }

     /**
      * Gets historical Bitcoin price data for a specified number of days from the current date
      *
      * @param days The number of days to look back from today
      * @param currency The currency code (e.g., "usd", "eur", "jpy")
      * @return Map with dates as keys and price information as values
      * @throws IOException If an I/O error occurs or API returns invalid data
      */
     @Tool(name = "Get BitCoin price for the last n number of days")
     public Map<LocalDate, Map<String, Double>> getHistoricalBitcoinPrice(int days, String currency) throws IOException {
          // Use CoinGecko's market chart endpoint which gives historical data by days
          String endpoint = "/coins/bitcoin/market_chart";
          String queryParams = "?vs_currency=" + currency.toLowerCase() + "&days=" + days + "&interval=daily";

          String responseBody = restClient.get()
                  .uri(COINGECKO_API_BASE_URL + endpoint + queryParams)
                  .retrieve()
                  .body(String.class);

          JsonNode rootNode = objectMapper.readTree(responseBody);
          JsonNode pricesNode = rootNode.get("prices");
          JsonNode marketCapsNode = rootNode.get("market_caps");
          JsonNode volumesNode = rootNode.get("total_volumes");

          Map<LocalDate, Map<String, Double>> historicalData = new LinkedHashMap<>();
          LocalDateTime now = LocalDateTime.now();

          // Process data for each day
          for (int i = 0; i < Math.min(days, pricesNode.size()); i++) {
               // Calculate the date (now - (days-i))
               LocalDate date = now.minusDays(days - i).toLocalDate();

               // Extract price data for this day
               double price = pricesNode.get(i).get(1).asDouble();
               double marketCap = marketCapsNode.get(i).get(1).asDouble();
               double volume = volumesNode.get(i).get(1).asDouble();

               // Store in the result map
               Map<String, Double> dayData = new HashMap<>();
               dayData.put("price", price);
               dayData.put("market_cap", marketCap);
               dayData.put("total_volume", volume);

               historicalData.put(date, dayData);
          }

          return historicalData;
     }

     /**
      * Alternative implementation that makes multiple calls to the history endpoint
      * for cases where the market_chart endpoint doesn't provide needed data structure
      */
     public Map<LocalDate, Map<String, Double>> getHistoricalBitcoinPriceByDailyFetch(int days, String currency) throws IOException {
          Map<LocalDate, Map<String, Double>> historicalData = new LinkedHashMap<>();
          LocalDateTime now = LocalDateTime.now();
          currency = currency.toLowerCase();

          // Fetch data for each day
          for (int i = 0; i < days; i++) {
               LocalDate date = now.minusDays(i).toLocalDate();
               String dateFormatted = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

               String endpoint = "/coins/bitcoin/history";
               String queryParams = "?date=" + dateFormatted;

               String responseBody = restClient.get()
                       .uri(COINGECKO_API_BASE_URL + endpoint + queryParams)
                       .retrieve()
                       .body(String.class);

               JsonNode rootNode = objectMapper.readTree(responseBody);

               // Check if we have market data for this date
               if (rootNode.has("market_data")) {
                    JsonNode marketData = rootNode.get("market_data");

                    Map<String, Double> dayData = new HashMap<>();
                    dayData.put("price", marketData.get("current_price").get(currency).asDouble());
                    dayData.put("market_cap", marketData.get("market_cap").get(currency).asDouble());
                    dayData.put("total_volume", marketData.get("total_volume").get(currency).asDouble());

                    historicalData.put(date, dayData);
               }
          }

          return historicalData;
     }
}
