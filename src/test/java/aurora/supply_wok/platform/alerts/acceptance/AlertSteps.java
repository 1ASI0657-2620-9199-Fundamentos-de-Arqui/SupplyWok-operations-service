package aurora.supply_wok.platform.alerts.acceptance;

import aurora.supply_wok.platform.alerts.application.commandservices.AlertCommandService;
import aurora.supply_wok.platform.alerts.application.internal.commandservices.AlertCommandServiceImpl;
import aurora.supply_wok.platform.alerts.application.internal.eventhandlers.InventoryStockEventHandler;
import aurora.supply_wok.platform.alerts.application.internal.eventhandlers.PurchaseOrderEventHandler;
import aurora.supply_wok.platform.alerts.application.internal.queryservices.AlertQueryServiceImpl;
import aurora.supply_wok.platform.alerts.application.queryservices.AlertQueryService;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.Alert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import aurora.supply_wok.platform.alerts.domain.model.commands.AcknowledgeAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAllRestaurantAlertsQuery;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAllSupplierAlertsQuery;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertStatus;
import aurora.supply_wok.platform.alerts.infrastructure.clients.InventoryServiceClient;
import aurora.supply_wok.platform.alerts.infrastructure.persistence.jpa.repositories.AlertPersistenceRepository;
import aurora.supply_wok.platform.inventory.interfaces.events.SupplyMinimumStockReachedIntegrationEvent;
import aurora.supply_wok.platform.iot.interfaces.acl.IotContextFacade;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderCreatedIntegrationEvent;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlertSteps {

    private final AlertPersistenceRepository alertPersistenceRepository = mock(AlertPersistenceRepository.class);
    private final IotContextFacade iotContextFacade = mock(IotContextFacade.class);
    private final InventoryServiceClient inventoryServiceClient = mock(InventoryServiceClient.class);
    private final DomainEventPublisher domainEventPublisher = mock(DomainEventPublisher.class);

    private final AlertCommandService alertCommandService;
    private final AlertQueryService alertQueryService;
    private final InventoryStockEventHandler inventoryStockEventHandler;
    private final PurchaseOrderEventHandler purchaseOrderEventHandler;

    private final Map<Long, Alert> alertsStore = new HashMap<>();
    private long nextAlertId = 1L;

    private Long currentRestaurantId;
    private String currentSupplierEmail;
    private Alert targetAlert;
    private Optional<Alert> acknowledgeResult;

    public AlertSteps() {
        this.alertCommandService = new AlertCommandServiceImpl(
                alertPersistenceRepository,
                iotContextFacade,
                inventoryServiceClient,
                domainEventPublisher
        );
        this.alertQueryService = new AlertQueryServiceImpl(alertPersistenceRepository);
        this.inventoryStockEventHandler = new InventoryStockEventHandler(alertCommandService);
        this.purchaseOrderEventHandler = new PurchaseOrderEventHandler(alertCommandService);

        when(alertPersistenceRepository.save(any(Alert.class))).thenAnswer(invocation -> {
            Alert alert = invocation.getArgument(0);
            if (alert.getId() == null) {
                ReflectionTestUtils.setField(alert, "id", nextAlertId++);
            }
            alertsStore.put(alert.getId(), alert);
            return alert;
        });

        when(alertPersistenceRepository.findById(anyLong())).thenAnswer(inv ->
                Optional.ofNullable(alertsStore.get(inv.getArgument(0))));

        when(alertPersistenceRepository.findRestaurantAlertByIdAndRestaurantId(anyLong(), anyLong()))
                .thenAnswer(inv -> {
                    Long alertId = inv.getArgument(0);
                    Long restaurantId = inv.getArgument(1);
                    Alert alert = alertsStore.get(alertId);
                    if (alert instanceof RestaurantAlert ra && Objects.equals(ra.getRestaurantId(), restaurantId)) {
                        return Optional.of(ra);
                    }
                    return Optional.empty();
                });

        when(alertPersistenceRepository.findSupplierAlertByIdAndSupplierEmail(anyLong(), anyString()))
                .thenAnswer(inv -> {
                    Long alertId = inv.getArgument(0);
                    String email = inv.getArgument(1);
                    Alert alert = alertsStore.get(alertId);
                    if (alert instanceof SupplierAlert sa && Objects.equals(sa.getSupplierEmail(), email)) {
                        return Optional.of(sa);
                    }
                    return Optional.empty();
                });

        when(alertPersistenceRepository.findAllRestaurantAlertsByRestaurantId(anyLong()))
                .thenAnswer(inv -> {
                    Long restaurantId = inv.getArgument(0);
                    return alertsStore.values().stream()
                            .filter(a -> a instanceof RestaurantAlert ra && Objects.equals(ra.getRestaurantId(), restaurantId))
                            .map(a -> (RestaurantAlert) a)
                            .toList();
                });

        when(alertPersistenceRepository.findAllSupplierAlertsBySupplierEmail(anyString()))
                .thenAnswer(inv -> {
                    String email = inv.getArgument(0);
                    return alertsStore.values().stream()
                            .filter(a -> a instanceof SupplierAlert sa && Objects.equals(sa.getSupplierEmail(), email))
                            .map(a -> (SupplierAlert) a)
                            .toList();
                });
    }

    @Given("a restaurant with ID {long}")
    public void aRestaurantWithId(Long restaurantId) {
        this.currentRestaurantId = restaurantId;
    }

    @When("an inventory minimum stock event occurs for supply {string} with stock {int} and minimum {int} for restaurant {long}")
    public void anInventoryMinimumStockEventOccursForSupplyWithStockAndMinimumForRestaurant(
            String supplyName, int stock, int minimumStock, Long restaurantId) {
        var event = new SupplyMinimumStockReachedIntegrationEvent(100L, supplyName, stock, minimumStock, restaurantId);
        inventoryStockEventHandler.on(event);
    }

    @Then("a HIGH severity alert is created for restaurant {long}")
    public void aHighSeverityAlertIsCreatedForRestaurant(Long restaurantId) {
        var alertOpt = alertsStore.values().stream()
                .filter(a -> a instanceof RestaurantAlert ra && Objects.equals(ra.getRestaurantId(), restaurantId))
                .findFirst();
        assertThat(alertOpt).isPresent();
        targetAlert = alertOpt.get();
        assertThat(targetAlert.getSeverity()).isEqualTo(EAlertSeverity.HIGH);
    }

    @And("the alert is visible to restaurant {long}")
    public void theAlertIsVisibleToRestaurant(Long restaurantId) {
        var alerts = alertQueryService.handle(new GetAllRestaurantAlertsQuery(restaurantId));
        assertThat(alerts).anyMatch(a -> Objects.equals(a.getId(), targetAlert.getId()));
    }

    @And("the alert is not visible to restaurant {long}")
    public void theAlertIsNotVisibleToRestaurant(Long otherRestaurantId) {
        var alerts = alertQueryService.handle(new GetAllRestaurantAlertsQuery(otherRestaurantId));
        assertThat(alerts).noneMatch(a -> Objects.equals(a.getId(), targetAlert.getId()));
    }

    @Given("a supplier with email {string}")
    public void aSupplierWithEmail(String email) {
        this.currentSupplierEmail = email;
    }

    @When("a purchase order created event is received for order {string} from restaurant {string} with priority {string} for supplier {string}")
    public void aPurchaseOrderCreatedEventIsReceivedForOrderFromRestaurantWithPriorityForSupplier(
            String orderCode, String restaurantName, String priority, String supplierEmail) {
        var event = new PurchaseOrderCreatedIntegrationEvent(
                200L,
                orderCode,
                55L,
                "Fresh Foods Ltd",
                restaurantName,
                priority,
                "PENDING",
                currentRestaurantId,
                supplierEmail
        );
        purchaseOrderEventHandler.on(event);
    }

    @Then("a HIGH severity alert is created for supplier {string}")
    public void aHighSeverityAlertIsCreatedForSupplier(String supplierEmail) {
        var alertOpt = alertsStore.values().stream()
                .filter(a -> a instanceof SupplierAlert sa && Objects.equals(sa.getSupplierEmail(), supplierEmail))
                .findFirst();
        assertThat(alertOpt).isPresent();
        targetAlert = alertOpt.get();
        assertThat(targetAlert.getSeverity()).isEqualTo(EAlertSeverity.HIGH);
    }

    @And("the alert is visible to supplier {string}")
    public void theAlertIsVisibleToSupplier(String supplierEmail) {
        var alerts = alertQueryService.handle(new GetAllSupplierAlertsQuery(supplierEmail));
        assertThat(alerts).anyMatch(a -> Objects.equals(a.getId(), targetAlert.getId()));
    }

    @And("the alert is not visible to supplier {string}")
    public void theAlertIsNotVisibleToSupplier(String otherSupplierEmail) {
        var alerts = alertQueryService.handle(new GetAllSupplierAlertsQuery(otherSupplierEmail));
        assertThat(alerts).noneMatch(a -> Objects.equals(a.getId(), targetAlert.getId()));
    }

    @Given("a pending alert exists for restaurant {long}")
    public void aPendingAlertExistsForRestaurant(Long restaurantId) {
        RestaurantAlert alert = new RestaurantAlert(restaurantId, EAlertSeverity.HIGH, "Stock low alert", 0L, "System");
        ReflectionTestUtils.setField(alert, "id", nextAlertId++);
        alertsStore.put(alert.getId(), alert);
        this.targetAlert = alert;
    }

    @When("restaurant {long} acknowledges the alert")
    public void restaurantAcknowledgesTheAlert(Long restaurantId) {
        acknowledgeResult = alertCommandService.handle(new AcknowledgeAlertCommand(targetAlert.getId(), restaurantId, null));
    }

    @Then("the alert status is changed to ACKNOWLEDGED")
    public void theAlertStatusIsChangedToAcknowledged() {
        assertThat(acknowledgeResult).isPresent();
        assertThat(acknowledgeResult.get().getStatus()).isEqualTo(EAlertStatus.ACKNOWLEDGED);
    }

    @When("restaurant {long} attempts to acknowledge the alert of restaurant {long}")
    public void restaurantAttemptsToAcknowledgeTheAlertOfRestaurant(Long callerRestaurantId, Long ownerRestaurantId) {
        acknowledgeResult = alertCommandService.handle(new AcknowledgeAlertCommand(targetAlert.getId(), callerRestaurantId, null));
    }

    @Then("the alert acknowledgment is rejected as not found")
    public void theAlertAcknowledgmentIsRejectedAsNotFound() {
        assertThat(acknowledgeResult).isEmpty();
    }
}
