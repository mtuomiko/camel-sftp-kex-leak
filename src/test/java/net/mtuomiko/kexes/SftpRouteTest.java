package net.mtuomiko.kexes;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

class SftpRouteTest extends SftpContainerBaseTest {

    @Override
    protected Class<?> getMainClass() {
        // The main class of the application to test
        return MyApplication.class;
    }

    @Test
    void sftpRouteTest() {
        var sftpServer1 = new GenericContainer(DockerImageName.parse("atmoz/sftp:alpine-3.7"))
                .withExposedPorts(22)
                .withCommand("myuser1:Hunter2:::upload1");
        sftpServer1.start();

        var sftpServer2 = new GenericContainer(DockerImageName.parse("atmoz/sftp:alpine-3.7"))
                .withExposedPorts(22)
                .withCommand("myuser2:Hunter2:::upload2");
        sftpServer2.start();

        sendBody("direct:start-route", null);
    }

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        var properties = new Properties();
        properties.setProperty("sftp1.port", sftpServer1.getMappedPort(22).toString());
        properties.setProperty("sftp2.port", sftpServer2.getMappedPort(22).toString());
        return properties;
    }
}
