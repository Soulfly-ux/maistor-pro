package bg.angebotpilot.offer;

import bg.angebotpilot.company.CompanyProfile;
import bg.angebotpilot.company.CompanyProfileService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/a")
public class PublicOfferController {

    private final OfferService offerService;
    private final CompanyProfileService companyProfileService;
    private final OfferPdfService offerPdfService;

    public PublicOfferController(OfferService offerService,
                                 CompanyProfileService companyProfileService,
                                 OfferPdfService offerPdfService) {
        this.offerService = offerService;
        this.companyProfileService = companyProfileService;
        this.offerPdfService = offerPdfService;
    }

    @GetMapping("/{token}")
    public String view(@PathVariable String token, Model model) {
        model.addAttribute("offer", offerService.getDetailedByPublicToken(token));
        model.addAttribute("companyProfile", companyProfileService.findProfile().orElse(null));
        return "public/offer";
    }

    @PostMapping("/{token}/response")
    public String respond(@PathVariable String token,
                          @RequestParam OfferStatus decision,
                          @RequestParam(required = false) String note,
                          RedirectAttributes redirectAttributes) {
        Offer offer = offerService.recordCustomerDecision(token, decision, note);
        String message = offer.getStatus() == OfferStatus.ACCEPTED
                ? "Vielen Dank. Das Angebot wurde angenommen."
                : "Ihre Rückmeldung wurde übermittelt.";
        redirectAttributes.addFlashAttribute("publicMessage", message);
        return "redirect:/a/" + token;
    }

    @GetMapping("/{token}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String token) {
        Offer offer = offerService.getDetailedByPublicToken(token);
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
}
