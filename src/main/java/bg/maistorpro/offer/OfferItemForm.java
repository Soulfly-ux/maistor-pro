package bg.maistorpro.offer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class OfferItemForm {

    @NotBlank(message = "Добави описание")
    private String description;

    @NotNull(message = "Добави количество")
    @DecimalMin(value = "0.01", message = "Количеството трябва да бъде по-голямо от нула")
    @Digits(integer = 8, fraction = 2, message = "Използвай най-много 2 цифри след запетаята")
    private BigDecimal quantity = BigDecimal.ONE;

    @NotBlank(message = "Добави мерна единица")
    private String unit = "Stk.";

    @NotNull(message = "Добави единична цена")
    @DecimalMin(value = "0.00", message = "Цената не може да бъде отрицателна")
    @Digits(integer = 10, fraction = 2, message = "Използвай най-много 2 цифри след запетаята")
    private BigDecimal unitPrice;

    public OfferItem toOfferItem() {
        return new OfferItem(description, quantity, unit, unitPrice);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
