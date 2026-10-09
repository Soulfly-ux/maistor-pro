package bg.maistorpro.offer;

import bg.maistorpro.customer.Customer;
import bg.maistorpro.customer.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OfferRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Test
    void listQueryLoadsItemsNeededForTotalsAfterRepositorySessionIsClosed() {
        Customer customer = customerRepository.save(new Customer(
                "Ivan Petrov", "Petrov Ausbau e.U.", "ivan@example.at", null,
                "Musterstraße 1", "1010", "Wien", "Österreich"));
        Offer offer = new Offer(
                "ANG-2026-0001", "Malerarbeiten", LocalDate.now(), LocalDate.now().plusDays(14),
                BigDecimal.valueOf(20), null, customer);
        offer.addItem(new OfferItem("Wände streichen", BigDecimal.TEN, "m²", BigDecimal.valueOf(12.50)));
        offerRepository.saveAndFlush(offer);

        List<Offer> offers = offerRepository.findAllByOrderByIssueDateDescIdDesc();

        assertThat(offers).hasSize(1);
        assertThat(offers.get(0).getTotalGross()).isEqualByComparingTo("150.00");
    }
}
