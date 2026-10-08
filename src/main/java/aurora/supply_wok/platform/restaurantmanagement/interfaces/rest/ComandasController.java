package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.DeleteComandaCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetAllComandasQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetComandaByIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetComandasByTableIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.ComandaCommandService;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.ComandaQueryService;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.AddComandaItemResource;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.ComandaResource;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.CreateComandaResource;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.UpdateComandaStatusResource;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform.AddComandaItemCommandFromResourceAssembler;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform.ComandaResourceFromEntityAssembler;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform.CreateComandaCommandFromResourceAssembler;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform.UpdateComandaStatusCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/comandas", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Comandas", description = "Comanda management endpoints")
@PreAuthorize("hasRole('RESTAURANT')")
public class ComandasController {

    private final ComandaCommandService comandaCommandService;
    private final ComandaQueryService comandaQueryService;
    private final CurrentUserProvider currentUserProvider;

    public ComandasController(ComandaCommandService comandaCommandService,
                              ComandaQueryService comandaQueryService,
                              CurrentUserProvider currentUserProvider) {
        this.comandaCommandService = comandaCommandService;
        this.comandaQueryService = comandaQueryService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Create a new comanda", description = "Creates a new comanda assigned to a table.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comanda created successfully",
                    content = @Content(schema = @Schema(implementation = ComandaResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Table not found")
    })
    public ResponseEntity<ComandaResource> createComanda(@Valid @RequestBody CreateComandaResource resource) {
        Long restaurantId = currentUserProvider.userId();
        var command = CreateComandaCommandFromResourceAssembler.toCommandFromResource(restaurantId, resource);
        var comandaId = comandaCommandService.handle(command);

        var comanda = comandaQueryService.handle(new GetComandaByIdQuery(restaurantId, comandaId));
        if (comanda.isEmpty()) return ResponseEntity.badRequest().build();

        var comandaResource = ComandaResourceFromEntityAssembler.toResourceFromEntity(comanda.get());
        return new ResponseEntity<>(comandaResource, HttpStatus.CREATED);
    }

    @GetMapping("/{comandaId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Get comanda by ID", description = "Retrieves a specific comanda by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comanda found",
                    content = @Content(schema = @Schema(implementation = ComandaResource.class))),
            @ApiResponse(responseCode = "404", description = "Comanda not found")
    })
    public ResponseEntity<ComandaResource> getComandaById(
            @PathVariable @Parameter(description = "Comanda unique identifier", example = "1", required = true) Long comandaId) {
        Long restaurantId = currentUserProvider.userId();
        var comanda = comandaQueryService.handle(new GetComandaByIdQuery(restaurantId, comandaId));
        if (comanda.isEmpty()) return ResponseEntity.notFound().build();

        var comandaResource = ComandaResourceFromEntityAssembler.toResourceFromEntity(comanda.get());
        return ResponseEntity.ok(comandaResource);
    }

    @GetMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Get all comandas", description = "Retrieves a list of all comandas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comandas list retrieved",
                    content = @Content(schema = @Schema(implementation = ComandaResource.class)))
    })
    public ResponseEntity<List<ComandaResource>> getAllComandas() {
        Long restaurantId = currentUserProvider.userId();
        var comandas = comandaQueryService.handle(new GetAllComandasQuery(restaurantId));
        if (comandas.isEmpty()) return ResponseEntity.ok(Collections.emptyList());

        var comandaResources = comandas.stream()
                .map(ComandaResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(comandaResources);
    }

    @GetMapping("/table/{tableId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Get comandas by table", description = "Retrieves all comandas for a specific table.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comandas for table retrieved",
                    content = @Content(schema = @Schema(implementation = ComandaResource.class))),
            @ApiResponse(responseCode = "404", description = "Table not found")
    })
    public ResponseEntity<List<ComandaResource>> getComandasByTableId(
            @PathVariable @Parameter(description = "Table unique identifier", example = "1", required = true) Long tableId) {
        Long restaurantId = currentUserProvider.userId();
        var comandas = comandaQueryService.handle(new GetComandasByTableIdQuery(restaurantId, tableId));
        if (comandas.isEmpty()) return ResponseEntity.ok(Collections.emptyList());

        var comandaResources = comandas.stream()
                .map(ComandaResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(comandaResources);
    }

    @PutMapping("/{comandaId}/status")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Update comanda status", description = "Updates the status of a comanda.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comanda status updated",
                    content = @Content(schema = @Schema(implementation = ComandaResource.class))),
            @ApiResponse(responseCode = "404", description = "Comanda not found")
    })
    public ResponseEntity<ComandaResource> updateComandaStatus(
            @PathVariable @Parameter(description = "Comanda unique identifier", example = "1", required = true) Long comandaId,
            @Valid @RequestBody UpdateComandaStatusResource resource) {
        Long restaurantId = currentUserProvider.userId();
        var command = UpdateComandaStatusCommandFromResourceAssembler.toCommandFromResource(restaurantId, comandaId, resource);
        var comanda = comandaCommandService.handle(command);
        if (comanda.isEmpty()) return ResponseEntity.notFound().build();

        var comandaResource = ComandaResourceFromEntityAssembler.toResourceFromEntity(comanda.get());
        return ResponseEntity.ok(comandaResource);
    }

    @PostMapping("/{comandaId}/items")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Add item to comanda", description = "Adds a new item to an existing comanda.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added to comanda",
                    content = @Content(schema = @Schema(implementation = ComandaResource.class))),
            @ApiResponse(responseCode = "404", description = "Comanda not found")
    })
    public ResponseEntity<ComandaResource> addComandaItem(
            @PathVariable @Parameter(description = "Comanda unique identifier", example = "1", required = true) Long comandaId,
            @Valid @RequestBody AddComandaItemResource resource) {
        Long restaurantId = currentUserProvider.userId();
        var command = AddComandaItemCommandFromResourceAssembler.toCommandFromResource(restaurantId, comandaId, resource);
        var comanda = comandaCommandService.handle(command);
        if (comanda.isEmpty()) return ResponseEntity.notFound().build();

        var comandaResource = ComandaResourceFromEntityAssembler.toResourceFromEntity(comanda.get());
        return new ResponseEntity<>(comandaResource, HttpStatus.CREATED);
    }

    @DeleteMapping("/{comandaId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Delete a comanda", description = "Deletes a comanda by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comanda deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Comanda not found")
    })
    public ResponseEntity<Void> deleteComanda(
            @PathVariable @Parameter(description = "Comanda unique identifier", example = "1", required = true) Long comandaId) {
        Long restaurantId = currentUserProvider.userId();
        comandaCommandService.handle(new DeleteComandaCommand(restaurantId, comandaId));
        return ResponseEntity.ok().build();
    }
}
