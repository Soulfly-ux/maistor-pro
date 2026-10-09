package bg.angebotpilot.offer;

import bg.angebotpilot.company.CompanyProfile;
import bg.angebotpilot.company.CompanyProfileService;
import bg.angebotpilot.customer.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/offers")
public class OfferController {

    private final OfferRepository offerRepository;
    private final CustomerRepository customerRepository;
    private final OfferService offerService;
    private final CompanyProfileService companyProfileService;
    private final OfferPdfService offerPdfService;

    public OfferController(OfferRepository offerRepository,
                           CustomerRepository customerRepository,
                           OfferService offerService,
                           CompanyProfileService companyProfileService,
                           OfferPdfService offerPdfService) {
        this.offerRepository = offerRepository;
        this.customerRepository = customerRepository;
        this.offerService = offerService;
        this.companyProfileService = companyProfileService;
        this.offerPdfService = offerPdfService;
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
        model.addAttribute("companyProfile", companyProfileService.findProfile().orElse(null));
        return "offers/detail";
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Offer offer = offerService.getDetailed(id);
        CompanyProfile profile = companyProfileService.requireProfile();
        byte[] pdf = offerPdfService.generate(offer, profile);
        String fileName = "Angebot-" + offer.getOfferNumber().replaceAll("[^A-Za-z0-9._-]", "_") + ".pdf";

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(fileName, StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentLength(pdf.length)
                .body(pdf);
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
