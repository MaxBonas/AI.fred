package com.example.streambot;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.CompletionRequest;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;

public class OpenAIService {
    private final OpenAiService service;
    private final String model;
    private final Dotenv dotenv;

    public OpenAIService() {
        this.dotenv = Dotenv.configure().ignoreIfMissing().load();
        String apiKey = dotenv.get("OPENAI_API_KEY");
        String baseUrl = dotenv.get("OPENAI_BASE_URL", "https://api.openai.com/");
        this.model = dotenv.get("OPENAI_MODEL", "text-davinci-003");

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
    }

    public String ask(String prompt) {
        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt)
                .model(this.model)
                .maxTokens(50)
                .build();
        return service.createCompletion(request)
                .getChoices()
                .get(0)
                .getText();
    }
}
