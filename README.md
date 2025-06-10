# StreamBot

Aplicación Java basada en Maven que utiliza un modelo de lenguaje para generar respuestas y mostrarlas en OBS. Utiliza un servidor compatible con Mistral para las consultas de IA.

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

### Uso básico

Ejecuta el bot de manera local y muestra las respuestas en OBS:

```bash
java -jar target/streambot-1.0-SNAPSHOT-shaded.jar
```

Puedes especificar las credenciales desde la línea de comandos:

```bash
java -jar target/streambot-1.0-SNAPSHOT-shaded.jar 
  --mistral-key TU_CLAVE --base-url http://localhost:11434/v1/ --model mistral-tiny
```


## Configuración de credenciales
Si no existe, al iniciar se mostrará un asistente interactivo para generarlo automáticamente. Este asistente solicita ahora `MISTRAL_BASE_URL` y `MISTRAL_MODEL`; puedes dejar estos campos vacíos para utilizar los valores por defecto.
También puedes crear el archivo `.env` manualmente en la raíz con las siguientes variables (puedes copiar `env.example` y completar los valores):

```
MISTRAL_API_KEY=tu_clave
MISTRAL_BASE_URL=
MISTRAL_MODEL=
```


## Uso con modelos locales

Para ejecutar el bot sin depender de la API de Mistral puedes levantar un servidor
compatible de manera local (por ejemplo mediante [Ollama](https://ollama.com) o
[LocalAI](https://localai.io)) y descargar el modelo Mixtral:

```bash
ollama pull mistralai/mixtral-8x22b-instruct-v0.1
```

Una vez iniciado el servicio local, configura la variable `MISTRAL_BASE_URL` para
apuntar al endpoint que expone dicho servidor, por ejemplo:

```bash
MISTRAL_BASE_URL=http://localhost:11434/v1/
```

De manera opcional puedes definir `MISTRAL_MODEL` si tu servidor usa un nombre de
modelo distinto. El resto de la aplicación funciona igual que con la API de Mistral.

## Licencia
Este proyecto se distribuye bajo los terminos de la [licencia MIT](LICENSE).

