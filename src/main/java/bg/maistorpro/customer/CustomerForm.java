package bg.maistorpro.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomerForm {

    @NotBlank(message = "Името е задължително")
    @Size(max = 120, message = "Името може да бъде най-много 120 знака")
    private String name;

    @Size(max = 160, message = "Името на фирмата може да бъде най-много 160 знака")
    private String companyName;

    @Email(message = "Въведи валиден имейл адрес")
    private String email;

    private String phone;
    private String street;
    private String postalCode;
    private String city;
    private String country = "Österreich";

    public Customer toCustomer() {
        return new Customer(name, companyName, email, phone, street, postalCode, city, country);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
}
