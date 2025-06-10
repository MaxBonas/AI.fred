# StreamBot

Aplicación Java basada en Maven que utiliza un modelo de lenguaje para generar respuestas y mostrarlas en OBS. El modelo se carga desde disco y la inferencia se realiza localmente.

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

Puedes indicar la ruta del modelo desde la línea de comandos:

```bash
java -jar target/streambot-1.0-SNAPSHOT-shaded.jar \
  --model-path /ruta/al/modelo
```


## Configuración de ruta del modelo
Si no existe `.env`, la primera vez que ejecutes la aplicación se abrirá automáticamente `SetupWizard`, un asistente interactivo que crea dicho archivo pidiendo solo `MISTRAL_MODEL_PATH`. También puedes utilizar `env.example` como plantilla o crear el archivo manualmente con el siguiente contenido:

```
MISTRAL_MODEL_PATH=/ruta/al/modelo
```


## Uso con modelos locales

Para obtener un modelo compatible puedes descargarlo previamente y apuntar `MISTRAL_MODEL_PATH` a la carpeta donde se encuentre.

## Licencia
Este proyecto se distribuye bajo los términos de la [licencia MIT](LICENSE).

