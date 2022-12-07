package de.openknowledge.sample.address.infrastructure;

import org.apache.geronimo.microprofile.metrics.common.jaxrs.SecurityValidator;
import org.apache.geronimo.microprofile.metrics.jaxrs.CdiMetricsEndpoints;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

import java.util.logging.Logger;

@Specializes
@ApplicationScoped
public class MetricsEndpoint extends CdiMetricsEndpoints {

    private static final Logger LOG = Logger.getLogger(MetricsEndpoint.class.getSimpleName());

    @PostConstruct
    public void disableSecurity() {
        LOG.info("Unser Security Validator - postconstruct");
        setSecurityValidator(new SecurityValidator());
    }
}
