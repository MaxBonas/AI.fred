# StreamBot

Aplicación Java basada en Maven que utiliza la API de OpenAI para generar respuestas y mostrarlas en la consola.

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
Antes de ejecutar asegúrate de que la variable `OPENAI_API_KEY` esté disponible en tu entorno o definida en `.env`. También puedes pasarla al iniciar la aplicación con el argumento `--api-key`.

## Ejecutar pruebas
Para correr las pruebas unitarias con Maven utilice:

```bash
mvn test
```

### Uso básico

Ejecuta el bot y muestra las respuestas en la consola:

```bash
java -jar target/streambot-1.0-SNAPSHOT-shaded.jar
```


## Configuración de la API de OpenAI
Regístrate en [OpenAI](https://platform.openai.com/) y crea una nueva *API key*. Copia esa clave en el archivo `.env` como valor de `OPENAI_API_KEY`. Si `.env` no existe, al ejecutar la aplicación se iniciará `SetupWizard` para solicitarla automáticamente. La clave ingresada quedará también disponible como propiedad del sistema para usarla de inmediato. Otra opción es pasar la clave cada vez con el argumento `--api-key`. También puedes usar `env.example` como plantilla. El archivo debe contener lo siguiente:

```
# Example configuration for StreamBot
OPENAI_API_KEY=
```

## Licencia
Este proyecto se distribuye bajo los términos de la [licencia MIT](LICENSE).

