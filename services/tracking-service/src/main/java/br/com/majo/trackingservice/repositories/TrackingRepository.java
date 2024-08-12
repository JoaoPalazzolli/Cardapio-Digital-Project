package br.com.majo.trackingservice.repositories;

import br.com.majo.trackingservice.domains.TrackingDomain;
import org.springframework.data.repository.CrudRepository;

public interface TrackingRepository extends CrudRepository<TrackingDomain, String> {
}
