package bg.angebotpilot.web;

import bg.angebotpilot.customer.CustomerRepository;
import bg.angebotpilot.offer.OfferRepository;
import bg.angebotpilot.offer.OfferStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final CustomerRepository customerRepository;
    private final OfferRepository offerRepository;

    public DashboardController(CustomerRepository customerRepository, OfferRepository offerRepository) {
        this.customerRepository = customerRepository;
        this.offerRepository = offerRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("customerCount", customerRepository.count());
        model.addAttribute("offerCount", offerRepository.count());
        model.addAttribute("acceptedOfferCount", offerRepository.countByStatus(OfferStatus.ACCEPTED));
        model.addAttribute("recentOffers", offerRepository.findAllByOrderByIssueDateDescIdDesc()
                .stream().limit(5).toList());
        return "dashboard";
    }
}
