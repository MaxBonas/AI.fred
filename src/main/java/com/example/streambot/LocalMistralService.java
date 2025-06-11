package com.example.streambot;

import ai.djl.Model;
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

import java.nio.file.Paths;

/**
 * Minimal service that loads a Mistral model from disk and performs inference
 * locally using DJL.
 */
public class LocalMistralService {
    private static final Logger logger = LoggerFactory.getLogger(LocalMistralService.class);
    private Predictor<String, String> predictor;
    private ZooModel<String, String> model;
    private LLModel ggufModel;

    public LocalMistralService() {
        String modelPath = EnvUtils.get("MISTRAL_MODEL_PATH", "model");
        try {
            if (modelPath.endsWith(".gguf") || modelPath.endsWith(".bin")) {
                ggufModel = new LLModel(Paths.get(modelPath));
                return;
            }
            Translator<String, String> translator = new Translator<>() {
                @Override
                public NDList processInput(TranslatorContext ctx, String input) {
                    // Basic tokenization: convert characters to their code points
                    int[] tokens = input.chars().toArray();
                    return new NDList(ctx.getNDManager().create(tokens));
                }

                @Override
                public String processOutput(TranslatorContext ctx, NDList list) {
                    if (list.isEmpty()) {
                        return "";
                    }
                    int[] tokens = list.get(0).toIntArray();
                    StringBuilder sb = new StringBuilder(tokens.length);
                    for (int t : tokens) {
                        sb.append((char) t);
                    }
                    return sb.toString();
                }
            };

            Criteria<String, String> criteria = Criteria.builder()
                    .setTypes(String.class, String.class)
                    .optModelPath(Paths.get(modelPath))
                    .optEngine("PyTorch")
                    .optTranslator(translator)
                    .build();
            model = ModelZoo.loadModel(criteria);
            predictor = model.newPredictor();
        } catch (Exception e) {
            logger.error("Error loading local model", e);
        }
    }

    /**
     * Executes inference with the loaded model.
     */
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

    /**
     * Release loaded resources.
     */
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
}
