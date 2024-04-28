package ee.carlrobert.llm.client.bedrock;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.anthropic.ClaudeClient;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionRequest;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionResponse;
import ee.carlrobert.llm.client.llama.LlamaClient;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BedrockClient {

  private static final Logger LOG = LoggerFactory.getLogger(BedrockClient.class);
  private static final MediaType APPLICATION_JSON =
      MediaType.get("application/json; charset=utf-8");
  private final OkHttpClient httpClient;
  private final String baseUrl;

  public BedrockClient(String awsRegion, String awsKey, String awsSecret,
                       OkHttpClient.Builder httpClientBuilder) {
    AwsSigV4Signer awsSigV4Signer = new AwsSigV4Signer(awsKey, awsSecret, awsRegion, "bedrock");
    this.httpClient = httpClientBuilder
        .addInterceptor(new AwsSigningInterceptor(awsSigV4Signer))
        .build();
    this.baseUrl = String.format(PropertiesLoader.getValue("aws.bedrock.baseUrl"), awsRegion);
  }

  public LlamaCompletionResponse getCompletion(BedrockModel model, LlamaCompletionRequest request)
      throws JsonProcessingException {
    logModelTypeMismatch(model, "LLAMA");
    return executeCompletionRequest(model, request, LlamaCompletionResponse.class);
  }

  public ClaudeCompletionResponse getCompletion(BedrockModel model, ClaudeCompletionRequest request)
      throws JsonProcessingException {
    logModelTypeMismatch(model, "CLAUDE");
    return executeCompletionRequest(model, request, ClaudeCompletionResponse.class);
  }

  public EventSource getCompletionAsync(
      BedrockModel model,
      LlamaCompletionRequest request,
      CompletionEventListener<String> eventListener) throws JsonProcessingException {
    logModelTypeMismatch(model, "LLAMA");
    return executeCompletionRequestAsync(model, request,
        LlamaClient.getEventSourceListener(eventListener));
  }

  public EventSource getCompletionAsync(
      BedrockModel model,
      ClaudeCompletionRequest request,
      CompletionEventListener<String> eventListener) throws JsonProcessingException {
    logModelTypeMismatch(model, "CLAUDE");
    return executeCompletionRequestAsync(model, request,
        ClaudeClient.getCompletionEventSourceListener(eventListener));
  }

  private <T> T executeCompletionRequest(BedrockModel model, Object request, Class<T> responseType)
      throws JsonProcessingException {
    BedrockInvokeRequestParams requestParams =
        new BedrockInvokeRequestParams.Builder().setStream(false).build();
    String requestBody = DeserializationUtil.OBJECT_MAPPER.writeValueAsString(request);
    try (var response = httpClient.newCall(buildHttpRequest(requestParams, model, requestBody))
        .execute()) {
      return DeserializationUtil.mapResponse(response, responseType);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private EventSource executeCompletionRequestAsync(BedrockModel model, Object request,
                                                    CompletionEventSourceListener<String> listener)
      throws JsonProcessingException {
    BedrockInvokeRequestParams requestParams =
        new BedrockInvokeRequestParams.Builder().setStream(true).build();
    String requestBody = DeserializationUtil.OBJECT_MAPPER.writeValueAsString(request);
    return EventSources.createFactory(httpClient)
        .newEventSource(buildHttpRequest(requestParams, model, requestBody), listener);
  }


  private void logModelTypeMismatch(BedrockModel model, String expectedTypePrefix) {
    if (!model.type.startsWith(expectedTypePrefix)) {
      LOG.warn(
          "Model " + model.label + " is called using " + expectedTypePrefix + " request format.");
    }
  }

  private Request buildHttpRequest(BedrockInvokeRequestParams requestParams, BedrockModel model,
                                   String request) {
    RequestBody requestBody;
    requestBody = RequestBody.create(request, APPLICATION_JSON);
    Map<String, String> headersMap = new HashMap<>();
    headersMap.put("X-Amzn-Bedrock-Accept", APPLICATION_JSON.toString());
    headersMap.put("Content-Type", APPLICATION_JSON.toString());
    addOptionalHeaders(headersMap, requestParams);
    Headers headers = Headers.of(headersMap);
    String invokeEndpoint = requestParams.isStream() ? "invoke-with-response-stream" : "invoke";
    HttpUrl url = Objects.requireNonNull(
        HttpUrl.parse(baseUrl + "/model/" + model.code + "/" + invokeEndpoint)
    ).newBuilder().build();
    return new Request.Builder().url(url).headers(headers).post(requestBody).build();
  }

  private void addOptionalHeaders(Map<String, String> headersMap,
                                  BedrockInvokeRequestParams requestParams) {
    if (requestParams.getGuardrailIdentifier() != null
        && requestParams.getGuardrailVersion() != null) {
      headersMap.put("X-Amzn-Bedrock-GuardrailIdentifier", requestParams.getGuardrailIdentifier());
      headersMap.put("X-Amzn-Bedrock-GuardrailVersion", requestParams.getGuardrailVersion());
    }
    headersMap.put("X-Amzn-Bedrock-Trace", requestParams.isTraceEnabled() ? "ENABLED" : "DISABLED");
  }

}
