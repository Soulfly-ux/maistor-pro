package bg.angebotpilot.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {

    List<ServiceItem> findAllByOrderByActiveDescCategoryAscNameAsc();

    List<ServiceItem> findByActiveTrueOrderByCategoryAscNameAsc();
}
