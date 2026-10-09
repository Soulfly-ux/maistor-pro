package bg.angebotpilot.web;

import bg.angebotpilot.company.CompanyProfileService;
import bg.angebotpilot.catalog.ServiceItemRepository;
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
    private final CompanyProfileService companyProfileService;
    private final ServiceItemRepository serviceItemRepository;

    public DashboardController(CustomerRepository customerRepository,
                               OfferRepository offerRepository,
                               CompanyProfileService companyProfileService,
                               ServiceItemRepository serviceItemRepository) {
        this.customerRepository = customerRepository;
        this.offerRepository = offerRepository;
        this.companyProfileService = companyProfileService;
        this.serviceItemRepository = serviceItemRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("customerCount", customerRepository.count());
        model.addAttribute("serviceCount", serviceItemRepository.count());
        model.addAttribute("offerCount", offerRepository.count());
        model.addAttribute("acceptedOfferCount", offerRepository.countByStatus(OfferStatus.ACCEPTED));
        model.addAttribute("companyProfileConfigured", companyProfileService.findProfile().isPresent());
        model.addAttribute("recentOffers", offerRepository.findAllByOrderByIssueDateDescIdDesc()
                .stream().limit(5).toList());
        return "dashboard";
    }
}
