package bg.angebotpilot.company;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/company")
public class CompanyProfileController {

    private final CompanyProfileService service;

    public CompanyProfileController(CompanyProfileService service) {
        this.service = service;
    }

    @GetMapping
    public String edit(Model model) {
        if (!model.containsAttribute("companyProfileForm")) {
            CompanyProfileForm form = service.findProfile()
                    .map(CompanyProfileForm::from)
                    .orElseGet(CompanyProfileForm::new);
            model.addAttribute("companyProfileForm", form);
        }
        return "company/profile";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute CompanyProfileForm companyProfileForm,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "company/profile";
        }

        service.save(companyProfileForm);
        redirectAttributes.addFlashAttribute("successMessage", "Фирменият профил беше запазен.");
        return "redirect:/company";
    }
}
