package io.upschool.dto.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySaveResponse {
    private Long id;
    private String name;
    private String error; // Hata durumu için alan eklendi
}
