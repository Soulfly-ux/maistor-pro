package bg.angebotpilot.offer;

import bg.angebotpilot.customer.Customer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OfferTest {

    @Test
    void calculatesNetTaxAndGrossTotals() {
        Customer customer = new Customer(
                "Ivan Petrov", "Petrov Ausbau e.U.", "ivan@example.at", null,
                "Musterstraße 1", "1010", "Wien", "Österreich");
        Offer offer = new Offer(
                "ANG-2026-0001", "Malerarbeiten", LocalDate.now(), LocalDate.now().plusDays(14),
                BigDecimal.valueOf(20), null, customer);

        offer.addItem(new OfferItem("Wände streichen", BigDecimal.valueOf(65), "m²", BigDecimal.valueOf(12.50)));
        offer.addItem(new OfferItem("Material", BigDecimal.ONE, "Pauschale", BigDecimal.valueOf(180)));

        assertThat(offer.getTotalNet()).isEqualByComparingTo("992.50");
        assertThat(offer.getTaxAmount()).isEqualByComparingTo("198.50");
        assertThat(offer.getTotalGross()).isEqualByComparingTo("1191.00");
    }

    @Test
    void recordsTheCustomersDecisionOnlyOnce() {
        Offer offer = createOffer(LocalDate.now().plusDays(14));

        offer.recordCustomerDecision(OfferStatus.ACCEPTED, " Bitte Termin bestätigen. ");

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.ACCEPTED);
        assertThat(offer.getCustomerResponseNote()).isEqualTo("Bitte Termin bestätigen.");
        assertThat(offer.getCustomerRespondedAt()).isNotNull();
        assertThat(offer.getPublicToken()).isNotBlank();

        offer.recordCustomerDecision(OfferStatus.ACCEPTED, "ignored duplicate");

        assertThat(offer.getCustomerResponseNote()).isEqualTo("Bitte Termin bestätigen.");
        assertThatThrownBy(() -> offer.recordCustomerDecision(OfferStatus.REJECTED, null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void recognizesAnExpiredOffer() {
        Offer offer = createOffer(LocalDate.now().minusDays(1));

        assertThat(offer.isExpired()).isTrue();
    }

    private Offer createOffer(LocalDate validUntil) {
        Customer customer = new Customer(
                "Ivan Petrov", "Petrov Ausbau e.U.", "ivan@example.at", null,
                "Musterstraße 1", "1010", "Wien", "Österreich");
        return new Offer(
                "ANG-2026-0001", "Malerarbeiten", LocalDate.now(), validUntil,
                BigDecimal.valueOf(20), null, customer);
    }
}
