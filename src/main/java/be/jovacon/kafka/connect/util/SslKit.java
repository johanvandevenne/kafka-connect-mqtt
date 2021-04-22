package be.jovacon.kafka.connect.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.logging.log4j.util.Strings;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

/**
 * A way to get SSL connections using regular PEM formatted certificate and key files.
 * Based on https://gist.github.com/sharonbn/4104301
 */

public class SslKit {
  public static SSLSocketFactory getSocketFactory(final File caCert, final File clientCert, final File clientKey, final String tlsVersion) throws Exception {
    Objects.requireNonNull(caCert, "caCert is required");
    Objects.requireNonNull(clientCert, "clientCert is required");
    Objects.requireNonNull(clientKey, "clientKey is required");
    if (Strings.isEmpty(tlsVersion)) {
      throw new IllegalArgumentException("tlsVersion is required");
    }

    if (!caCert.canRead()) {
      throw new IllegalArgumentException("Can't read CA cert file at: " + caCert.getAbsolutePath());
    }
    if (!clientCert.canRead()) {
      throw new IllegalArgumentException("Can't read client cert file at: " + clientCert.getAbsolutePath());
    }
    if (!clientKey.canRead()) {
      throw new IllegalArgumentException("Can't read client key file at: " + clientKey.getAbsolutePath());
    }
    return getSocketFactory(
        new FileInputStream(caCert),
        new FileInputStream(clientCert),
        new FileInputStream(clientKey),
        tlsVersion);
  }

  /**
   * Setup a SSLSocketFactory for mutual TLS. The server identity is verified. And then the client certificate is
   * offered to the server to that it can verify the identity of the client.
   * @param caCert The PEM encoded CA certificate to verify server identity with.
   * @param clientCert The PEM encoded client certificate to use.
   * @param clientKey The PEM encoded private key of the client.
   * @param tlsVersion The TLS algorithm to use. See: <code>Security.getAlgorithms("SSLContext");</code>
   * @return A newly created SSLSocketFactory instance.
   * @throws Exception there are so many exceptions in java.security... But they all basically amount to: 'can't connect'
   */
  static SSLSocketFactory getSocketFactory(final InputStream caCert, final InputStream clientCert,
      final InputStream clientKey, final String tlsVersion) throws Exception {
    addBouncyCastleMaybe();
    // Use CA certificate to establish server trust.
    TrustManagerFactory tmf = getTrustManagerFactory(caCert);

    // Load client certificate
    X509Certificate client = getX509Certificate(clientCert);

    // Load client key
    PEMParser parser = new PEMParser(new InputStreamReader(clientKey));
    KeyPair key = new JcaPEMKeyConverter().getKeyPair((PEMKeyPair) parser.readObject());
    parser.close();

    // Client key and certificate are used to authenticate with server.
    KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    clientKeyStore.load(null, null);
    clientKeyStore.setCertificateEntry("certificate", client);
    clientKeyStore.setKeyEntry("private-key", key.getPrivate(), "".toCharArray(), new Certificate[]{client});
    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(clientKeyStore, "".toCharArray());

    // Create SSL socket factory
    SSLContext context = SSLContext.getInstance(tlsVersion);
    context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

    return context.getSocketFactory();
  }

  public static SSLSocketFactory getSocketFactory(final File caCert, final String tlsVersion) throws Exception {
    Objects.requireNonNull(caCert, "caCert is required");
    if (Strings.isEmpty(tlsVersion)) {
      throw new IllegalArgumentException("tlsVersion is required");
    }

    if (!caCert.canRead()) {
      throw new IllegalArgumentException("Can't read CA cert file at: " + caCert.getAbsolutePath());
    }
    return getSocketFactory(new FileInputStream(caCert), tlsVersion);
  }

  /**
   * Setup a SSLSocketFactory that verifies server identity using the provided CA certificate.
   * @param caCert The PEM encoded CA certificate to verify server identity with.
   * @param tlsVersion The TLS algorithm to use. See: <code>Security.getAlgorithms("SSLContext");</code>
   * @return newly created SSLSocketFactory.
   * @throws Exception here are so many exceptions in java.security... But they all basically amount to: 'can't connect'
   */
  static SSLSocketFactory getSocketFactory(final InputStream caCert, final String tlsVersion) throws Exception {
    addBouncyCastleMaybe();

    // Use CA certificate to establish server trust.
    TrustManagerFactory tmf = getTrustManagerFactory(caCert);

    // Create SSL socket factory
    SSLContext context = SSLContext.getInstance(tlsVersion);
    context.init(null, tmf.getTrustManagers(), SecureRandom.getInstance("SHA1PRNG"));

    return context.getSocketFactory();
  }

  static TrustManagerFactory getTrustManagerFactory(final InputStream caCert) throws Exception {
    addBouncyCastleMaybe();

    // Load CA certificates
    X509Certificate ca = getX509Certificate(caCert);

    // CA certificate is used to verify server identity.
    KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    caKeyStore.load(null, null);
    caKeyStore.setCertificateEntry("ca-certificate", ca);
    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(caKeyStore);

    return tmf;
  }

  private static X509Certificate getX509Certificate(InputStream cert) throws CertificateException, IOException {
    PEMParser parser = new PEMParser(new InputStreamReader(cert));
    X509Certificate ca = new JcaX509CertificateConverter().getCertificate((X509CertificateHolder) parser.readObject());
    parser.close();
    return ca;
  }

  private static void addBouncyCastleMaybe() {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }
}
