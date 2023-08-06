package io.upschool.service;

import io.upschool.dto.company.CompanySaveRequest;
import io.upschool.dto.company.CompanySaveResponse;
import io.upschool.dto.company.CompanyUpdateRequest;
import io.upschool.dto.BaseResponse;
import io.upschool.entity.Company;
import io.upschool.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class CompanyService {
    private final CompanyRepository companyRepository;

    public List<CompanySaveResponse> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public BaseResponse<CompanySaveResponse> saveCompany(CompanySaveRequest companyRequest) {
        Company company = convertToEntity(companyRequest);
        company = companyRepository.save(company);
        CompanySaveResponse companySaveResponse = convertToResponse(company);
        return BaseResponse.<CompanySaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(companySaveResponse)
                .build();
    }

    public Company updateCompany(Long id, CompanyUpdateRequest companyRequest) {
        Company company = companyRepository.findById(id).orElse(null);
        if (company != null) {
            updateCompanyFromRequest(company, companyRequest);
            return companyRepository.save(company);
        } else {
            return null;
        }
    }

    public BaseResponse<CompanySaveResponse> getCompanyById(Long companyId) {
        Company company = companyRepository.findById(companyId).orElse(null);

        if (company != null) {
            CompanySaveResponse companyResponse = convertToResponse(company);
            return BaseResponse.<CompanySaveResponse>builder()
                    .status(200)
                    .isSuccess(true)
                    .data(companyResponse)
                    .build();
        } else {
            return BaseResponse.<CompanySaveResponse>builder()
                    .status(404)
                    .isSuccess(false)
                    .error("Company not found")
                    .data(null) // Bunu ekleyerek data alanını null olarak belirtebiliriz
                    .build();
        }
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    public CompanySaveResponse convertToResponse(Company company) {
        return CompanySaveResponse.builder()
                .id(company.getId())
                .name(company.getName())
                // Add other fields as needed
                .build();
    }


    private Company convertToEntity(CompanySaveRequest companyRequest) {
        return Company.builder()
                .name(companyRequest.getName())
                .build();
    }

    private void updateCompanyFromRequest(Company company, CompanyUpdateRequest companyRequest) {
        company.setName(companyRequest.getName());
    }
}
