# StreamBot

Aplicaci\u00f3n Java basada en Maven que utiliza un modelo de lenguaje y Twitch4J para interactuar con el p\u00fablico de un stream. Incluye ejemplos b\u00e1sicos para conectar con Twitch y generar preguntas usando la API de OpenAI.

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
2. IntelliJ detectar\u00e1 el `pom.xml` y configur\u00e1 autom\u00e1ticamente las dependencias.

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

### Ejecutar solo para OBS

Cuando el proyecto incluya soporte para este modo se podr\u00e1 iniciar el bot sin conectarse a Twitch utilizando:

```bash
java -jar target/streambot-1.0-SNAPSHOT-shaded.jar --obs-only
```

Este comando generar\u00e1 las respuestas del chat para mostrarlas exclusivamente en OBS.

También es posible sobrescribir las credenciales al iniciar el programa:

```bash
java -jar target/streambot-1.0-SNAPSHOT-shaded.jar \
  --openai-key TU_CLAVE --twitch-token oauth:token --channel micanal
```


## Configuraci\u00f3n de credenciales
Si no existe, al iniciar se mostrará un asistente interactivo para generarlo automáticamente. Este asistente solicita ahora `OPENAI_BASE_URL` y `OPENAI_MODEL`; puedes dejar estos campos vacíos para utilizar los valores por defecto.
También puedes crear el archivo `.env` manualmente en la raíz con las siguientes variables (puedes copiar `env.example` y completar los valores):

```
OPENAI_API_KEY=su_clave_de_openai
TWITCH_OAUTH_TOKEN=oauth:sutoken
TWITCH_CHANNEL=nombre_del_canal
OPENAI_BASE_URL=
OPENAI_MODEL=
```

### Variable `USE_TWITCH`

Por defecto el bot intenta conectarse a Twitch (`USE_TWITCH=true`). Si se establece `USE_TWITCH=false` la aplicación funcionará únicamente de manera local mostrando las respuestas en consola u OBS.

En el archivo `.env` puede indicarse así:

```
USE_TWITCH=false
```

También es posible pasarlo desde la línea de comandos (sobrescribe al valor del `.env`):

```bash
java -DUSE_TWITCH=false -cp target/streambot-1.0-SNAPSHOT.jar \
  com.example.streambot.StreamBotApplication
```

La opción abreviada `--obs-only` tiene el mismo efecto:

```bash
java -cp target/streambot-1.0-SNAPSHOT.jar com.example.streambot.StreamBotApplication --obs-only
```
La primera prueba es ejecutar la aplicaci\u00f3n y en el chat de Twitch escribir `!topic` para recibir una pregunta generada por el modelo.

## Uso con modelos locales

Para ejecutar el bot sin depender de la API de OpenAI puedes levantar un servidor
compatible de manera local (por ejemplo mediante [Ollama](https://ollama.com) o
[LocalAI](https://localai.io)) y descargar el modelo Mixtral:

```bash
ollama pull mistralai/mixtral-8x22b-instruct-v0.1
```

Una vez iniciado el servicio local, configura la variable `OPENAI_BASE_URL` para
apuntar al endpoint que expone dicho servidor, por ejemplo:

```bash
OPENAI_BASE_URL=http://localhost:11434/v1/
```

De manera opcional puedes definir `OPENAI_MODEL` si tu servidor usa un nombre de
modelo distinto. El resto de la aplicaci\u00f3n funciona igual que con la API de
OpenAI.

## Licencia
Este proyecto se distribuye bajo los terminos de la [licencia MIT](LICENSE).

