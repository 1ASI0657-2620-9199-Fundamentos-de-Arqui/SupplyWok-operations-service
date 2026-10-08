package aurora.supply_wok.platform.alerts.interfaces.rest;

import aurora.supply_wok.platform.alerts.application.commandservices.AlertCommandService;
import aurora.supply_wok.platform.alerts.application.queryservices.AlertQueryService;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import aurora.supply_wok.platform.alerts.domain.model.commands.AcknowledgeAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAlertByIdQuery;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAllSupplierAlertsQuery;
import aurora.supply_wok.platform.alerts.interfaces.rest.resources.SupplierAlertResource;
import aurora.supply_wok.platform.alerts.interfaces.rest.transform.AlertResourceFromEntityAssembler;
import aurora.supply_wok.platform.shared.domain.exceptions.ResourceNotFoundException;
import aurora.supply_wok.platform.shared.infrastructure.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/supplier/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Supplier Alerts", description = "Supplier Alerts management endpoints")
@PreAuthorize("hasRole('SUPPLIER')")
public class SupplierAlertsController {

    private final AlertCommandService alertCommandService;
    private final AlertQueryService alertQueryService;
    private final CurrentUserProvider currentUserProvider;

    public SupplierAlertsController(AlertCommandService alertCommandService,
                                    AlertQueryService alertQueryService,
                                    CurrentUserProvider currentUserProvider) {
        this.alertCommandService = alertCommandService;
        this.alertQueryService = alertQueryService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/{alertId}")
    @PreAuthorize("hasRole('SUPPLIER')")
    @Operation(summary = "Get supplier alert by ID", description = "Retrieves a specific supplier alert.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier alert found", content = @Content(schema = @Schema(implementation = SupplierAlertResource.class))),
        @ApiResponse(responseCode = "404", description = "Supplier alert not found")
    })
    public ResponseEntity<SupplierAlertResource> getSupplierAlertById(
            @PathVariable @Parameter(description = "Supplier Alert ID", example = "1", required = true) Long alertId
    ) {
        String supplierEmail = currentUserProvider.email();
        var query = new GetAlertByIdQuery(alertId, supplierEmail);
        var alert = alertQueryService.handle(query);

        if (alert.isEmpty() || !(alert.get() instanceof SupplierAlert)) {
            return ResponseEntity.notFound().build();
        }

        var alertResource = AlertResourceFromEntityAssembler.toSupplierResourceFromEntity((SupplierAlert) alert.get());
        return ResponseEntity.ok(alertResource);
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPPLIER')")
    @Operation(summary = "Get all supplier alerts", description = "Retrieves a list of all registered supplier alerts.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier alerts retrieved successfully")
    })
    public ResponseEntity<List<SupplierAlertResource>> getAllSupplierAlerts() {
        String supplierEmail = currentUserProvider.email();
        var alerts = alertQueryService.handle(new GetAllSupplierAlertsQuery(supplierEmail));
        if (alerts.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        var resources = alerts.stream()
                .map(alert -> AlertResourceFromEntityAssembler.toSupplierResourceFromEntity((SupplierAlert) alert))
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{alertId}")
    @PreAuthorize("hasRole('SUPPLIER')")
    @Operation(summary = "Acknowledge supplier alert", description = "Acknowledges a supplier alert by setting its status to Acknowledged.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier alert acknowledged successfully", content = @Content(schema = @Schema(implementation = SupplierAlertResource.class))),
        @ApiResponse(responseCode = "404", description = "Supplier alert not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<SupplierAlertResource> acknowledgeSupplierAlert(
            @PathVariable @Parameter(description = "Supplier Alert ID", example = "1", required = true) Long alertId
    ) {
        String supplierEmail = currentUserProvider.email();
        try {
            var acknowledged = alertCommandService.handle(new AcknowledgeAlertCommand(alertId, supplierEmail));
            if (acknowledged.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(AlertResourceFromEntityAssembler.toSupplierResourceFromEntity((SupplierAlert) acknowledged.get()));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
