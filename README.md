# camel-sftp-kex-leak

Demo for a bug in Camel SFTP where [keyExchangeProtocols settings](https://camel.apache.org/components/4.0.x/sftp-component.html#_endpoint_query_option_keyExchangeProtocols) leak between endpoints through Jsch.

This project requires JDK 17, and uses TestContainers so running the test requires a container runtime (like Docker).

Run the test for example with `.\mvnw.cmd test` on Windows or `./mvnw test` otherwise.

When running the [SftpRouteTest](src/test/java/net/mtuomiko/kexes/SftpRouteTest.java), you can see from the Jsch logging output that both endpoints end up sending the same `JSCH -> client proposal: KEX algorithms: diffie-hellman-group14-sha1,ext-info-c` proposal even though `diffie-hellman-group14-sha1` is only configured on one of them. The [default list from Jsch](https://github.com/mwiede/jsch/blob/jsch-0.2.11/src/main/java/com/jcraft/jsch/JSch.java#L42-L43) has more options for kex algorithms.

Sample output:

```
...
[main] org.apache.camel.component.file.remote.RemoteFileProducer DEBUG Not already connected/logged in. Connecting to: sftp://myuser1@localhost:53316/upload1?jschLoggingLevel=INFO&keyExchangeProtocols=diffie-hellman-group14-sha1&password=xxxxxx
[main] org.apache.camel.component.file.remote.SftpOperations DEBUG Using KEX: diffie-hellman-group14-sha1
...
[main] org.apache.camel.component.file.remote.SftpOperations INFO  JSCH -> client proposal: KEX algorithms: diffie-hellman-group14-sha1,ext-info-c
...
[main] org.apache.camel.component.file.remote.RemoteFileProducer DEBUG Not already connected/logged in. Connecting to: sftp://myuser2@localhost:53319/upload2?jschLoggingLevel=INFO&password=xxxxxx
...
[main] org.apache.camel.component.file.remote.SftpOperations INFO  JSCH -> client proposal: KEX algorithms: diffie-hellman-group14-sha1,ext-info-c
...
```

### Diagnosis

The issue, as far as I see it, is at [SftpOperations.java#L231](https://github.com/apache/camel/blob/camel-4.2.0/components/camel-ftp/src/main/java/org/apache/camel/component/file/remote/SftpOperations.java#L231) where the key exchange protocols are set to the Jsch class configuration which is a `static Hashtable<String, String> config = new Hashtable<>()` ([JSch.java#L40](https://github.com/mwiede/jsch/blob/jsch-0.2.11/src/main/java/com/jcraft/jsch/JSch.java#L40)).

In the Camel SFTP code you can also see that, for example, server host key algorithms are set to a Jsch Session, not the class configuration: [SftpOperations.java#L343](https://github.com/apache/camel/blob/camel-4.2.0/components/camel-ftp/src/main/java/org/apache/camel/component/file/remote/SftpOperations.java#L343). I believe this is where the key exchange protocol configuration should also be set, unless there's something preventing that (for some reason?).

