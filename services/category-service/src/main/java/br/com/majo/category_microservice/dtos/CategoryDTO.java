package br.com.majo.category_microservice.dtos;

import br.com.majo.category_microservice.infra.external.dtos.ProductDTO;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO extends RepresentationModel<CategoryDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private Date createAt;

    private UUID restaurantId;
    private List<ProductDTO> products;
}
