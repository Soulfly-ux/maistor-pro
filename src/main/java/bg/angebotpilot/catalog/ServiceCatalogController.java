package bg.angebotpilot.catalog;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/services")
public class ServiceCatalogController {

    private final ServiceItemRepository repository;

    public ServiceCatalogController(ServiceItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("services", repository.findAllByOrderByActiveDescCategoryAscNameAsc());
        return "services/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        if (!model.containsAttribute("serviceItemForm")) {
            model.addAttribute("serviceItemForm", new ServiceItemForm());
        }
        return "services/new";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute ServiceItemForm serviceItemForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "services/new";
        }

        ServiceItem serviceItem = repository.save(serviceItemForm.toServiceItem());
        redirectAttributes.addFlashAttribute("successMessage",
                "Услугата „" + serviceItem.getName() + "“ беше добавена.");
        return "redirect:/services";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ServiceItem serviceItem = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Услугата не беше намерена"));
        serviceItem.toggleActive();
        repository.save(serviceItem);
        redirectAttributes.addFlashAttribute("successMessage",
                serviceItem.isActive() ? "Услугата беше активирана." : "Услугата беше архивирана.");
        return "redirect:/services";
    }
}
