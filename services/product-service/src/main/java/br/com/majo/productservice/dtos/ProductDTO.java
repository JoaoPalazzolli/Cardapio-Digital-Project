package br.com.majo.productservice.dtos;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class ProductDTO extends RepresentationModel<ProductDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String urlImage;
    private Boolean soldOut;
    private Date createAt;

    private String categoryId;
    private UUID restaurantId;
}
