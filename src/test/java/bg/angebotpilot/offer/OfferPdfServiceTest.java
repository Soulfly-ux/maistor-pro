package bg.angebotpilot.offer;

import bg.angebotpilot.company.CompanyProfile;
import bg.angebotpilot.company.CompanyProfileForm;
import bg.angebotpilot.customer.Customer;
import com.lowagie.text.pdf.PdfReader;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class OfferPdfServiceTest {

    @Test
    void createsReadablePdfWithUnicodeContent() throws Exception {
        CompanyProfileForm profileForm = new CompanyProfileForm();
        profileForm.setCompanyName("Петров Ausbau e.U.");
        profileForm.setOwnerName("Иван Петров");
        profileForm.setEmail("office@example.at");
        profileForm.setStreet("Musterstraße 12");
        profileForm.setPostalCode("1010");
        profileForm.setCity("Wien");
        profileForm.setCountry("Österreich");
        profileForm.setIban("AT61 1904 3002 3457 3201");
        profileForm.setBic("BKAUATWW");

        CompanyProfile profile = new CompanyProfile();
        profile.updateFrom(profileForm);

        Customer customer = new Customer(
                "Мария Иванова", "Beispiel Kundin GmbH", "maria@example.at", null,
                "Hauptstraße 1", "1020", "Wien", "Österreich");
        Offer offer = new Offer(
                "ANG-2026-0042", "Malerarbeiten und Vorbereitung",
                LocalDate.of(2026, 10, 9), LocalDate.of(2026, 10, 23),
                BigDecimal.valueOf(20), "Ausführung nach Terminvereinbarung.", customer);
        offer.addItem(new OfferItem("Wände streichen", BigDecimal.valueOf(65), "m²",
                BigDecimal.valueOf(12.50)));

        byte[] pdf = new OfferPdfService().generate(offer, profile);

        assertThat(pdf).hasSizeGreaterThan(5_000);
        assertThat(new String(pdf, 0, 5, StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
        PdfReader reader = new PdfReader(pdf);
        assertThat(reader.getNumberOfPages()).isEqualTo(1);
        reader.close();

        String previewPath = System.getProperty("pdf.preview.path");
        if (previewPath != null && !previewPath.isBlank()) {
            Path output = Path.of(previewPath);
            Files.createDirectories(output.getParent());
            Files.write(output, pdf);
        }
    }
}
