package com.frozendo.pennysave;

import com.frozendo.pennysave.config.properties.RabbitProperties;
import com.pennysave.frozendo.config.IntegrationTestConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@IntegrationTestConfig
@EnableConfigurationProperties({
        RabbitProperties.class
})
public class PersonModuleIntegrationTest extends IntegrationTestsBase {
}
