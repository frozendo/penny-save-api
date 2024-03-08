package com.frozendo.pennysave;

import com.frozendo.pennysave.config.properties.PennySaveEmailProperties;
import com.frozendo.pennysave.config.properties.RabbitProperties;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import com.pennysave.frozendo.config.IntegrationTestConfig;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@IntegrationTestConfig
@EnableConfigurationProperties({
        RabbitProperties.class, PennySaveEmailProperties.class
})
public class PersonModuleIntegrationTest extends IntegrationTestsBase {

//    @RegisterExtension
//    protected static GreenMailExtension greenMail = new GreenMailExtension(getServerStartup())
//            .withConfiguration(GreenMailConfiguration.aConfig().withUser("penny-save-mail", "mailpassword"))
//            .withPerMethodLifecycle(true);

//    protected static ServerSetup getServerStartup() {
//        final ServerSetup serverSetup = new ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP);
//        serverSetup.setServerStartupTimeout(5000L);
//        return serverSetup;
//    }

}
