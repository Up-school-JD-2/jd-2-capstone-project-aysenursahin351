package io.upschool.service;

import io.upschool.dto.CompanyDTO;
import io.upschool.entity.Company;
import io.upschool.repository.CompanyRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static io.upschool.entity.Company.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {
    private final CompanyRepository companyRepository;

    public List<CompanyDTO> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CompanyDTO getCompanyById(Long id) {
        return companyRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public CompanyDTO saveCompany(CompanyDTO companyDTO) {
        Company company = convertToEntity(companyDTO);
        company = companyRepository.save(company);
        return convertToDTO(company);
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    private CompanyDTO convertToDTO(@NotNull Company company) {
        return CompanyDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .build();
    }

    private Company convertToEntity(@NotNull CompanyDTO companyDTO) {
        return Company.builder()
                .id(companyDTO.getId())
                .name(companyDTO.getName())
                .build();
    }
}

