# StreamBot

Aplicaci\u00f3n Java basada en Maven que utiliza un modelo de lenguaje y Twitch4J para interactuar con el p\u00fablico de un stream. Incluye ejemplos b\u00e1sicos para conectar con Twitch y generar preguntas usando la API de OpenAI.

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
```bash
mvn package
java -cp target/streambot-1.0-SNAPSHOT.jar com.example.streambot.StreamBotApplication
```

### Ejecutar solo para OBS

Cuando el proyecto incluya soporte para este modo se podr\u00e1 iniciar el bot sin conectarse a Twitch utilizando:

```bash
java -cp target/streambot-1.0-SNAPSHOT.jar com.example.streambot.StreamBotApplication --obs-only
```

Este comando generar\u00e1 las respuestas del chat para mostrarlas exclusivamente en OBS.

## Configuraci\u00f3n de credenciales
Cree un archivo `.env` en la ra\u00edz con las siguientes variables:

```
OPENAI_API_KEY=su_clave_de_openai
TWITCH_OAUTH_TOKEN=oauth:sutoken
TWITCH_CHANNEL=nombre_del_canal
```

La primera prueba es ejecutar la aplicaci\u00f3n y en el chat de Twitch escribir `!topic` para recibir una pregunta generada por el modelo.
