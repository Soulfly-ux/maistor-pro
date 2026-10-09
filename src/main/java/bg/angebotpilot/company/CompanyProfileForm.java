package bg.angebotpilot.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompanyProfileForm {

    @NotBlank(message = "Името на фирмата е задължително")
    @Size(max = 160, message = "Името може да бъде най-много 160 знака")
    private String companyName;

    @Size(max = 120, message = "Името може да бъде най-много 120 знака")
    private String ownerName;

    @NotBlank(message = "Имейлът е задължителен")
    @Email(message = "Въведи валиден имейл адрес")
    private String email;

    private String phone;
    private String website;

    @NotBlank(message = "Адресът е задължителен")
    private String street;

    @NotBlank(message = "Пощенският код е задължителен")
    private String postalCode;

    @NotBlank(message = "Градът е задължителен")
    private String city;

    @NotBlank(message = "Държавата е задължителна")
    private String country = "Österreich";

    private String taxNumber;
    private String vatId;
    private String iban;
    private String bic;

    @Min(value = 1, message = "Срокът трябва да е поне 1 ден")
    @Max(value = 365, message = "Срокът не може да е повече от 365 дни")
    private Integer paymentTermDays = 14;

    @Size(max = 500, message = "Текстът може да бъде най-много 500 знака")
    private String footerNote = "Vielen Dank für Ihr Vertrauen.";

    public static CompanyProfileForm from(CompanyProfile profile) {
        CompanyProfileForm form = new CompanyProfileForm();
        form.companyName = profile.getCompanyName();
        form.ownerName = profile.getOwnerName();
        form.email = profile.getEmail();
        form.phone = profile.getPhone();
        form.website = profile.getWebsite();
        form.street = profile.getStreet();
        form.postalCode = profile.getPostalCode();
        form.city = profile.getCity();
        form.country = profile.getCountry();
        form.taxNumber = profile.getTaxNumber();
        form.vatId = profile.getVatId();
        form.iban = profile.getIban();
        form.bic = profile.getBic();
        form.paymentTermDays = profile.getPaymentTermDays();
        form.footerNote = profile.getFooterNote();
        return form;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getVatId() {
        return vatId;
    }

    public void setVatId(String vatId) {
        this.vatId = vatId;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public Integer getPaymentTermDays() {
        return paymentTermDays;
    }

    public void setPaymentTermDays(Integer paymentTermDays) {
        this.paymentTermDays = paymentTermDays;
    }

    public String getFooterNote() {
        return footerNote;
    }

    public void setFooterNote(String footerNote) {
        this.footerNote = footerNote;
    }
}
