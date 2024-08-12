package br.com.majo.trackingservice.dtos;

import br.com.majo.trackingservice.infra.util.TrackingStatus;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class TrackingDTO extends RepresentationModel<TrackingDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;

    private TrackingStatus status;
    private String fromService;
    private String description;
    private Date createAt;
    private Date lastUpdate;
}
