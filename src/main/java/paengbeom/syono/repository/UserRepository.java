package paengbeom.syono.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import paengbeom.syono.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
}
