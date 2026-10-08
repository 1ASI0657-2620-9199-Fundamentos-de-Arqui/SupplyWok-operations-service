package aurora.supply_wok.platform.restaurantmanagement.application.internal.queryservices;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetAllComandasQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetComandaByIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.queries.GetComandasByTableIdQuery;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.ComandaRepository;
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
class ComandaQueryServiceImplTests {

    @Mock
    private ComandaRepository comandaRepository;

    @InjectMocks
    private ComandaQueryServiceImpl comandaQueryService;

    @Test
    void handleGetAllComandas_whenInvoked_delegatesToRepositoryFindAll() {
        // Arrange
        var expectedList = List.of(new Comanda(1L), new Comanda(2L));
        when(comandaRepository.findAll()).thenReturn(expectedList);

        // Act
        List<Comanda> result = comandaQueryService.handle(new GetAllComandasQuery());

        // Assert
        assertThat(result).isSameAs(expectedList);
        verify(comandaRepository).findAll();
    }

    @Test
    void handleGetComandaById_whenInvoked_delegatesToRepositoryFindById() {
        // Arrange
        var comanda = new Comanda(1L);
        when(comandaRepository.findById(10L)).thenReturn(Optional.of(comanda));

        // Act
        Optional<Comanda> result = comandaQueryService.handle(new GetComandaByIdQuery(10L));

        // Assert
        assertThat(result).containsSame(comanda);
        verify(comandaRepository).findById(10L);
    }

    @Test
    void handleGetComandasByTableId_whenInvoked_delegatesToRepositoryFindByTableId() {
        // Arrange
        var expectedList = List.of(new Comanda(3L));
        when(comandaRepository.findByTableId(3L)).thenReturn(expectedList);

        // Act
        List<Comanda> result = comandaQueryService.handle(new GetComandasByTableIdQuery(3L));

        // Assert
        assertThat(result).isSameAs(expectedList);
        verify(comandaRepository).findByTableId(3L);
    }
}
