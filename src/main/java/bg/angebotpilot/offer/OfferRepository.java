package bg.angebotpilot.offer;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    @EntityGraph(attributePaths = {"customer", "items"})
    List<Offer> findAllByOrderByIssueDateDescIdDesc();

    @EntityGraph(attributePaths = {"customer", "items"})
    Optional<Offer> findDetailedById(Long id);

    @EntityGraph(attributePaths = {"customer", "items"})
    Optional<Offer> findDetailedByPublicToken(String publicToken);

    long countByStatus(OfferStatus status);
}
