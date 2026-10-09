package bg.angebotpilot.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "service_items")
public class ServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String description;
    private String unit;
    private BigDecimal defaultPrice;
    private boolean active = true;

    protected ServiceItem() {
    }

    public ServiceItem(String name, String category, String description, String unit, BigDecimal defaultPrice) {
        this.name = name.trim();
        this.category = clean(category);
        this.description = clean(description);
        this.unit = unit.trim();
        this.defaultPrice = defaultPrice;
    }

    public void toggleActive() {
        active = !active;
    }

    public String getOfferDescription() {
        return description == null ? name : description;
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getDefaultPrice() {
        return defaultPrice;
    }

    public boolean isActive() {
        return active;
    }
}
