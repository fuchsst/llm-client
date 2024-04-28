package ee.carlrobert.llm.client.bedrock;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class AwsSigV4Signer {

  private static final String ALGORITHM = "HmacSHA256";
  private static final String CANONICAL_URI = "/";
  private static final String TERMINATOR = "aws4_request";

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String service;

  public AwsSigV4Signer(String accessKey, String secretKey, String region, String service) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.service = service;
  }

  public Request signRequest(Request request) throws Exception {
    String amzDate = getAmzDate();
    HttpUrl url = request.url();
    Map<String, String> headers = new HashMap<>();
    request.headers().names()
        .forEach(name -> headers.put(name.toLowerCase(Locale.US), request.header(name)));
    headers.put("host", url.host());
    headers.put("x-amz-date", amzDate);
    String payloadHash = hash(request.body() != null ? request.body().toString() : "",
        StandardCharsets.UTF_8.name());
    String signedHeaders = "host;x-amz-date";
    String canonicalQueryString = url.encodedQuery() != null ? url.encodedQuery() : "";
    String canonicalRequest =
        buildCanonicalRequest(request.method(), CANONICAL_URI, canonicalQueryString, signedHeaders,
            headers, payloadHash);
    String credentialScope = buildCredentialScope(amzDate, region, service);
    String stringToSign = buildStringToSign(amzDate, credentialScope,
        hash(canonicalRequest, StandardCharsets.UTF_8.name()));
    String signature = calculateSignature(secretKey, stringToSign, amzDate, region, service);
    String authorizationHeader =
        buildAuthorizationHeader(accessKey, credentialScope, signedHeaders, signature);
    return request.newBuilder()
        .header("Authorization", authorizationHeader)
        .build();
  }

  private static final String ISO8601_BASIC_FORMAT = "yyyyMMdd'T'HHmmss'Z'";

  public static String getAmzDate() {
    SimpleDateFormat sdf = new SimpleDateFormat(ISO8601_BASIC_FORMAT, Locale.US);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sdf.format(System.currentTimeMillis());
  }

  public static String hash(String text, String charsetName)
      throws NoSuchAlgorithmException, UnsupportedEncodingException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(text.getBytes(charsetName));
    return bytesToHex(hash);
  }

  public static String calculateSignature(String secretKey, String stringToSign, String dateStamp,
                                          String regionName, String serviceName) throws Exception {
    byte[] baSecret = ("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8);
    byte[] baDate = hmacSHA256(dateStamp, baSecret);
    byte[] baRegion = hmacSHA256(regionName, baDate);
    byte[] baService = hmacSHA256(serviceName, baRegion);
    byte[] baSigning = hmacSHA256("aws4_request", baService);
    return bytesToHex(hmacSHA256(stringToSign, baSigning));
  }

  public static String buildAuthorizationHeader(String accessKey, String credentialScope,
                                                String signedHeaders, String signature) {
    return String.format("AWS4-HMAC-SHA256 Credential=%s/%s, SignedHeaders=%s, Signature=%s",
        accessKey, credentialScope, signedHeaders, signature);
  }

  public static String buildCredentialScope(String date, String region, String service) {
    return date.substring(0, 8) + "/" + region + "/" + service + "/aws4_request";
  }

  public static String buildCanonicalRequest(String method, String canonicalUri,
                                             String canonicalQueryString, String signedHeaders,
                                             Map<String, String> headers, String payloadHash) {
    StringBuilder sb = new StringBuilder();
    sb.append(method).append('\n')
        .append(canonicalUri).append('\n')
        .append(canonicalQueryString).append('\n');

    headers.forEach((key, value) -> sb.append(key).append(':').append(value).append('\n'));
    sb.append('\n').append(signedHeaders).append('\n').append(payloadHash);

    return sb.toString();
  }

  public static String buildStringToSign(String amzDate, String credentialScope,
                                         String hashedCanonicalRequest) {
    return "AWS4-HMAC-SHA256\n" + amzDate + "\n" + credentialScope + "\n" + hashedCanonicalRequest;
  }

  private static byte[] hmacSHA256(String data, byte[] key) throws Exception {
    String algorithm = "HmacSHA256";
    Mac mac = Mac.getInstance(algorithm);
    mac.init(new SecretKeySpec(key, algorithm));
    return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}

