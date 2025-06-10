# StreamBot

Aplicaci\u00f3n Java basada en Maven que utiliza un modelo de lenguaje y Twitch4J para interactuar con el p\u00fablico de un stream. Incluye ejemplos b\u00e1sicos para conectar con Twitch y generar preguntas usando la API de OpenAI.

## Importar en IntelliJ
1. Desde IntelliJ seleccione **File \u2192 Open** y elija la carpeta del proyecto.
2. IntelliJ detectar\u00e1 el `pom.xml` y configur\u00e1 autom\u00e1ticamente las dependencias.

## Compilar y ejecutar
```bash
mvn package
java -cp target/streambot-1.0-SNAPSHOT.jar com.example.streambot.StreamBotApplication
```

## Configuraci\u00f3n de credenciales
Cree un archivo `.env` en la ra\u00edz con las siguientes variables:

```
OPENAI_API_KEY=su_clave_de_openai
TWITCH_OAUTH_TOKEN=oauth:sutoken
TWITCH_CHANNEL=nombre_del_canal
```

La primera prueba es ejecutar la aplicaci\u00f3n y en el chat de Twitch escribir `!topic` para recibir una pregunta generada por el modelo.
