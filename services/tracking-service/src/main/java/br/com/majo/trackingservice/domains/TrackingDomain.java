package br.com.majo.trackingservice.domains;

import br.com.majo.trackingservice.infra.util.TrackingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@RedisHash
public class TrackingDomain implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Indexed
    private String id;

    private TrackingStatus status;
    private String fromService;
    private String description;
    private Date createAt;
    private Date lastUpdate;
}
