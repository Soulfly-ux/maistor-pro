package bg.angebotpilot.offer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OfferForm {

    @NotNull(message = "Избери клиент")
    private Long customerId;

    @NotBlank(message = "Заглавието е задължително")
    private String title;

    @NotNull(message = "Избери дата")
    private LocalDate issueDate = LocalDate.now();

    @NotNull(message = "Избери срок на валидност")
    private LocalDate validUntil = LocalDate.now().plusDays(14);

    @NotNull(message = "Добави данъчна ставка")
    @DecimalMin(value = "0.00", message = "Данъкът не може да бъде отрицателен")
    @DecimalMax(value = "100.00", message = "Данъкът не може да надвишава 100%")
    private BigDecimal taxRate = BigDecimal.valueOf(20);

    private String notes;

    @NotEmpty(message = "Офертата трябва да съдържа поне една позиция")
    private List<@Valid OfferItemForm> items = new ArrayList<>(List.of(new OfferItemForm()));

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<OfferItemForm> getItems() {
        return items;
    }

    public void setItems(List<OfferItemForm> items) {
        this.items = items;
    }
}
