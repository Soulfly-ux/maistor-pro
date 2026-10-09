package bg.angebotpilot.catalog;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ServiceItemForm {

    @NotBlank(message = "Името на услугата е задължително")
    @Size(max = 140, message = "Името може да бъде най-много 140 знака")
    private String name;

    @Size(max = 100, message = "Категорията може да бъде най-много 100 знака")
    private String category;

    @Size(max = 255, message = "Описанието може да бъде най-много 255 знака")
    private String description;

    @NotBlank(message = "Мерната единица е задължителна")
    private String unit = "Std.";

    @NotNull(message = "Добави стандартна цена")
    @DecimalMin(value = "0.00", message = "Цената не може да бъде отрицателна")
    @Digits(integer = 10, fraction = 2, message = "Използвай най-много 2 цифри след запетаята")
    private BigDecimal defaultPrice;

    public ServiceItem toServiceItem() {
        return new ServiceItem(name, category, description, unit, defaultPrice);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(BigDecimal defaultPrice) {
        this.defaultPrice = defaultPrice;
    }
}
