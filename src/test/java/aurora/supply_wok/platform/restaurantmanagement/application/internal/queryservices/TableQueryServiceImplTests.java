package aurora.supply_wok.platform.restaurantmanagement.application.internal.queryservices;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetAllTablesQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetTableByIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.RestaurantTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableQueryServiceImplTests {

    @Mock
    private RestaurantTableRepository tableRepository;

    @InjectMocks
    private TableQueryServiceImpl tableQueryService;

    @Test
    void handleGetAllTables_whenInvoked_delegatesToRepositoryFindAll() {
        // Arrange
        var expectedList = List.of(new RestaurantTable(1, 4), new RestaurantTable(2, 6));
        when(tableRepository.findAll()).thenReturn(expectedList);

        // Act
        List<RestaurantTable> result = tableQueryService.handle(new GetAllTablesQuery());

        // Assert
        assertThat(result).isSameAs(expectedList);
        verify(tableRepository).findAll();
    }

    @Test
    void handleGetTableById_whenInvoked_delegatesToRepositoryFindById() {
        // Arrange
        var table = new RestaurantTable(1, 4);
        when(tableRepository.findById(5L)).thenReturn(Optional.of(table));

        // Act
        Optional<RestaurantTable> result = tableQueryService.handle(new GetTableByIdQuery(5L));

        // Assert
        assertThat(result).containsSame(table);
        verify(tableRepository).findById(5L);
    }
}
