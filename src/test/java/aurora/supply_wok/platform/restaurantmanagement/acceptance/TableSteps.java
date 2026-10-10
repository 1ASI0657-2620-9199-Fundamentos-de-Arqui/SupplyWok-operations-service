package aurora.supply_wok.platform.restaurantmanagement.acceptance;

import aurora.supply_wok.platform.restaurantmanagement.application.internal.commandservices.TableCommandServiceImpl;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.DeleteTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableStatusCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.TableCommandService;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.ETableStatus;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.RestaurantTableRepository;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.UpdateTableStatusResource;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform.UpdateTableStatusCommandFromResourceAssembler;
import aurora.supply_wok.platform.shared.domain.exceptions.ResourceNotFoundException;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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

public class TableSteps {

    private final RestaurantTableRepository tableRepository = mock(RestaurantTableRepository.class);
    private final DomainEventPublisher domainEventPublisher = mock(DomainEventPublisher.class);
    private final TableCommandService tableCommandService = new TableCommandServiceImpl(tableRepository, domainEventPublisher);

    private final Map<Long, RestaurantTable> tableStore = new HashMap<>();
    private Exception lastException;

    public TableSteps() {
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

        when(tableRepository.existsByIdAndRestaurantId(anyLong(), anyLong())).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            Long restaurantId = inv.getArgument(1);
            RestaurantTable table = tableStore.get(id);
            return table != null && Objects.equals(table.getRestaurantId(), restaurantId);
        });

        when(tableRepository.existsById(anyLong())).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            return tableStore.containsKey(id);
        });
    }

    @Given("a restaurant table with ID {long} exists for restaurant {long} in status {string}")
    public void aRestaurantTableWithIdExistsForRestaurantInStatus(Long tableId, Long restaurantId, String status) {
        RestaurantTable table = new RestaurantTable(restaurantId, tableId.intValue(), 4);
        ReflectionTestUtils.setField(table, "id", tableId);
        table.updateStatus(new UpdateTableStatusCommand(restaurantId, tableId, ETableStatus.valueOf(status)));
        tableStore.put(tableId, table);
    }

    @Given("a restaurant table with ID {long} exists for restaurant {long}")
    public void aRestaurantTableWithIdExistsForRestaurant(Long tableId, Long restaurantId) {
        RestaurantTable table = new RestaurantTable(restaurantId, tableId.intValue(), 4);
        ReflectionTestUtils.setField(table, "id", tableId);
        tableStore.put(tableId, table);
    }

    @When("restaurant {long} updates the status of restaurant table {long} to {string}")
    public void restaurantUpdatesTheStatusOfRestaurantTableTo(Long restaurantId, Long tableId, String newStatus) {
        try {
            var command = new UpdateTableStatusCommand(restaurantId, tableId, ETableStatus.valueOf(newStatus));
            tableCommandService.handle(command);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("restaurant table {long} status is updated to {string}")
    public void restaurantTableStatusIsUpdatedTo(Long tableId, String expectedStatus) {
        assertThat(lastException).isNull();
        RestaurantTable table = tableStore.get(tableId);
        assertThat(table).isNotNull();
        assertThat(table.getStatus().name()).isEqualTo(expectedStatus);
    }

    @When("restaurant {long} attempts to update the status of restaurant table {long} to {string}")
    public void restaurantAttemptsToUpdateTheStatusOfRestaurantTableTo(Long restaurantId, Long tableId, String newStatus) {
        try {
            var command = new UpdateTableStatusCommand(restaurantId, tableId, ETableStatus.valueOf(newStatus));
            tableCommandService.handle(command);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the table status update is rejected as table not found")
    public void theTableStatusUpdateIsRejectedAsTableNotFound() {
        assertThat(lastException).isInstanceOf(ResourceNotFoundException.class);
        assertThat(lastException.getMessage()).contains("Table with id");
    }

    @When("restaurant {long} attempts to delete restaurant table {long}")
    public void restaurantAttemptsToDeleteRestaurantTable(Long restaurantId, Long tableId) {
        try {
            tableCommandService.handle(new DeleteTableCommand(restaurantId, tableId));
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the table deletion is rejected as table not found")
    public void theTableDeletionIsRejectedAsTableNotFound() {
        assertThat(lastException).isInstanceOf(ResourceNotFoundException.class);
        assertThat(lastException.getMessage()).contains("Table with id");
    }

    @When("restaurant {long} attempts to update restaurant table {long} status using invalid status value {string}")
    public void restaurantAttemptsToUpdateRestaurantTableStatusUsingInvalidStatusValue(
            Long restaurantId, Long tableId, String invalidStatus) {
        try {
            var command = UpdateTableStatusCommandFromResourceAssembler.toCommandFromResource(
                    restaurantId, tableId, new UpdateTableStatusResource(invalidStatus));
            tableCommandService.handle(command);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the table status update is rejected with an invalid status error")
    public void theTableStatusUpdateIsRejectedWithAnInvalidStatusError() {
        assertThat(lastException).isInstanceOf(IllegalArgumentException.class);
    }
}
