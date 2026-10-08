package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.DeleteTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetAllTablesQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetTableByIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.TableCommandService;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.TableQueryService;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.CreateTableResource;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.TableResource;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.UpdateTableResource;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.UpdateTableStatusResource;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform.CreateTableCommandFromResourceAssembler;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform.TableResourceFromEntityAssembler;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform.UpdateTableCommandFromResourceAssembler;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform.UpdateTableStatusCommandFromResourceAssembler;
import aurora.supply_wok.platform.shared.infrastructure.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "*", methods = { RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(value = "/api/v1/tables", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Tables", description = "Restaurant table management endpoints")
@PreAuthorize("hasRole('RESTAURANT')")
public class TablesController {

    private final TableCommandService tableCommandService;
    private final TableQueryService tableQueryService;
    private final CurrentUserProvider currentUserProvider;

    public TablesController(TableCommandService tableCommandService,
                            TableQueryService tableQueryService,
                            CurrentUserProvider currentUserProvider) {
        this.tableCommandService = tableCommandService;
        this.tableQueryService = tableQueryService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Create a new table", description = "Creates a new restaurant table.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Table created successfully",
                    content = @Content(schema = @Schema(implementation = TableResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<TableResource> createTable(@Valid @RequestBody CreateTableResource resource) {
        Long restaurantId = currentUserProvider.userId();
        var command = CreateTableCommandFromResourceAssembler.toCommandFromResource(restaurantId, resource);
        var tableId = tableCommandService.handle(command);

        var table = tableQueryService.handle(new GetTableByIdQuery(restaurantId, tableId));
        if (table.isEmpty()) return ResponseEntity.badRequest().build();

        var tableResource = TableResourceFromEntityAssembler.toResourceFromEntity(table.get());
        return new ResponseEntity<>(tableResource, HttpStatus.CREATED);
    }

    @GetMapping("/{tableId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Get table by ID", description = "Retrieves a specific table by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Table found",
                    content = @Content(schema = @Schema(implementation = TableResource.class))),
            @ApiResponse(responseCode = "404", description = "Table not found")
    })
    public ResponseEntity<TableResource> getTableById(
            @PathVariable @Parameter(description = "Table unique identifier", example = "1", required = true) Long tableId) {
        Long restaurantId = currentUserProvider.userId();
        var table = tableQueryService.handle(new GetTableByIdQuery(restaurantId, tableId));
        if (table.isEmpty()) return ResponseEntity.notFound().build();

        var tableResource = TableResourceFromEntityAssembler.toResourceFromEntity(table.get());
        return ResponseEntity.ok(tableResource);
    }

    @GetMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Get all tables", description = "Retrieves a list of all restaurant tables.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tables list retrieved",
                    content = @Content(schema = @Schema(implementation = TableResource.class)))
    })
    public ResponseEntity<List<TableResource>> getAllTables() {
        Long restaurantId = currentUserProvider.userId();
        var tables = tableQueryService.handle(new GetAllTablesQuery(restaurantId));
        if (tables.isEmpty()) return ResponseEntity.ok(Collections.emptyList());

        var tableResources = tables.stream()
                .map(TableResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(tableResources);
    }

    @PutMapping("/{tableId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Update table details", description = "Updates the number and capacity of a restaurant table.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Table updated successfully",
                    content = @Content(schema = @Schema(implementation = TableResource.class))),
            @ApiResponse(responseCode = "404", description = "Table not found")
    })
    public ResponseEntity<TableResource> updateTable(
            @PathVariable @Parameter(description = "Table unique identifier", example = "1", required = true) Long tableId,
            @Valid @RequestBody UpdateTableResource resource) {
        Long restaurantId = currentUserProvider.userId();
        var command = UpdateTableCommandFromResourceAssembler.toCommandFromResource(restaurantId, tableId, resource);
        var table = tableCommandService.handle(command);
        if (table.isEmpty()) return ResponseEntity.notFound().build();

        var tableResource = TableResourceFromEntityAssembler.toResourceFromEntity(table.get());
        return ResponseEntity.ok(tableResource);
    }

    @PutMapping("/{tableId}/status")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Update table status", description = "Updates the status of a restaurant table.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Table status updated",
                    content = @Content(schema = @Schema(implementation = TableResource.class))),
            @ApiResponse(responseCode = "404", description = "Table not found")
    })
    public ResponseEntity<TableResource> updateTableStatus(
            @PathVariable @Parameter(description = "Table unique identifier", example = "1", required = true) Long tableId,
            @Valid @RequestBody UpdateTableStatusResource resource) {
        Long restaurantId = currentUserProvider.userId();
        var command = UpdateTableStatusCommandFromResourceAssembler.toCommandFromResource(restaurantId, tableId, resource);
        var table = tableCommandService.handle(command);
        if (table.isEmpty()) return ResponseEntity.notFound().build();

        var tableResource = TableResourceFromEntityAssembler.toResourceFromEntity(table.get());
        return ResponseEntity.ok(tableResource);
    }

    @DeleteMapping("/{tableId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Delete a table", description = "Deletes a restaurant table by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Table deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Table not found")
    })
    public ResponseEntity<Void> deleteTable(
            @PathVariable @Parameter(description = "Table unique identifier", example = "1", required = true) Long tableId) {
        Long restaurantId = currentUserProvider.userId();
        tableCommandService.handle(new DeleteTableCommand(restaurantId, tableId));
        return ResponseEntity.ok().build();
    }
}
