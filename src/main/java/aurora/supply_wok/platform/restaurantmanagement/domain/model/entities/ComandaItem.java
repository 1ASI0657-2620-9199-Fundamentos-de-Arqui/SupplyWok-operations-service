package aurora.supply_wok.platform.restaurantmanagement.domain.model.entities;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Table(name = "comanda_items")
public class ComandaItem extends AuditableModel {

    @Getter
    @NotNull
    @NotBlank
    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;

    @Getter
    @NotNull
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Getter
    @Column(name = "notes", length = 255)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    public ComandaItem() {
    }

    public ComandaItem(String productName, int quantity, String notes, Comanda comanda) {
        this.productName = productName;
        this.quantity = quantity;
        this.notes = notes;
        this.comanda = comanda;
    }

    public ComandaItem updateQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }
}
