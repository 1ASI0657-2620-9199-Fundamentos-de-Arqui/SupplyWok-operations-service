package aurora.supply_wok.platform.shared.interfaces.rest.transform;

import aurora.supply_wok.platform.shared.application.result.ApplicationError;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseAssemblerTests {

    @Test
    void toStatusFromErrorCode_whenValidationError_returnsBadRequest() {
        // Arrange
        String errorCode = "VALIDATION_ERROR";

        // Act
        HttpStatusCode status = ErrorResponseAssembler.toStatusFromErrorCode(errorCode);

        // Assert
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void toStatusFromErrorCode_whenNotFound_returnsNotFound() {
        // Arrange
        String errorCode = "RESOURCE_NOT_FOUND";

        // Act
        HttpStatusCode status = ErrorResponseAssembler.toStatusFromErrorCode(errorCode);

        // Assert
        assertThat(status).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void toStatusFromErrorCode_whenServiceUnavailable_returnsServiceUnavailable() {
        // Arrange
        String errorCode = "SERVICE_UNAVAILABLE";

        // Act
        HttpStatusCode status = ErrorResponseAssembler.toStatusFromErrorCode(errorCode);

        // Assert
        assertThat(status).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void toStatusFromErrorCode_whenConflict_returnsConflict() {
        // Arrange
        String errorCode = "ENTITY_CONFLICT";

        // Act
        HttpStatusCode status = ErrorResponseAssembler.toStatusFromErrorCode(errorCode);

        // Assert
        assertThat(status).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void toStatusFromErrorCode_whenBusinessRuleViolation_returnsUnprocessableEntity() {
        // Arrange
        String errorCode = "BUSINESS_RULE_VIOLATION";

        // Act
        HttpStatusCode status = ErrorResponseAssembler.toStatusFromErrorCode(errorCode);

        // Assert
        assertThat(status.value()).isEqualTo(422);
    }

    @Test
    void toErrorResponseFromApplicationError_whenErrorProvided_mapsToResponseEntity() {
        // Arrange
        var error = ApplicationError.validationError("name", "Name is required");

        // Act
        var response = ErrorResponseAssembler.toErrorResponseFromApplicationError(error);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("VALIDATION_ERROR");
    }
}
