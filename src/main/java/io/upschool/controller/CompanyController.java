package io.upschool.controller;

import io.upschool.dto.company.CompanySaveRequest;
import io.upschool.dto.company.CompanySaveResponse;
import io.upschool.dto.company.CompanyUpdateRequest;
import io.upschool.dto.BaseResponse;
import io.upschool.entity.Company;
import io.upschool.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<CompanySaveResponse>>> getAllCompanies() {
        List<CompanySaveResponse> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(BaseResponse.<List<CompanySaveResponse>>builder()
                .status(200)
                .isSuccess(true)
                .data(companies)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CompanySaveResponse>> getCompanyById(@PathVariable Long id) {
        BaseResponse<CompanySaveResponse> companyResponse = companyService.getCompanyById(id);
        if (companyResponse.isSuccess()) {
            return ResponseEntity.ok(companyResponse);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
        }
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CompanySaveResponse>> saveCompany(@RequestBody CompanySaveRequest companyRequest) {
        BaseResponse<CompanySaveResponse> savedCompany = companyService.saveCompany(companyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCompany);
    }

    @PostMapping("/{id}")
    public ResponseEntity<BaseResponse<CompanySaveResponse>> updateCompany(@PathVariable Long id, @RequestBody CompanyUpdateRequest companyRequest) {
        Company updatedCompany = companyService.updateCompany(id, companyRequest);
        if (updatedCompany != null) {
            CompanySaveResponse companySaveResponse = companyService.convertToResponse(updatedCompany);
            return ResponseEntity.ok(BaseResponse.<CompanySaveResponse>builder()
                    .status(200)
                    .isSuccess(true)
                    .data(companySaveResponse)
                    .build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
