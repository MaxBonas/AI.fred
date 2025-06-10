package com.example.streambot;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import io.github.cdimascio.dotenv.Dotenv;

public class OpenAIService {
    private final OpenAiService service;

    public OpenAIService() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String apiKey = dotenv.get("OPENAI_API_KEY");
        service = new OpenAiService(apiKey);
    }

    public String ask(String prompt) {
        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt)
                .model("text-davinci-003")
                .maxTokens(50)
                .build();
        return service.createCompletion(request)
                .getChoices()
                .get(0)
                .getText();
    }
}
