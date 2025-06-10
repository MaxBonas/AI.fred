package com.example.streambot;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.CompletionRequest;

import com.example.streambot.EnvUtils;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
    private final OpenAiService service;

    public OpenAIService() {
        String apiKey = EnvUtils.get("OPENAI_API_KEY");
        String baseUrl = EnvUtils.get("OPENAI_BASE_URL", "https://api.openai.com/");

        OkHttpClient client = OpenAiService.defaultClient(apiKey, Duration.ofSeconds(60));
        ObjectMapper mapper = OpenAiService.defaultObjectMapper();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        service = new OpenAiService(api, client.dispatcher().executorService());
        logger.info("OpenAIService inicializado usando {}", baseUrl);
    }

    public String ask(String prompt) {
        String model = EnvUtils.get("OPENAI_MODEL", "text-davinci-003");

        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt)
                .model(model)
                .maxTokens(50)
                .build();
        return service.createCompletion(request)
                .getChoices()
                .get(0)
                .getText();
    }


}
