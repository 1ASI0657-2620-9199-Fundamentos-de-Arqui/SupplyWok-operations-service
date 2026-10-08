package aurora.supply_wok.platform.shared.application.result;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultTests {

    @Test
    void success_whenConstructed_indicatesSuccessAndHoldsValue() {
        // Arrange
        String value = "Success Value";

        // Act
        Result<String, String> result = Result.success(value);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
        assertThat(result.toOptional()).contains(value);
        assertThat(result.getOrElse("Default")).isEqualTo(value);
    }

    @Test
    void failure_whenConstructed_indicatesFailureAndHoldsError() {
        // Arrange
        String error = "Error Detail";

        // Act
        Result<String, String> result = Result.failure(error);

        // Assert
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.toOptional()).isEmpty();
        assertThat(result.getOrElse("Default")).isEqualTo("Default");
    }

    @Test
    void map_whenSuccess_transformsValue() {
        // Arrange
        Result<Integer, String> result = Result.success(5);

        // Act
        Result<Integer, String> mapped = result.map(n -> n * 2);

        // Assert
        assertThat(mapped.toOptional()).contains(10);
    }

    @Test
    void mapError_whenFailure_transformsError() {
        // Arrange
        Result<Integer, String> result = Result.failure("bad_request");

        // Act
        Result<Integer, String> mapped = result.mapError(String::toUpperCase);

        // Assert
        assertThat(mapped).isInstanceOf(Result.Failure.class);
        assertThat(((Result.Failure<Integer, String>) mapped).error()).isEqualTo("BAD_REQUEST");
    }

    @Test
    void flatMap_whenSuccess_returnsNewResult() {
        // Arrange
        Result<Integer, String> result = Result.success(10);

        // Act
        Result<String, String> flatMapped = result.flatMap(n -> Result.success("Number: " + n));

        // Assert
        assertThat(flatMapped.toOptional()).contains("Number: 10");
    }

    @Test
    void recover_whenFailure_recoversWithFallback() {
        // Arrange
        Result<String, String> result = Result.failure("Network error");

        // Act
        Result<String, String> recovered = result.recover(err -> Result.success("Cached response"));

        // Assert
        assertThat(recovered.toOptional()).contains("Cached response");
    }
}
