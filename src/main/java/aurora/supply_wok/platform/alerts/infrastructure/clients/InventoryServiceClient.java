package aurora.supply_wok.platform.alerts.infrastructure.clients;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.List;

/**
 * Synchronous client to retrieve inventory data from inventory-service.
 */
@Service
@Slf4j
public class InventoryServiceClient {

    public record SupplyItemResponse(
            Long id,
            String name,
            String unitOfMeasure,
            int currentStock,
            int minimumStockLevel,
            String category
    ) {}

    private final RestClient restClient;
    private final String inventoryServiceUrl;

    @Autowired
    public InventoryServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${inventory.service.url:${INVENTORY_SERVICE_URL:http://localhost:8082}}") String inventoryServiceUrl
    ) {
        this.inventoryServiceUrl = inventoryServiceUrl;
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(2));

        this.restClient = restClientBuilder
                .baseUrl(inventoryServiceUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public long getTotalSupplyStock() {
        try {
            var request = restClient.get()
                    .uri("/api/v1/supplies");

            String authHeader = getAuthorizationHeader();
            if (authHeader != null && !authHeader.isBlank()) {
                request.header(HttpHeaders.AUTHORIZATION, authHeader);
            }

            List<SupplyItemResponse> supplies = request
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<SupplyItemResponse>>() {});

            if (supplies == null || supplies.isEmpty()) {
                return 0L;
            }

            return supplies.stream()
                    .mapToLong(SupplyItemResponse::currentStock)
                    .sum();
        } catch (Exception ex) {
            log.error("Failed to retrieve supplies from inventory service at {}: {}", inventoryServiceUrl, ex.getMessage());
            throw new InventoryServiceUnavailableException("Inventory service unavailable", ex);
        }
    }

    private String getAuthorizationHeader() {
        try {
            var requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
                HttpServletRequest request = servletRequestAttributes.getRequest();
                return request.getHeader(HttpHeaders.AUTHORIZATION);
            }
        } catch (Exception ex) {
            log.debug("No active request attributes found: {}", ex.getMessage());
        }
        return null;
    }
}
