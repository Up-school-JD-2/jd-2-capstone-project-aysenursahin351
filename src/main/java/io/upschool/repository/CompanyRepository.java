package io.upschool.repository;
import io.upschool.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Custom query methods can be added if needed
}