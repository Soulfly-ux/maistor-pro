package bg.angebotpilot.offer;

import bg.angebotpilot.customer.Customer;
import bg.angebotpilot.customer.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final CustomerRepository customerRepository;

    public OfferService(OfferRepository offerRepository, CustomerRepository customerRepository) {
        this.offerRepository = offerRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Offer create(OfferForm form) {
        Customer customer = customerRepository.findById(form.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Клиентът не беше намерен"));

        String offerNumber = "ANG-%d-%04d".formatted(
                form.getIssueDate().getYear(), offerRepository.count() + 1);

        Offer offer = new Offer(
                offerNumber,
                form.getTitle(),
                form.getIssueDate(),
                form.getValidUntil(),
                form.getTaxRate(),
                form.getNotes(),
                customer
        );
        form.getItems().stream()
                .map(OfferItemForm::toOfferItem)
                .forEach(offer::addItem);

        return offerRepository.save(offer);
    }

    @Transactional(readOnly = true)
    public Offer getDetailed(Long id) {
        return offerRepository.findDetailedById(id)
                .orElseThrow(() -> new EntityNotFoundException("Офертата не беше намерена"));
    }

    @Transactional
    public Offer changeStatus(Long id, OfferStatus status) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Офертата не беше намерена"));
        offer.changeStatus(status);
        return offer;
    }
}
