package be.jovacon.kafka.connect.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.SystemPropertiesPropertySource;
import org.junit.jupiter.api.Test;

class SslKitTest {

  @Test
  void getTrustManagerFactory() throws Exception {
    InputStream caCertificate = this.getClass().getClassLoader().getResourceAsStream("ca.crt");
    TrustManagerFactory tmf = SslKit.getTrustManagerFactory(caCertificate);

    assertNotNull(tmf);
  }

  @Test
  void getCaSslSocketFactory() throws Exception {
    InputStream caCertificate = this.getClass().getClassLoader().getResourceAsStream("ca.crt");

    SSLSocketFactory socketFactory = SslKit.getSocketFactory(caCertificate, "TLSV1.1");

    assertNotNull(socketFactory);
  }

  @Test
  void getMutualSSLSocketFactory() throws Exception {
    InputStream caCertificate = this.getClass().getClassLoader().getResourceAsStream("ca.crt");
    InputStream clientCertificate = this.getClass().getClassLoader().getResourceAsStream("client.crt");
    InputStream clientKey = this.getClass().getClassLoader().getResourceAsStream("client.key");

    SSLSocketFactory socketFactory = SslKit.getSocketFactory(caCertificate, clientCertificate, clientKey, "TLSV1.2");

    assertNotNull(socketFactory);
  }
}