package ee.carlrobert.llm.client.bedrock;

public enum BedrockModel {
  // TitanTextExpressV1("Amazon Titan Text G1 - Express", "1.x",
  // "amazon.titan-text-express-v1", "TITAN"),
  // TitanTextLiteV1("Amazon Titan Text G1 - Lite", "1.x",
  // "amazon.titan-text-lite-v1", "TITAN"),
  // TitanEmbeddingsTextV1("Amazon Titan Embeddings G1 - Text", "1.x",
  // "amazon.titan-embed-text-v1", "TITAN_EMBED_TEXT"),
  // TitanMultimodalEmbeddingsV1("Amazon Titan Multimodal Embeddings G1", "1.x",
  // "amazon.titan-embed-image-v1", "TITAN_EMBED_IMAGE"),
  // TitanImageGeneratorV1("Amazon Titan Image Generator G1", "1.x",
  // "amazon.titan-image-generator-v1", "TITAN_GEN_IMAGE"),
  AnthropicClaudeV2_0("Anthropic Claude", "2.0",
      "anthropic.claude-v2", "CLAUDE2"),
  AnthropicClaudeV2_1("Anthropic Claude", "2.1",
      "anthropic.claude-v2:1", "CLAUDE2"),
  AnthropicClaude3SonnetV1("Anthropic Claude 3 Sonnet", "1.0",
      "anthropic.claude-3-sonnet-20240229-v1:0", "CLAUDE3"),
  AnthropicClaude3HaikuV1("Anthropic Claude 3 Haiku", "1.0",
      "anthropic.claude-3-haiku-20240307-v1:0", "CLAUDE3"),
  AnthropicClaude3OpusV1("Anthropic Claude 3 Opus", "1.0",
      "anthropic.claude-3-opus-20240229-v1:0", "CLAUDE3"),
  AnthropicClaudeInstantV1("Anthropic Claude Instant", "1.x",
      "anthropic.claude-instant-v1", "CLAUDE"),
  // AI21Jurassic2MidV1("AI21 Labs Jurassic-2 Mid", "1.x",
  // "ai21.j2-mid-v1", "JURASSIC"),
  // AI21Jurassic2UltraV1("AI21 Labs Jurassic-2 Ultra", "1.x",
  // "ai21.j2-ultra-v1", "JURASSIC"),
  // CohereCommandV14("Cohere Command", "14.x",
  // "cohere.command-text-v14", "COHERE"),
  // CohereCommandLightV15("Cohere Command Light", "15.x",
  // "cohere.command-light-text-v14", "COHERE"),
  // CohereEmbedEnglishV3("Cohere Embed English", "3.x",
  // "cohere.embed-english-v3", "COHERE_EMBED"),
  // CohereEmbedMultilingualV3("Cohere Embed Multilingual", "3.x",
  // "cohere.embed-multilingual-v3", "COHERE_EMBED"),
  MetaLLama2Chat13B("Meta Llama 2 Chat 13B", "1.x",
      "meta.llama2-13b-chat-v1", "LLAMA2"),
  MetaLLama2Chat70B("Meta Llama 2 Chat 70B", "1.x",
      "meta.llama2-70b-chat-v1", "LLAMA2"),
  MetaLLama2Instruct8B("Meta Llama 3 8b Instruct", "1.0",
      "meta.llama3-8b-instruct-v1:0", "LLAMA3"),
  MetaLLama2Instruct70B("Meta Llama 3 70b Instruct", "1.0",
      "meta.llama3-70b-instruct-v1:0",
      "LLAMA2"),
  // MistralInstruct7B("Mistral AI Mistral 7B Instruct", "0.2",
  // "mistral.mistral-7b-instruct-v0:2", "MISTRAL"),
  // MistralMixtralInstruct8x7B("Mistral AI Mixtral 8X7B Instruct", "0.1",
  // "mistral.mixtral-8x7b-instruct-v0:1", "MIXTRAL"),
  // MistralLarge("Mistral AI Mistral Large", "1.0",
  // "mistral.mistral-large-2402-v1:0", "MISTRAL")
  ;
  public final String label;
  public final String version;
  public final String code;
  public final String type;


  private BedrockModel(String label, String version, String code, String type) {
    this.label = label;
    this.version = version;
    this.code = code;
    this.type = type;
  }
}
