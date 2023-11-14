package net.mtuomiko.kexes;

import org.apache.camel.test.main.junit5.CamelMainTestSupport;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

abstract class SftpContainerBaseTest extends CamelMainTestSupport {
    static final GenericContainer sftpServer1;
    static final GenericContainer sftpServer2;

    static {
        sftpServer1 = new GenericContainer(DockerImageName.parse("atmoz/sftp:alpine-3.7"))
                .withExposedPorts(22)
                .withCommand("myuser1:Hunter2:::upload1");
        sftpServer1.start();

        sftpServer2 = new GenericContainer(DockerImageName.parse("atmoz/sftp:alpine-3.7"))
                .withExposedPorts(22)
                .withCommand("myuser2:Hunter2:::upload2");
        sftpServer2.start();
    }

}
