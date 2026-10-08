package aurora.supply_wok.platform.restaurantmanagement.application.internal.commandservices;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.AddComandaItemCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.CreateComandaCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.DeleteComandaCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateComandaStatusCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.services.ComandaCommandService;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.ComandaRepository;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ComandaCommandServiceImpl implements ComandaCommandService {

    private final ComandaRepository comandaRepository;
    private final DomainEventPublisher domainEventPublisher;

    public ComandaCommandServiceImpl(ComandaRepository comandaRepository, DomainEventPublisher domainEventPublisher) {
        this.comandaRepository = comandaRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Long handle(CreateComandaCommand command) {
        var comanda = new Comanda(command);
        comandaRepository.save(comanda);
        comanda.onCreated();
        domainEventPublisher.publishAndClear(comanda);
        return comanda.getId();
    }

    @Override
    public Optional<Comanda> handle(UpdateComandaStatusCommand command) {
        var result = comandaRepository.findById(command.comandaId());
        if (result.isEmpty())
            throw new IllegalArgumentException("Comanda with id %d not found".formatted(command.comandaId()));
        var comanda = result.get();
        comanda.updateStatus(command);
        comandaRepository.save(comanda);
        domainEventPublisher.publishAndClear(comanda);
        return Optional.of(comanda);
    }

    @Override
    public Optional<Comanda> handle(AddComandaItemCommand command) {
        var result = comandaRepository.findById(command.comandaId());
        if (result.isEmpty())
            throw new IllegalArgumentException("Comanda with id %d not found".formatted(command.comandaId()));
        var comanda = result.get();
        comanda.addItem(command);
        comandaRepository.save(comanda);
        domainEventPublisher.publishAndClear(comanda);
        return Optional.of(comanda);
    }

    @Override
    public void handle(DeleteComandaCommand command) {
        if (!comandaRepository.existsById(command.comandaId()))
            throw new IllegalArgumentException("Comanda with id %d not found".formatted(command.comandaId()));
        comandaRepository.deleteById(command.comandaId());
    }
}
