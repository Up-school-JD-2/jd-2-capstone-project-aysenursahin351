package io.upschool.service;

import io.upschool.dto.company.CompanySaveRequest;
import io.upschool.dto.company.CompanySaveResponse;
import io.upschool.dto.company.CompanyUpdateRequest;
import io.upschool.dto.BaseResponse;
import io.upschool.entity.Company;
import io.upschool.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Lazy


public class CompanyService {
    private final CompanyRepository companyRepository;

    public List<CompanySaveResponse> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    public Company getCompanyById4Repo(Long id) {
        return companyRepository.findById(id)
                .orElse(null); // Varsa uçuşu, yoksa null döndürür.
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

    public BaseResponse<CompanySaveResponse> deleteCompany(Long id) {
        Optional<Company> optionalCompany = companyRepository.findById(id);

        CompanySaveResponse companyResponse;
        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();

            if (company.getStatus() == 0) {
                return BaseResponse.<CompanySaveResponse>builder()
                        .status(404)
                        .isSuccess(false)
                        .error("Company has already deleted.")
                        .data(null) // Bunu ekleyerek data alanını null olarak belirtebiliriz
                        .build();
            
            }

            // Set the status to "deleted" and save
            company.setStatus(0);
            companyResponse=  convertToResponse(company);
            companyRepository.save(company);


        } else {
            return BaseResponse.<CompanySaveResponse>builder()
                    .status(404)
                    .isSuccess(false)
                    .error("Company not found")
                    .data(null) // Bunu ekleyerek data alanını null olarak belirtebiliriz
                    .build();
        }
        return BaseResponse.<CompanySaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(companyResponse)
                .build();   
    }
    


    public CompanySaveResponse convertToResponse(Company company) {
        return CompanySaveResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .status(company.getStatus())
                .build();
    }
    public List<CompanySaveResponse> searchCompanyByName(String name) {
        List<Company> companies = companyRepository.flexibleSearch(name);
        return companies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private Company convertToEntity(CompanySaveRequest companyRequest) {
        return Company.builder()
                .name(companyRequest.getName())
                .status(1)
                .build();
    }

    private void updateCompanyFromRequest(Company company, CompanyUpdateRequest companyRequest) {
        company.setName(companyRequest.getName());
    }
}
