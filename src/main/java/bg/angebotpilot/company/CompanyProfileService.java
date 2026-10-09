package bg.angebotpilot.company;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CompanyProfileService {

    private final CompanyProfileRepository repository;

    public CompanyProfileService(CompanyProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<CompanyProfile> findProfile() {
        return repository.findFirstByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public CompanyProfile requireProfile() {
        return findProfile().orElseThrow(() ->
                new EntityNotFoundException("Попълни фирмения профил, преди да свалиш PDF оферта"));
    }

    @Transactional
    public CompanyProfile save(CompanyProfileForm form) {
        CompanyProfile profile = repository.findFirstByOrderByIdAsc()
                .orElseGet(CompanyProfile::new);
        profile.updateFrom(form);
        return repository.save(profile);
    }
}
