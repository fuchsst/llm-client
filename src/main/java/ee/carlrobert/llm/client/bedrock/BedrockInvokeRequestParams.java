package ee.carlrobert.llm.client.bedrock;

public class BedrockInvokeRequestParams {
  private final String guardrailIdentifier;
  private final String guardrailVersion;
  private final boolean traceEnabled;
  private final boolean stream;

  public BedrockInvokeRequestParams(BedrockInvokeRequestParams.Builder builder) {
    this.guardrailIdentifier = builder.guardrailIdentifier;
    this.guardrailVersion = builder.guardrailVersion;
    this.traceEnabled = builder.traceEnabled;
    this.stream = builder.stream;
  }

  public String getGuardrailIdentifier() {
    return guardrailIdentifier;
  }

  public String getGuardrailVersion() {
    return guardrailVersion;
  }

  public boolean isTraceEnabled() {
    return traceEnabled;
  }

  public boolean isStream() {
    return stream;
  }

  public static class Builder {

    private String guardrailIdentifier;
    private String guardrailVersion;
    private boolean traceEnabled;
    private boolean stream;


    public BedrockInvokeRequestParams.Builder setStream(boolean stream) {
      this.stream = stream;
      return this;
    }

    public BedrockInvokeRequestParams.Builder setGuardrailIdentifier(String guardrailIdentifier) {
      this.guardrailIdentifier = guardrailIdentifier;
      return this;
    }

    public BedrockInvokeRequestParams.Builder setGuardrailVersion(String guardrailVersion) {
      this.guardrailVersion = guardrailVersion;
      return this;
    }

    public BedrockInvokeRequestParams.Builder setTraceEnabled(boolean traceEnabled) {
      this.traceEnabled = traceEnabled;
      return this;
    }

    public BedrockInvokeRequestParams build() {
      return new BedrockInvokeRequestParams(this);
    }
  }
}
