package aurora.supply_wok.platform.restaurantmanagement.acceptance;

import aurora.supply_wok.platform.restaurantmanagement.application.internal.commandservices.ComandaCommandServiceImpl;
import aurora.supply_wok.platform.restaurantmanagement.application.internal.eventhandlers.ComandaTableOccupancyEventHandler;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.CreateComandaCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateComandaStatusCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableStatusCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.ComandaCreatedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.ComandaStatusChangedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.ComandaCommandService;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.EComandaStatus;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.ETableStatus;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.ComandaRepository;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.RestaurantTableRepository;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.UpdateComandaStatusResource;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform.UpdateComandaStatusCommandFromResourceAssembler;
import aurora.supply_wok.platform.shared.domain.exceptions.ResourceNotFoundException;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComandaSteps {

    private final RestaurantTableRepository tableRepository = mock(RestaurantTableRepository.class);
    private final ComandaRepository comandaRepository = mock(ComandaRepository.class);

    private final ComandaCommandService comandaCommandService;
    private final ComandaTableOccupancyEventHandler tableOccupancyHandler;

    private final Map<Long, RestaurantTable> tableStore = new HashMap<>();
    private final Map<Long, Comanda> comandaStore = new HashMap<>();
    private long nextComandaId = 1L;

    private Long createdComandaId;
    private Long currentComandaId;
    private Long currentTableId;
    private Exception lastException;

    public ComandaSteps() {
        ApplicationEventPublisher[] eventPublisherHolder = new ApplicationEventPublisher[1];
        DomainEventPublisher domainEventPublisher = new DomainEventPublisher(event -> {
            if (eventPublisherHolder[0] != null) {
                eventPublisherHolder[0].publishEvent(event);
            }
        });

        this.tableOccupancyHandler = new ComandaTableOccupancyEventHandler(tableRepository, domainEventPublisher);

        eventPublisherHolder[0] = event -> {
            if (event instanceof ComandaCreatedEvent cce) {
                tableOccupancyHandler.on(cce);
            } else if (event instanceof ComandaStatusChangedEvent csce) {
                tableOccupancyHandler.on(csce);
            }
        };

        this.comandaCommandService = new ComandaCommandServiceImpl(comandaRepository, tableRepository, domainEventPublisher);

        when(tableRepository.save(any(RestaurantTable.class))).thenAnswer(inv -> {
            RestaurantTable table = inv.getArgument(0);
            tableStore.put(table.getId(), table);
            return table;
        });

        when(tableRepository.findById(anyLong())).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            return Optional.ofNullable(tableStore.get(id));
        });

        when(tableRepository.findByIdAndRestaurantId(anyLong(), anyLong())).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            Long restaurantId = inv.getArgument(1);
            RestaurantTable table = tableStore.get(id);
            if (table != null && Objects.equals(table.getRestaurantId(), restaurantId)) {
                return Optional.of(table);
            }
            return Optional.empty();
        });

        when(comandaRepository.save(any(Comanda.class))).thenAnswer(inv -> {
            Comanda comanda = inv.getArgument(0);
            if (comanda.getId() == null) {
                ReflectionTestUtils.setField(comanda, "id", nextComandaId++);
            }
            comandaStore.put(comanda.getId(), comanda);
            return comanda;
        });

        when(comandaRepository.findById(anyLong())).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            return Optional.ofNullable(comandaStore.get(id));
        });

        when(comandaRepository.findByIdAndRestaurantId(anyLong(), anyLong())).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            Long restaurantId = inv.getArgument(1);
            Comanda comanda = comandaStore.get(id);
            if (comanda != null && Objects.equals(comanda.getRestaurantId(), restaurantId)) {
                return Optional.of(comanda);
            }
            return Optional.empty();
        });
    }

    @Given("a table with ID {long} exists for restaurant {long} in status {string}")
    public void aTableWithIdExistsForRestaurantInStatus(Long tableId, Long restaurantId, String status) {
        RestaurantTable table = new RestaurantTable(restaurantId, tableId.intValue(), 4);
        ReflectionTestUtils.setField(table, "id", tableId);
        table.updateStatus(new UpdateTableStatusCommand(restaurantId, tableId, ETableStatus.valueOf(status)));
        tableStore.put(tableId, table);
    }

    @When("restaurant {long} creates a comanda on table {long}")
    public void restaurantCreatesAComandaOnTable(Long restaurantId, Long tableId) {
        try {
            createdComandaId = comandaCommandService.handle(new CreateComandaCommand(restaurantId, tableId));
            currentComandaId = createdComandaId;
            currentTableId = tableId;
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the comanda is successfully created with status {string}")
    public void theComandaIsSuccessfullyCreatedWithStatus(String expectedStatus) {
        assertThat(lastException).isNull();
        assertThat(createdComandaId).isNotNull();
        Comanda comanda = comandaStore.get(createdComandaId);
        assertThat(comanda).isNotNull();
        assertThat(comanda.getStatus().name()).isEqualTo(expectedStatus);
    }

    @And("table {long} status is updated to {string}")
    public void tableStatusIsUpdatedTo(Long tableId, String expectedStatus) {
        RestaurantTable table = tableStore.get(tableId);
        assertThat(table).isNotNull();
        assertThat(table.getStatus().name()).isEqualTo(expectedStatus);
    }

    @When("restaurant {long} attempts to create a comanda on table {long}")
    public void restaurantAttemptsToCreateAComandaOnTable(Long restaurantId, Long tableId) {
        try {
            createdComandaId = comandaCommandService.handle(new CreateComandaCommand(restaurantId, tableId));
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the comanda creation is rejected as table not found")
    public void theComandaCreationIsRejectedAsTableNotFound() {
        assertThat(lastException).isInstanceOf(ResourceNotFoundException.class);
        assertThat(lastException.getMessage()).contains("Table with id");
    }

    @Given("an open comanda with ID {long} exists on table {long} for restaurant {long}")
    public void anOpenComandaWithIdExistsOnTableForRestaurant(Long comandaId, Long tableId, Long restaurantId) {
        if (!tableStore.containsKey(tableId)) {
            RestaurantTable table = new RestaurantTable(restaurantId, tableId.intValue(), 4);
            ReflectionTestUtils.setField(table, "id", tableId);
            table.updateStatus(new UpdateTableStatusCommand(restaurantId, tableId, ETableStatus.OCCUPIED));
            tableStore.put(tableId, table);
        }
        Comanda comanda = new Comanda(restaurantId, tableId);
        ReflectionTestUtils.setField(comanda, "id", comandaId);
        comandaStore.put(comandaId, comanda);
        this.currentComandaId = comandaId;
        this.currentTableId = tableId;
    }

    @When("restaurant {long} updates the comanda status to {string}")
    public void restaurantUpdatesTheComandaStatusTo(Long restaurantId, String newStatus) {
        try {
            var command = new UpdateComandaStatusCommand(restaurantId, currentComandaId, EComandaStatus.valueOf(newStatus));
            comandaCommandService.handle(command);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the comanda status is updated to {string}")
    public void theComandaStatusIsUpdatedTo(String expectedStatus) {
        assertThat(lastException).isNull();
        Comanda comanda = comandaStore.get(currentComandaId);
        assertThat(comanda).isNotNull();
        assertThat(comanda.getStatus().name()).isEqualTo(expectedStatus);
    }

    @When("restaurant {long} attempts to update the comanda status using invalid status value {string}")
    public void restaurantAttemptsToUpdateTheComandaStatusUsingInvalidStatusValue(Long restaurantId, String invalidStatus) {
        try {
            var command = UpdateComandaStatusCommandFromResourceAssembler.toCommandFromResource(
                    restaurantId, currentComandaId, new UpdateComandaStatusResource(invalidStatus));
            comandaCommandService.handle(command);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the comanda status update is rejected with an invalid status error")
    public void theComandaStatusUpdateIsRejectedWithAnInvalidStatusError() {
        assertThat(lastException).isInstanceOf(IllegalArgumentException.class);
        assertThat(lastException.getMessage()).containsIgnoringCase("Invalid comanda status");
    }
}
