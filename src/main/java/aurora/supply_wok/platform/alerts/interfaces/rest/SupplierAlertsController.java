package aurora.supply_wok.platform.alerts.interfaces.rest;

import aurora.supply_wok.platform.alerts.application.commandservices.AlertCommandService;
import aurora.supply_wok.platform.alerts.application.queryservices.AlertQueryService;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAlertByIdQuery;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAllSupplierAlertsQuery;
import aurora.supply_wok.platform.alerts.interfaces.rest.resources.SupplierAlertResource;
import aurora.supply_wok.platform.alerts.interfaces.rest.transform.AlertResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/supplier/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Supplier Alerts", description = "Supplier Alerts management endpoints")
public class SupplierAlertsController {

    private final AlertCommandService alertCommandService;
    private final AlertQueryService alertQueryService;

    public SupplierAlertsController(AlertCommandService alertCommandService, AlertQueryService alertQueryService) {
        this.alertCommandService = alertCommandService;
        this.alertQueryService = alertQueryService;
    }

    @GetMapping("/{alertId}")
    @Operation(summary = "Get supplier alert by ID", description = "Retrieves a specific supplier alert.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier alert found", content = @Content(schema = @Schema(implementation = SupplierAlertResource.class))),
        @ApiResponse(responseCode = "404", description = "Supplier alert not found")
    })
    public ResponseEntity<SupplierAlertResource> getSupplierAlertById(
            @PathVariable @Parameter(description = "Supplier Alert ID", example = "1", required = true) Long alertId
    ) {
        var query = new GetAlertByIdQuery(alertId);
        var alert = alertQueryService.handle(query);

        if (alert.isEmpty() || !(alert.get() instanceof SupplierAlert)) {
            return ResponseEntity.notFound().build();
        }

        var alertResource = AlertResourceFromEntityAssembler.toSupplierResourceFromEntity((SupplierAlert) alert.get());
        return ResponseEntity.ok(alertResource);
    }

    @GetMapping
    @Operation(summary = "Get all supplier alerts", description = "Retrieves a list of all registered supplier alerts.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier alerts retrieved successfully")
    })
    public ResponseEntity<List<SupplierAlertResource>> getAllSupplierAlerts() {
        var alerts = alertQueryService.handle(new GetAllSupplierAlertsQuery());
        if (alerts.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        var resources = alerts.stream()
                .map(alert -> AlertResourceFromEntityAssembler.toSupplierResourceFromEntity((SupplierAlert) alert))
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{alertId}")
    @Operation(summary = "Acknowledge supplier alert", description = "Acknowledges a supplier alert by setting its status to Acknowledged.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier alert acknowledged successfully", content = @Content(schema = @Schema(implementation = SupplierAlertResource.class))),
        @ApiResponse(responseCode = "404", description = "Supplier alert not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<SupplierAlertResource> acknowledgeSupplierAlert(
            @PathVariable @Parameter(description = "Supplier Alert ID", example = "1", required = true) Long alertId
    ) {
        var currentAlert = alertQueryService.handle(new GetAlertByIdQuery(alertId));
        if (currentAlert.isEmpty() || !(currentAlert.get() instanceof SupplierAlert)) {
            return ResponseEntity.notFound().build();
        }

        try {
            var acknowledged = alertCommandService.handle(new aurora.supply_wok.platform.alerts.domain.model.commands.AcknowledgeAlertCommand(alertId));
            if (acknowledged.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(AlertResourceFromEntityAssembler.toSupplierResourceFromEntity((SupplierAlert) acknowledged.get()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
