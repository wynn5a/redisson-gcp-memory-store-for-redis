import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.net.ssl.TrustManagerFactory;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

/**
 * @author wynn5a
 */
public class App {

  public static void main(String[] args) {
    Config config = new Config();
    SingleServerConfig singleServerConfig = config.useSingleServer();
    singleServerConfig.setAddress("rediss://{your-redis-ip}:{your-redis-port}");
    singleServerConfig.setPassword("your auth string if have");
    singleServerConfig.setSslTrustManagerFactory(getTrustMangerFactory());
    RedissonClient redisson = Redisson.create(config);
    System.out.println(redisson.getKeys().getKeysStream().collect(Collectors.joining(", ")));
    redisson.shutdown();
  }

  private static TrustManagerFactory getTrustMangerFactory() {
    String CA_FILE = Objects.requireNonNull(App.class.getResource("{your-server-ca-downloaded}.pem")).getPath();

    try (FileInputStream fis = new FileInputStream(CA_FILE)) {
      X509Certificate ca = (X509Certificate) CertificateFactory.getInstance("X.509")
          .generateCertificate(new BufferedInputStream(fis));

      KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
      ks.load(null, null);
      ks.setCertificateEntry("GCP-MEMORY_STORE_FOR_REDIS-CA", ca);

      TrustManagerFactory tmf = TrustManagerFactory
          .getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(ks);
      return tmf;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
