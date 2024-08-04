package br.com.majo.restaurantservice.dtos;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class RestaurantDTO extends RepresentationModel<RestaurantDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private Date createAt;
}
