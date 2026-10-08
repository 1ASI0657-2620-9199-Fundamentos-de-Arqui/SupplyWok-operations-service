package aurora.supply_wok.platform.iot.interfaces.rest;

import aurora.supply_wok.platform.iot.application.commandservices.SensorCommandService;
import aurora.supply_wok.platform.iot.application.queryservices.SensorQueryService;
import aurora.supply_wok.platform.iot.domain.model.commands.DeleteSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.queries.GetAllSensorsQuery;
import aurora.supply_wok.platform.iot.domain.model.queries.GetSensorByIdQuery;
import aurora.supply_wok.platform.iot.interfaces.rest.resources.CreateSensorResource;
import aurora.supply_wok.platform.iot.interfaces.rest.resources.SensorResource;
import aurora.supply_wok.platform.iot.interfaces.rest.resources.UpdateSensorResource;
import aurora.supply_wok.platform.iot.interfaces.rest.transform.CreateSensorCommandFromResourceAssembler;
import aurora.supply_wok.platform.iot.interfaces.rest.transform.SensorResourceFromEntityAssembler;
import aurora.supply_wok.platform.iot.interfaces.rest.transform.UpdateSensorCommandFromResourceAssembler;
import aurora.supply_wok.platform.shared.domain.exceptions.ResourceNotFoundException;
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

/**
 * REST controller that exposes endpoints for managing sensors.
 * Provides endpoints for creating, retrieving, updating, deleting, and listing sensors.
 */
@RestController
@RequestMapping(value = "/api/v1/sensors", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Sensors", description = "Sensor management endpoints")
@PreAuthorize("hasRole('RESTAURANT')")
public class SensorsController {

    private final SensorCommandService sensorCommandService;
    private final SensorQueryService sensorQueryService;
    private final CurrentUserProvider currentUserProvider;

    public SensorsController(SensorCommandService sensorCommandService,
                             SensorQueryService sensorQueryService,
                             CurrentUserProvider currentUserProvider) {
        this.sensorCommandService = sensorCommandService;
        this.sensorQueryService = sensorQueryService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(
        summary = "Create a new sensor",
        description = "Creates a new sensor with specified attributes and validation."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Sensor created successfully",
            content = @Content(schema = @Schema(implementation = SensorResource.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<SensorResource> createSensor(@Valid @RequestBody CreateSensorResource resource) {
        try {
            Long restaurantId = currentUserProvider.userId();
            var createSensorCommand = CreateSensorCommandFromResourceAssembler.toCommandFromResource(restaurantId, resource);
            var sensorId = sensorCommandService.handle(createSensorCommand);

            var getSensorByIdQuery = new GetSensorByIdQuery(restaurantId, sensorId);
            var sensor = sensorQueryService.handle(getSensorByIdQuery);

            if (sensor.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            var sensorResource = SensorResourceFromEntityAssembler.toResourceFromEntity(sensor.get());
            return new ResponseEntity<>(sensorResource, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{sensorId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(
        summary = "Get sensor by ID",
        description = "Retrieves a specific sensor's information by unique identifier."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sensor found",
            content = @Content(schema = @Schema(implementation = SensorResource.class))
        ),
        @ApiResponse(responseCode = "404", description = "Sensor not found")
    })
    public ResponseEntity<SensorResource> getSensorById(
            @PathVariable
            @Parameter(description = "Sensor unique identifier", example = "1", required = true)
            Long sensorId
    ) {
        Long restaurantId = currentUserProvider.userId();
        var getSensorByIdQuery = new GetSensorByIdQuery(restaurantId, sensorId);
        var sensor = sensorQueryService.handle(getSensorByIdQuery);

        if (sensor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var sensorResource = SensorResourceFromEntityAssembler.toResourceFromEntity(sensor.get());
        return ResponseEntity.ok(sensorResource);
    }

    @GetMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(
        summary = "Get all sensors",
        description = "Retrieves a list of all registered sensors."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sensors list retrieved",
            content = @Content(schema = @Schema(implementation = SensorResource.class))
        )
    })
    public ResponseEntity<List<SensorResource>> getAllSensors() {
        Long restaurantId = currentUserProvider.userId();
        var sensors = sensorQueryService.handle(new GetAllSensorsQuery(restaurantId));
        if (sensors.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        var sensorResources = sensors.stream()
                .map(SensorResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(sensorResources);
    }

    @PutMapping("/{sensorId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(
        summary = "Update sensor",
        description = "Updates an existing sensor's information by unique identifier."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sensor updated successfully",
            content = @Content(schema = @Schema(implementation = SensorResource.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Sensor not found")
    })
    public ResponseEntity<SensorResource> updateSensor(
            @PathVariable
            @Parameter(description = "Sensor unique identifier", example = "1", required = true)
            Long sensorId,
            @Valid @RequestBody UpdateSensorResource resource
    ) {
        Long restaurantId = currentUserProvider.userId();
        try {
            var command = UpdateSensorCommandFromResourceAssembler.toCommandFromResource(restaurantId, sensorId, resource);
            var sensor = sensorCommandService.handle(command);
            if (sensor.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var sensorResource = SensorResourceFromEntityAssembler.toResourceFromEntity(sensor.get());
            return ResponseEntity.ok(sensorResource);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage().contains("does not exist")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{sensorId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(
        summary = "Delete sensor",
        description = "Deletes an existing sensor by unique identifier."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Sensor deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Sensor not found")
    })
    public ResponseEntity<Void> deleteSensor(
            @PathVariable
            @Parameter(description = "Sensor unique identifier", example = "1", required = true)
            Long sensorId
    ) {
        Long restaurantId = currentUserProvider.userId();
        try {
            sensorCommandService.handle(new DeleteSensorCommand(restaurantId, sensorId));
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage().contains("does not exist")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
}
