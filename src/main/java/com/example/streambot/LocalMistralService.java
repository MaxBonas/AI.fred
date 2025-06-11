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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
        String modelPath = EnvUtils.get("MISTRAL_MODEL_PATH");
        if (modelPath == null || modelPath.isBlank()) {
            String home = System.getProperty("user.home");
            String defaultWin = home + "\\AppData\\Local\\nomic.ai\\GPT4All\\Meta-Llama-3-8B-Instruct.Q4_0.gguf";
            if (System.getProperty("os.name", "").startsWith("Windows") &&
                    java.nio.file.Files.exists(java.nio.file.Paths.get(defaultWin))) {
                modelPath = defaultWin;
            } else {
                modelPath = "model";
            }
        }
        try {
            // The following translator performs a very naive tokenization
            // that simply converts each character to its Unicode code point.
            // It is intended only as a minimal placeholder for demos and is
            // not suitable for real production models. Integrate a tokenizer
            // compatible with your LLM (e.g. SentencePiece) for full support.
            Translator<String, String> translator = new Translator<>() {
                @Override
                public NDList processInput(TranslatorContext ctx, String input) {
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
            Path base = Paths.get(modelPath);
            List<Path> candidates = collectCandidates(base);
            List<String> tried = new ArrayList<>();

            for (Path p : candidates) {
                if (loadModel(p, translator)) {
                    return;
                }
                tried.add(p.toString());
            }


            // Search the parent directory as a fallback when the provided path was a file
            if (Files.isRegularFile(base)) {
                Path parent = base.getParent();
                if (parent != null) {
                    for (Path p : collectCandidates(parent)) {
                        if (!p.equals(base) && loadModel(p, translator)) {
                            logger.info("Loaded alternative model {}", p);
                            return;
                        }
                        tried.add(p.toString());
                    }
                }
            }

            logger.error("Failed to load any model from {}. Tried: {}", modelPath, String.join(", ", tried));
        } catch (Exception e) {
            logger.error("Error loading local model", e);
        }
    }

    // Package-private constructor for injecting a predictor, used mainly in tests
    LocalMistralService(Predictor<String, String> predictor) {
        this.predictor = predictor;
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

    private boolean loadModel(Path path, Translator<String, String> translator) {
        try {
            if (path.toString().endsWith(".gguf") || path.toString().endsWith(".bin")) {
                try {
                    ggufModel = new LLModel(path);
                    return true;
                } catch (IllegalStateException e) {
                    logger.warn("Unsupported GGUF model format");
                    return false;
                }
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

    private List<Path> collectCandidates(Path base) throws IOException {
        List<Path> files = new ArrayList<>();
        if (Files.isDirectory(base)) {
            try (var stream = Files.walk(base)) {
                stream.filter(Files::isRegularFile).forEach(files::add);
            }
        } else {
            files.add(base);
        }
        return files;
    }
}
