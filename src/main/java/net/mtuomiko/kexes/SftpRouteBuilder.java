package net.mtuomiko.kexes;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.direct;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.sftp;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class SftpRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from(direct("start-route"))
                .routeId("start")
                .setBody(constant("ABC"))
                .setHeader(Exchange.FILE_NAME, constant("file.txt"))
                // This sftp producer has a custom key exchange protocol configuration
                .log(LoggingLevel.INFO, "First SFTP producer begins")
                .to(
                        sftp("myuser1@localhost:{{sftp1.port}}/upload1")
                                .password("Hunter2")
                                .jschLoggingLevel(LoggingLevel.INFO)
                                .keyExchangeProtocols("diffie-hellman-group14-sha1")
                )
                .log(LoggingLevel.INFO, "Second SFTP producer begins")
                // This sftp producer does not have any custom kex protocol configuration, so it should use Jsch
                // defaults. But the kex configuration from above leaks into this endpoint
                .to(
                        sftp("myuser2@localhost:{{sftp2.port}}/upload2")
                                .password("Hunter2")
                                .jschLoggingLevel(LoggingLevel.INFO)
                );
    }
}
