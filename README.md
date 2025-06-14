# StreamBot

[English version](README.en.md)

Aplicación Java basada en Maven que utiliza la API de OpenAI para generar respuestas y mostrarlas en la consola. Opcionalmente puede reproducir la respuesta con voz sintética utilizando el servicio de Text-to-Speech de OpenAI.

## Requisitos previos

- Java 17 (JDK 17)
- Maven (opcional, se puede usar el wrapper `./mvnw` incluido)
- La función de "pulsar para hablar" utiliza la biblioteca `jnativehook`,
  que se descarga automáticamente con Maven.

Instale primero JDK 17 y luego proceda con la instalaci\u00f3n de Maven.

## Instalaci\u00f3n de JDK 17

Si a\u00fan no tiene Java instalado, puede hacerlo de la siguiente manera:

### Ubuntu/Debian

```bash
sudo apt-get update
sudo apt-get install openjdk-17-jdk
```

### macOS con Homebrew

```bash
brew install openjdk@17
```

### Windows

Descargue el instalador desde [la p\u00e1gina de OpenJDK](https://adoptium.net) y siga los pasos.

Para verificar la instalaci\u00f3n ejecute:

```bash
java -version
```

## Instalaci\u00f3n de Maven

Este proyecto incluye un *Maven Wrapper* (`./mvnw`) que descarga
automáticamente Maven al ejecutarse. Puedes omitir esta sección si
prefieres usar el wrapper. Si deseas instalar Maven manualmente sigue
alguno de estos métodos:


### Ubuntu/Debian

```bash
sudo apt-get update
sudo apt-get install maven
```

### macOS con Homebrew

```bash
brew install maven
```

### Windows

Descargue el binario desde [la p\u00e1gina oficial de Maven](https://maven.apache.org/download.cgi), descompr\u00edmalo y agregue la ruta `bin` a la variable de entorno `PATH`.

Para verificar la instalaci\u00f3n ejecute:

```bash
mvn -v
```

## Importar en IntelliJ
1. Desde IntelliJ seleccione **File \u2192 Open** y elija la carpeta del proyecto.
2. IntelliJ detectará el `pom.xml` y configurará automáticamente las dependencias.

## Compilar y ejecutar
Al compilar con Maven se generar\u00e1 el archivo `target/streambot-1.0-SNAPSHOT-shaded.jar`.
```bash
./mvnw package
java -jar target/streambot-1.0-SNAPSHOT-shaded.jar
```
Antes de ejecutar asegúrate de que las variables `OPENAI_API_KEY` y `OPENAI_MODEL` estén disponibles en tu entorno o definidas en `.env`. También puedes pasarlas al iniciar la aplicación con los argumentos `--api-key` y `--model`. De forma predeterminada se usa `gpt-3.5-turbo` como modelo. Además es posible habilitar la síntesis de voz con `--tts-enabled true`, seleccionar la voz mediante `--tts-voice` y elegir la tecla de activación con `--push-key`. Usa `--help` para listar todas las opciones.

## Ejecutar pruebas
Para correr las pruebas unitarias con Maven utilice:

```bash
./mvnw test
```

### Uso básico

Ejecuta el bot y muestra las respuestas en la consola:

```bash
java -jar target/streambot-1.0-SNAPSHOT-shaded.jar
```

Mantén pulsada la tecla **F12** para hablar. El audio se grabará
mientras permanezca presionada y se enviará al soltarla. Puedes cambiar
la tecla con la opción `--push-key` o la variable `PUSH_KEY`.


## Configuración de la API de OpenAI
Regístrate en [OpenAI](https://platform.openai.com/) y crea una nueva *API key*. Copia esa clave en el archivo `.env` como valor de `OPENAI_API_KEY`. Puedes indicar el modelo con `OPENAI_MODEL`. Los modelos soportados son `gpt-3.5-turbo`, `gpt-3.5-turbo-16k`, `gpt-4` y `gpt-4-32k` (estos valores se mostrarán cuando `SetupWizard` pregunte por `OPENAI_MODEL`). Si `.env` no existe, al ejecutar la aplicación se iniciará `SetupWizard`, un asistente interactivo que solicitará los valores y generará el archivo automáticamente. La clave ingresada quedará también disponible como propiedad del sistema para usarla de inmediato. Otra opción es pasar estos valores con los argumentos `--api-key` y `--model`. También puedes habilitar la síntesis con `--tts-enabled` y elegir la voz con `--tts-voice` y definir la tecla con `--push-key`. Usa `--setup` si deseas volver a ejecutar el asistente y sobrescribir la configuración actual. Puedes usar `env.example` como plantilla.
Para mostrar las preguntas del asistente en inglés, ejecuta `--lang en` o define la variable `SETUP_LANG=en`.

Las principales variables de configuración son:

- `OPENAI_API_KEY`: clave de autenticación para la API de OpenAI.
- `OPENAI_MODEL`: modelo a utilizar. Los valores admitidos son `gpt-3.5-turbo`, `gpt-3.5-turbo-16k`, `gpt-4` y `gpt-4-32k`.
- `OPENAI_TEMPERATURE`: grado de aleatoriedad (0–2).
- `OPENAI_TOP_P`: umbral de muestreo *top p*.
- `OPENAI_MAX_TOKENS`: límite de tokens por respuesta.
- `OPENAI_LANGUAGE`: idioma de las respuestas (por defecto `es`).
- `CONVERSATION_STYLE`: tono para las sugerencias de conversación.
- `PREFERRED_TOPICS`: lista de temas preferidos separados por comas.
- `SILENCE_TIMEOUT`: segundos de espera antes de proponer un nuevo tema.
- `TTS_ENABLED`: si se establece en `true` reproduce las respuestas con voz sintética.
- `TTS_VOICE`: voz a utilizar para la síntesis (`alloy`, `echo`, `fable`, `onyx`, `nova` o `shimmer`).
- `USE_MICROPHONE`: establece `true` para tener en cuenta el micrófono y reiniciar el temporizador de silencio cuando se detecte sonido.
- `MICROPHONE_NAME`: nombre del dispositivo de micrófono a utilizar (opcional).
- `PUSH_KEY`: tecla para pulsar y hablar (por defecto `F12`).

Usa `env.example` como guía para crear tu propio `.env`:

```
# Configuración de ejemplo para StreamBot
OPENAI_API_KEY=
OPENAI_MODEL=
# Temperatura entre 0 y 2
OPENAI_TEMPERATURE=
# top_p para muestreo nucleus
OPENAI_TOP_P=
# Máximo de tokens por respuesta
OPENAI_MAX_TOKENS=
# Idioma para las respuestas (es, en, etc.). Valor por defecto "es"
OPENAI_LANGUAGE=
# Tono deseado para las sugerencias
CONVERSATION_STYLE=
# Temas separados por comas
PREFERRED_TOPICS=
# Segundos de silencio antes de sugerir un tema
SILENCE_TIMEOUT=
# Habilitar reproducción de texto a voz
TTS_ENABLED=
# Nombre de la voz para TTS (alloy, echo, fable, onyx, nova o shimmer)
TTS_VOICE=
# Monitorear actividad del micrófono para reiniciar el temporizador
USE_MICROPHONE=
# Nombre del dispositivo de micrófono (opcional)
MICROPHONE_NAME=
```

## Licencia
Este proyecto se distribuye bajo los términos de la [licencia MIT](LICENSE).

