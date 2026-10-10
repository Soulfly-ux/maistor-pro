package bg.angebotpilot.security;

import bg.angebotpilot.customer.Customer;
import bg.angebotpilot.customer.CustomerRepository;
import bg.angebotpilot.offer.Offer;
import bg.angebotpilot.offer.OfferItem;
import bg.angebotpilot.offer.OfferRepository;
import bg.angebotpilot.offer.OfferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OfferRepository offerRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void redirectsTheOwnerAreaToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Вход за майстора")));
    }

    @Test
    void authenticatesTheConfiguredOwner() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("angebotpilot-local"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("admin"));
    }

    @Test
    void keepsTheTokenizedOfferPublicAndAcceptsACsrfProtectedResponse() throws Exception {
        Offer offer = saveOffer();

        mockMvc.perform(get("/a/{token}", offer.getPublicToken()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Persönliches Angebot")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));

        mockMvc.perform(post("/a/{token}/response", offer.getPublicToken())
                        .param("decision", "ACCEPTED"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/a/{token}/response", offer.getPublicToken())
                        .with(csrf())
                        .param("decision", "ACCEPTED")
                        .param("note", "Termin bitte bestätigen"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/a/" + offer.getPublicToken()));

        Offer updated = offerRepository.findById(offer.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(OfferStatus.ACCEPTED);
        assertThat(updated.getCustomerResponseNote()).isEqualTo("Termin bitte bestätigen");
    }

    private Offer saveOffer() {
        Customer customer = customerRepository.save(new Customer(
                "Max Mustermann", null, "max@example.at", null,
                "Musterstraße 1", "1010", "Wien", "Österreich"));
        Offer offer = new Offer(
                "ANG-TEST-0001", "Malerarbeiten", LocalDate.now(), LocalDate.now().plusDays(14),
                BigDecimal.valueOf(20), null, customer);
        offer.addItem(new OfferItem("Wände streichen", BigDecimal.TEN, "m²", BigDecimal.valueOf(12.50)));
        return offerRepository.saveAndFlush(offer);
    }
}
