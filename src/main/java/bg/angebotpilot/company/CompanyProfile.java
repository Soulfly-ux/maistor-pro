package bg.angebotpilot.company;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_profiles")
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String ownerName;
    private String email;
    private String phone;
    private String website;
    private String street;
    private String postalCode;
    private String city;
    private String country;
    private String taxNumber;
    private String vatId;
    private String iban;
    private String bic;
    private Integer paymentTermDays;
    @Column(length = 500)
    private String footerNote;

    public CompanyProfile() {
    }

    public void updateFrom(CompanyProfileForm form) {
        companyName = clean(form.getCompanyName());
        ownerName = clean(form.getOwnerName());
        email = clean(form.getEmail());
        phone = clean(form.getPhone());
        website = clean(form.getWebsite());
        street = clean(form.getStreet());
        postalCode = clean(form.getPostalCode());
        city = clean(form.getCity());
        country = clean(form.getCountry());
        taxNumber = clean(form.getTaxNumber());
        vatId = clean(form.getVatId());
        iban = clean(form.getIban());
        bic = clean(form.getBic());
        paymentTermDays = form.getPaymentTermDays();
        footerNote = clean(form.getFooterNote());
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public Long getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public String getStreet() {
        return street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public String getVatId() {
        return vatId;
    }

    public String getIban() {
        return iban;
    }

    public String getBic() {
        return bic;
    }

    public Integer getPaymentTermDays() {
        return paymentTermDays;
    }

    public String getFooterNote() {
        return footerNote;
    }
}
