package bg.angebotpilot.catalog;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceItemTest {

    @Test
    void usesDescriptionForOffersAndCanBeArchived() {
        ServiceItem serviceItem = new ServiceItem(
                "Боядисване",
                "Бояджийски услуги",
                "Wände streichen, zweimaliger Anstrich",
                "m²",
                BigDecimal.valueOf(12.50));

        assertThat(serviceItem.getOfferDescription()).isEqualTo("Wände streichen, zweimaliger Anstrich");
        assertThat(serviceItem.isActive()).isTrue();

        serviceItem.toggleActive();

        assertThat(serviceItem.isActive()).isFalse();
    }

    @Test
    void fallsBackToTheServiceNameWhenDescriptionIsMissing() {
        ServiceItem serviceItem = new ServiceItem(
                "Anfahrt", null, " ", "Pauschale", BigDecimal.valueOf(30));

        assertThat(serviceItem.getOfferDescription()).isEqualTo("Anfahrt");
    }
}
