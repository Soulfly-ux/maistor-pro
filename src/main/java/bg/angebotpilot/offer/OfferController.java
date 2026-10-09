package bg.angebotpilot.offer;

import bg.angebotpilot.customer.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/offers")
public class OfferController {

    private final OfferRepository offerRepository;
    private final CustomerRepository customerRepository;
    private final OfferService offerService;

    public OfferController(OfferRepository offerRepository,
                           CustomerRepository customerRepository,
                           OfferService offerService) {
        this.offerRepository = offerRepository;
        this.customerRepository = customerRepository;
        this.offerService = offerService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("offers", offerRepository.findAllByOrderByIssueDateDescIdDesc());
        return "offers/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Long customerId, Model model) {
        if (!model.containsAttribute("offerForm")) {
            OfferForm form = new OfferForm();
            form.setCustomerId(customerId);
            model.addAttribute("offerForm", form);
        }
        addCustomers(model);
        return "offers/new";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute OfferForm offerForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (offerForm.getIssueDate() != null && offerForm.getValidUntil() != null
                && offerForm.getValidUntil().isBefore(offerForm.getIssueDate())) {
            bindingResult.rejectValue("validUntil", "date.order",
                    "Срокът не може да бъде преди датата на офертата");
        }

        if (bindingResult.hasErrors()) {
            addCustomers(model);
            return "offers/new";
        }

        Offer offer = offerService.create(offerForm);
        redirectAttributes.addFlashAttribute("successMessage",
                "Офертата „" + offer.getOfferNumber() + "“ беше създадена.");
        return "redirect:/offers/" + offer.getId();
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("offer", offerService.getDetailed(id));
        model.addAttribute("statuses", OfferStatus.values());
        return "offers/detail";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam OfferStatus status,
                               RedirectAttributes redirectAttributes) {
        Offer offer = offerService.changeStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage",
                "Статусът беше променен на „" + offer.getStatus().getLabel() + "“.");
        return "redirect:/offers/" + id;
    }

    private void addCustomers(Model model) {
        model.addAttribute("customers", customerRepository.findAllByOrderByCompanyNameAscNameAsc());
    }
}
