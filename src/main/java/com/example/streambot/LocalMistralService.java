package com.example.streambot;

import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import com.hexadevlabs.gpt4all.LLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LocalMistralService {
    private static final Logger logger = LoggerFactory.getLogger(LocalMistralService.class);
    private Predictor<String, String> predictor;
    private ZooModel<String, String> model;
    private LLModel ggufModel;

    public LocalMistralService() {
        String modelPathProp = EnvUtils.get("MISTRAL_MODEL_PATH");
        List<Path> candidates = new ArrayList<>();

        if (modelPathProp != null && !modelPathProp.isBlank()) {
            candidates.add(Paths.get(modelPathProp));
        }

        String home = System.getProperty("user.home");
        String defaultWin = home + "\\AppData\\Local\\nomic.ai\\GPT4All\\Meta-Llama-3-8B-Instruct.Q4_0.gguf";
        if (System.getProperty("os.name", "").startsWith("Windows") &&
                Files.exists(Paths.get(defaultWin))) {
            candidates.add(Paths.get(defaultWin));
        }

        candidates.add(Paths.get("model"));

        try {
            Translator<String, String> translator = new Translator<>() {
                @Override
                public NDList processInput(TranslatorContext ctx, String input) {
                    int[] tokens = input.chars().toArray();
                    return new NDList(ctx.getNDManager().create(tokens));
                }

                @Override
                public String processOutput(TranslatorContext ctx, NDList list) {
                    if (list.isEmpty()) return "";
                    int[] tokens = list.get(0).toIntArray();
                    StringBuilder sb = new StringBuilder(tokens.length);
                    for (int t : tokens) sb.append((char) t);
                    return sb.toString();
                }
            };

            for (Path p : candidates) {
                if (loadModel(p, translator)) {
                    return;
                }
            }
        } catch (Exception e) {
            logger.error("Error loading local model", e);
        }
    }

    // Package-private constructor for injecting a predictor in tests
    LocalMistralService(Predictor<String, String> predictor) {
        this.predictor = predictor;
    }

    public String ask(String prompt) {
        if (ggufModel != null) {
            return ggufModel.generate(prompt, LLModel.config().build());
        }
        if (predictor == null) {
            return "";
        }
        try {
            return predictor.predict(prompt);
        } catch (TranslateException e) {
            logger.error("Inference error", e);
            return "";
        }
    }

    public void close() {
        if (ggufModel != null) {
            try {
                ggufModel.close();
            } catch (Exception e) {
                logger.error("Error closing gguf model", e);
            }
            ggufModel = null;
        }
        if (predictor != null) {
            predictor.close();
            predictor = null;
        }
        if (model != null) {
            model.close();
            model = null;
        }
    }

    private boolean loadModel(Path path, Translator<String, String> translator) {
        try {
            if (path.toString().endsWith(".gguf") || path.toString().endsWith(".bin")) {
                ggufModel = new LLModel(path);
                return true;
            }
            Criteria<String, String> criteria = Criteria.builder()
                    .setTypes(String.class, String.class)
                    .optModelPath(path)
                    .optEngine("PyTorch")
                    .optTranslator(translator)
                    .build();
            model = ModelZoo.loadModel(criteria);
            predictor = model.newPredictor();
            return true;
        } catch (Exception e) {
            logger.warn("Failed to load model {}", path, e);
            close();
            return false;
        }
    }
}


}
