package aurora.supply_wok.platform.alerts.interfaces.rest;

import aurora.supply_wok.platform.alerts.application.commandservices.AlertCommandService;
import aurora.supply_wok.platform.alerts.application.queryservices.AlertQueryService;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateAlertRestaurantFromInventoryCommand;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAlertByIdQuery;
import aurora.supply_wok.platform.alerts.domain.model.queries.GetAllRestaurantAlertsQuery;
import aurora.supply_wok.platform.alerts.interfaces.rest.resources.RestaurantAlertResource;
import aurora.supply_wok.platform.alerts.interfaces.rest.transform.AlertResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/restaurant/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Restaurant Alerts", description = "Restaurant Alerts management endpoints")
public class RestaurantAlertsController {

    private final AlertCommandService alertCommandService;
    private final AlertQueryService alertQueryService;

    public RestaurantAlertsController(AlertCommandService alertCommandService, AlertQueryService alertQueryService) {
        this.alertCommandService = alertCommandService;
        this.alertQueryService = alertQueryService;
    }

    @PostMapping
    @Operation(summary = "Check inventory and create restaurant alert", description = "Checks inventory stock against sensor and creates an alert if different.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Restaurant alert created", content = @Content(schema = @Schema(implementation = RestaurantAlertResource.class))),
        @ApiResponse(responseCode = "204", description = "No alert created (inventory matches sensor)"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "503", description = "Inventory service unavailable")
    })
    public ResponseEntity<RestaurantAlertResource> createRestaurantAlertFromInventory(
            @RequestParam(required = false, defaultValue = "1") Long sensorId
    ) {
        var alert = alertCommandService.handle(new CreateAlertRestaurantFromInventoryCommand(sensorId));
        return alert
                .map(a -> ResponseEntity.status(HttpStatus.CREATED).body(AlertResourceFromEntityAssembler.toRestaurantResourceFromEntity((RestaurantAlert) a)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/inventory-check")
    @Operation(summary = "Check inventory and create restaurant alert", description = "Checks inventory stock against sensor and creates an alert if different.")
    public ResponseEntity<RestaurantAlertResource> checkInventoryAndCreateAlert(
            @RequestParam(required = false, defaultValue = "1") Long sensorId
    ) {
        return createRestaurantAlertFromInventory(sensorId);
    }

    @GetMapping("/{alertId}")
    @Operation(summary = "Get restaurant alert by ID", description = "Retrieves a specific restaurant alert.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant alert found", content = @Content(schema = @Schema(implementation = RestaurantAlertResource.class))),
        @ApiResponse(responseCode = "404", description = "Restaurant alert not found")
    })
    public ResponseEntity<RestaurantAlertResource> getRestaurantAlertById(
            @PathVariable @Parameter(description = "Restaurant Alert ID", example = "1", required = true) Long alertId
    ) {
        var query = new GetAlertByIdQuery(alertId);
        var alert = alertQueryService.handle(query);

        if (alert.isEmpty() || !(alert.get() instanceof RestaurantAlert)) {
            return ResponseEntity.notFound().build();
        }

        var alertResource = AlertResourceFromEntityAssembler.toRestaurantResourceFromEntity((RestaurantAlert) alert.get());
        return ResponseEntity.ok(alertResource);
    }

    @GetMapping
    @Operation(summary = "Get all restaurant alerts", description = "Retrieves a list of all registered restaurant alerts.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant alerts retrieved successfully")
    })
    public ResponseEntity<List<RestaurantAlertResource>> getAllRestaurantAlerts() {
        var alerts = alertQueryService.handle(new GetAllRestaurantAlertsQuery());
        if (alerts.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        var resources = alerts.stream()
                .map(alert -> AlertResourceFromEntityAssembler.toRestaurantResourceFromEntity((RestaurantAlert) alert))
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{alertId}")
    @Operation(summary = "Acknowledge restaurant alert", description = "Acknowledges a restaurant alert by setting its status to Acknowledged.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant alert acknowledged successfully", content = @Content(schema = @Schema(implementation = RestaurantAlertResource.class))),
        @ApiResponse(responseCode = "404", description = "Restaurant alert not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<RestaurantAlertResource> acknowledgeRestaurantAlert(
            @PathVariable @Parameter(description = "Restaurant Alert ID", example = "1", required = true) Long alertId
    ) {
        var currentAlert = alertQueryService.handle(new GetAlertByIdQuery(alertId));
        if (currentAlert.isEmpty() || !(currentAlert.get() instanceof RestaurantAlert)) {
            return ResponseEntity.notFound().build();
        }

        try {
            var acknowledged = alertCommandService.handle(new aurora.supply_wok.platform.alerts.domain.model.commands.AcknowledgeAlertCommand(alertId));
            if (acknowledged.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(AlertResourceFromEntityAssembler.toRestaurantResourceFromEntity((RestaurantAlert) acknowledged.get()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
