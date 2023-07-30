package io.upschool.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirportDTO {
    private Long id;
    private String name;
    private String country;
    private String code;
}
