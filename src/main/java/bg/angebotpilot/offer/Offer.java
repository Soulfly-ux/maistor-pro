package bg.angebotpilot.offer;

import bg.angebotpilot.customer.Customer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String offerNumber;
    private String title;
    private LocalDate issueDate;
    private LocalDate validUntil;
    private BigDecimal taxRate;
    private String notes;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    private final List<OfferItem> items = new ArrayList<>();

    protected Offer() {
    }

    public Offer(String offerNumber, String title, LocalDate issueDate, LocalDate validUntil,
                 BigDecimal taxRate, String notes, Customer customer) {
        this.offerNumber = offerNumber;
        this.title = title;
        this.issueDate = issueDate;
        this.validUntil = validUntil;
        this.taxRate = taxRate;
        this.notes = notes;
        this.customer = customer;
        this.status = OfferStatus.DRAFT;
    }

    public void addItem(OfferItem item) {
        item.attachTo(this);
        items.add(item);
    }

    public void changeStatus(OfferStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalNet() {
        return items.stream()
                .map(OfferItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTaxAmount() {
        if (taxRate == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return getTotalNet()
                .multiply(taxRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalGross() {
        return getTotalNet().add(getTaxAmount()).setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public String getOfferNumber() {
        return offerNumber;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public String getNotes() {
        return notes;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OfferItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
