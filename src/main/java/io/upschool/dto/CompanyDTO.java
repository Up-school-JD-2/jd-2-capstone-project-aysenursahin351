package io.upschool.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyDTO {
    private Long id;
    private String name;
}
