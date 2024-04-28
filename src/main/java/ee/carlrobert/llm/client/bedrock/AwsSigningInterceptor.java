package ee.carlrobert.llm.client.bedrock;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;


public class AwsSigningInterceptor implements Interceptor {

  private final AwsSigV4Signer signer;

  public AwsSigningInterceptor(AwsSigV4Signer signer) {
    this.signer = signer;
  }

  @NotNull
  @Override
  public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();
    Request signedRequest = null;
    try {
      signedRequest = signer.signRequest(originalRequest);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return chain.proceed(signedRequest);
  }
}

