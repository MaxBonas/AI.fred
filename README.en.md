# StreamBot

[Versión en español](README.md)

StreamBot is a Java application built with Maven that uses the OpenAI API to generate responses and print them in the console. Optionally it can play the reply with synthesized speech using OpenAI's Text-to-Speech service.

## Prerequisites

- Java 17 (JDK 17)
- Maven (optional, you can use the included `./mvnw` wrapper)

Install JDK 17 first and then proceed with Maven if you prefer not to use the wrapper.

## Installing JDK 17

If you do not have Java installed, you can do so as follows:

### Ubuntu/Debian
```bash
sudo apt-get update
sudo apt-get install openjdk-17-jdk
```

### macOS with Homebrew
```bash
brew install openjdk@17
```

### Windows
Download the installer from [the OpenJDK website](https://adoptium.net) and follow the steps.

Verify the installation with:
```bash
java -version
```

## Installing Maven

This project ships with a *Maven Wrapper* (`./mvnw`) that downloads Maven automatically when invoked. You can skip this section if you want to use the wrapper. If you prefer a manual installation, follow one of these methods:

### Ubuntu/Debian
```bash
sudo apt-get update
sudo apt-get install maven
```

### macOS with Homebrew
```bash
brew install maven
```

### Windows
Download the binary from [the official Maven site](https://maven.apache.org/download.cgi), extract it and add the `bin` directory to your `PATH`.

Verify the installation with:
```bash
mvn -v
```

## Compile and run
When compiling with Maven the file `target/streambot-1.0-SNAPSHOT-shaded.jar` will be produced.
```bash
./mvnw package
java -jar target/streambot-1.0-SNAPSHOT-shaded.jar
```
Before running make sure the variables `OPENAI_API_KEY` and `OPENAI_MODEL` are present in your environment or defined in `.env`. They can also be provided as arguments `--api-key` and `--model`. By default the model `gpt-3.5-turbo` is used. You can enable speech synthesis with `--tts-enabled true` and choose the voice using `--tts-voice`.

## Configuration
Sign up at [OpenAI](https://platform.openai.com/) and create a new *API key*. Copy it to the `.env` file as the value for `OPENAI_API_KEY`. You can indicate the model with `OPENAI_MODEL`. Supported models are `gpt-3.5-turbo`, `gpt-3.5-turbo-16k`, `gpt-4` and `gpt-4-32k`. If `.env` does not exist, running the application will launch `SetupWizard`, an interactive assistant that requests the values and generates the file automatically. You can also provide these values using `--api-key` and `--model`. Speech can be enabled with `--tts-enabled` and the voice chosen with `--tts-voice`. Use `--setup` if you want to run the wizard again and overwrite the current configuration. The file `env.example` can be used as a template.

The main configuration variables are:

- `OPENAI_API_KEY`: OpenAI API authentication key.
- `OPENAI_MODEL`: model to use (`gpt-3.5-turbo`, `gpt-3.5-turbo-16k`, `gpt-4` or `gpt-4-32k`).
- `OPENAI_TEMPERATURE`: randomness degree (0–2).
- `OPENAI_TOP_P`: top p sampling threshold.
- `OPENAI_MAX_TOKENS`: maximum tokens per reply.
- `CONVERSATION_STYLE`: tone for conversation hints.
- `PREFERRED_TOPICS`: comma separated list of preferred topics.
- `SILENCE_TIMEOUT`: seconds of silence before suggesting a new topic.
- `TTS_ENABLED`: set to `true` to play replies with synthesized voice.
- `TTS_VOICE`: voice to use for synthesis (`alloy`, `echo`, `fable`, `onyx`, `nova` or `shimmer`).

Use `env.example` as a guide to create your own `.env`:
```text
# Example configuration for StreamBot
OPENAI_API_KEY=
OPENAI_MODEL=
OPENAI_TEMPERATURE=
OPENAI_TOP_P=
OPENAI_MAX_TOKENS=
CONVERSATION_STYLE=
PREFERRED_TOPICS=
SILENCE_TIMEOUT=
TTS_ENABLED=
TTS_VOICE=
```

## License
This project is distributed under the terms of the [MIT license](LICENSE).

Este archivo también está disponible en [español](README.md).
