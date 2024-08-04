package br.com.majo.categoryservice.infra.external.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ProductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String urlImage;
    private Boolean soldOut;
}
