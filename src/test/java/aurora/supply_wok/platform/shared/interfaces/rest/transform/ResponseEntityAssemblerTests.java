package aurora.supply_wok.platform.shared.interfaces.rest.transform;

import aurora.supply_wok.platform.shared.application.result.ApplicationError;
import aurora.supply_wok.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseEntityAssemblerTests {

    @Test
    void toResponseEntityFromResult_whenSuccess_returnsSuccessStatusAndTransformedBody() {
        // Arrange
        Result<String, ApplicationError> result = Result.success("sample");

        // Act
        ResponseEntity<?> response = ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                String::toUpperCase,
                HttpStatus.OK
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("SAMPLE");
    }

    @Test
    void toResponseEntityFromResult_whenFailure_returnsErrorResponse() {
        // Arrange
        ApplicationError error = ApplicationError.notFound("Order", "123");
        Result<String, ApplicationError> result = Result.failure(error);

        // Act
        ResponseEntity<?> response = ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                String::toUpperCase,
                HttpStatus.OK
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
