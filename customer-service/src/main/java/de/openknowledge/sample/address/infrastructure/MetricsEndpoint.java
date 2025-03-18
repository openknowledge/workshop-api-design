package de.openknowledge.sample.address.infrastructure;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

import org.apache.geronimo.microprofile.metrics.common.jaxrs.SecurityValidator;
import org.apache.geronimo.microprofile.metrics.jaxrs.CdiMetricsEndpoints;

@Specializes
@ApplicationScoped
public class MetricsEndpoint extends CdiMetricsEndpoints {

    @PostConstruct
    public void disableSecurity() {
        setSecurityValidator(new SecurityValidator());
        setPrometheus(new PrometheusFormatter());
    }
}
