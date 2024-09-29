package paengbeom.syono.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import paengbeom.syono.entity.ConnectedCompany;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectedCompanyRepository extends JpaRepository<ConnectedCompany, Long> {
    List<ConnectedCompany> findByUserEmail(String userEmail);

    Optional<ConnectedCompany> findByUserEmailAndCompanyName(String userEmail, String companyName);
    Optional<ConnectedCompany> findByUserEmailAndCompanyCode(String userEmail, String companyCode);
}
