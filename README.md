# StreamBot

Aplicación Java basada en Maven que utiliza un modelo de lenguaje para generar respuestas y mostrarlas en OBS. El modelo se carga desde disco y la inferencia se realiza localmente. Puede trabajar con modelos PyTorch mediante **DJL** o con archivos `.gguf` gracias a **gpt4all-java-binding**.

## Requisitos previos

- Java 17 (JDK 17)
- Maven

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

Si Maven no est\u00e1 instalado en su sistema puede hacerlo siguiendo alguno de estos m\u00e9todos:

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
mvn package
java -jar target/streambot-1.0-SNAPSHOT-shaded.jar
```

## Ejecutar pruebas
Para correr las pruebas unitarias con Maven utilice:

```bash
mvn test
```

### Uso básico

Ejecuta el bot de manera local y muestra las respuestas en OBS:

```bash
java -jar target/streambot-1.0-SNAPSHOT-shaded.jar
```


## Configuración de la API de OpenAI
Si no existe `.env`, la primera vez que ejecutes la aplicación se abrirá automáticamente `SetupWizard`, un asistente interactivo que solicitará tu clave de OpenAI y escribirá `OPENAI_API_KEY` en el archivo. También puedes utilizar `env.example` como plantilla o crear el archivo manualmente con el siguiente contenido. La variable `OPENAI_API_KEY` almacena tu clave de API:

```
# Example configuration for StreamBot
OPENAI_API_KEY=
```

La aplicación admite modelos en formato PyTorch (`.pt`/`.zip`) y también
modelos `.gguf` gracias a la biblioteca **gpt4all-java-binding** incluida en el
proyecto.


> **Nota**: solo pueden cargarse modelos `.gguf` compatibles con
> [gpt4all-java-binding](https://github.com/nomic-ai/gpt4all/tree/main/gpt4all-bindings/java).
> Algunos archivos, como `Meta-Llama-3-8B-Instruct.Q4_0.gguf`, pueden no funcionar con el
> backend incluido. Si ocurre un error, consulte la [documentación oficial](https://docs.gpt4all.io/)
> para verificar los formatos soportados. Algunos modelos requieren bibliotecas
> nativas adicionales que se pueden obtener desde las
> [releases de gpt4all](https://github.com/nomic-ai/gpt4all/releases).



## Uso con modelos locales

Si quieres trabajar con modelos almacenados localmente, define manualmente la
variable `MISTRAL_MODEL_PATH` y apunta a la carpeta donde se encuentre el modelo.

### Ejemplo de modelos locales en Windows
Si ya tienes modelos descargados en `C:\Users\Max\AppData\Local\nomic.ai\GPT4All`,
puedes usar cualquiera de los siguientes archivos como valor de
`MISTRAL_MODEL_PATH`:

```
C:\Users\Max\AppData\Local\nomic.ai\GPT4All\mistral-7b-instruct-v0.1.Q4_0.gguf
C:\Users\Max\AppData\Local\nomic.ai\GPT4All\mistral-7b-openorca.gguf2.Q4_0.gguf
C:\Users\Max\AppData\Local\nomic.ai\GPT4All\nous-hermes-llama2-13b.Q4_0.gguf
C:\Users\Max\AppData\Local\nomic.ai\GPT4All\Phi-3.5-3.8B-vision-instruct-F16.gguf
C:\Users\Max\AppData\Local\nomic.ai\GPT4All\qwen2.5-coder-7b-instruct-q4_0.gguf
C:\Users\Max\AppData\Local\nomic.ai\GPT4All\gpt4all-falcon-newbpe-q4_0.gguf
C:\Users\Max\AppData\Local\nomic.ai\GPT4All\Llama-3.2-1B-Instruct-Q4_0.gguf
C:\Users\Max\AppData\Local\nomic.ai\GPT4All\Llama-3.2-3B-Instruct-Q4_0.gguf
C:\Users\Max\AppData\Local\nomic.ai\GPT4All\Llama-3.2-11B-Vision-Instruct-mmproj.f16.gguf
C:\Users\Max\AppData\Local\nomic.ai\GPT4All\Meta-Llama-3.1-8B-Instruct-128k-Q4_0.gguf
C:\Users\Max\AppData\Local\nomic.ai\GPT4All\Meta-Llama-3-8B-Instruct.Q4_0.gguf
```

## Limitaciones del traductor
La clase `LocalMistralService` utiliza un traductor muy b\u00e1sico que simplemente
convierte cada car\u00e1cter en su c\u00f3digo Unicode. Este enfoque solo sirve como
marcador de posici\u00f3n y no es adecuado para modelos de producci\u00f3n. Para una
compatibilidad completa con modelos de lenguaje, integra un *tokenizer* real
(por ejemplo SentencePiece o el que requiera tu modelo).

## Licencia
Este proyecto se distribuye bajo los términos de la [licencia MIT](LICENSE).

