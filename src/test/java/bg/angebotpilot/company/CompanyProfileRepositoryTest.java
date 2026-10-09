package bg.angebotpilot.company;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CompanyProfileRepositoryTest {

    @Autowired
    private CompanyProfileRepository repository;

    @Test
    void storesTheSingleCompanyProfile() {
        CompanyProfileForm form = new CompanyProfileForm();
        form.setCompanyName("Petrov Ausbau e.U.");
        form.setEmail("office@example.at");
        form.setStreet("Musterstraße 12");
        form.setPostalCode("1010");
        form.setCity("Wien");
        form.setCountry("Österreich");

        CompanyProfile profile = new CompanyProfile();
        profile.updateFrom(form);
        repository.saveAndFlush(profile);

        assertThat(repository.findFirstByOrderByIdAsc())
                .get()
                .extracting(CompanyProfile::getCompanyName)
                .isEqualTo("Petrov Ausbau e.U.");
    }
}
