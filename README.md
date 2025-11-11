# README

# openCartE2E
Prueba funcional automatizada (Prueba E2E) del flujo de compra en la página  http://opencart.abstracta.us/ usando la herramienta SerenityBDD con Screenplay

Proyecto creado por: Mayra Romero


## Herramientas y Complementos

|                                                                               **IntelliJ**                                                                                |**Java**|**Gradle**|
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------:| :----: | :----:  |
| [<img width="50" height="50" src="https://cdn.iconscout.com/icon/free/png-128/intellij-idea-569199.png">](https://www.jetbrains.com/es-es/idea/download/#section=windows) | [<img height="60" src="https://www.oracle.com/a/ocom/img/cb71-java-logo.png">](https://www.oracle.com/java/technologies/downloads/) | [<img height="50" src="https://gradle.org/images/gradle-knowledge-graph-logo.png?20170228">](https://gradle.org/releases/) |

> **NOTA**:

> * Para ejecutar el proyecto se recomienda minimo las siguientes versiones:
    >   * IntelliJ Community Edition 2021
>   * Java JDK 17
>   * Gradle 8.8
> * Una vez obtenido IntelliJ es necesario instalar los plugins de Gherkin y Cucumber for Java. (
    *[Guia de instalación plugins en intellij](https://www.jetbrains.com/help/idea/managing-plugins.html)*)

## Ejecución local

0. Clonar el proyecto

```bash
  git clone 
```

Para correr el proyecto de manera local se debe realizar los siguientes pasos:

1. Definir la tag de los tipos de tests que se van a ejecutar, esto lo hacemos en el archivo WebRunnerTest, para el
   ejemplo se va a correr todos los test.

```
tags = "@compraOpenCart"
```

2. Definir el driver a usarse en serenity.properties.

```
#CONFIGURACION DRIVER
webdriver.driver=${BrowserWeb}
```

3. Definir variable de entorno para el navegador a usarse en serenity.properties.
```
browserWeb=Chrome
```

4. Para ejecutar el escenario se debe verificar o editar la data de prueba que se encuentra en la ruta 
   "src/test/resources/dataTest/dataTestOpenCart.xlsx", en el archivo se registran la data identificandolo por la columna idCaso para elegir el caso de prueba se desea ejecutar, en la columna productos se debe incluir todos los productos que se desea agregar a la compra separado por "," por ejemplo: "iPhone,MacBook Air".


## Comandos

El arquetipo posee 2 runners activos, uno para pruebas con SErenityBDD y otro para pruebas con Karate Framework

### Comandos SerenityBDD

Para ejecutar todos los escenarios por linea de comandos

```bash
  .\gradlew clean test --tests "openCart.runners.RunnerTest"
```

Para ejecutar escenarios que contengan un tag especifico

```bash
  .\gradlew clean test --tests -Dcucumber.filter.tags="@compraOpenCart" openCart.runners.RunnerTest
```

Para ejecutar los escenarios enviando variables de ambiente

```bash
  ./gradlew clean test --tests "openCart.runners.RunnerTest" -DbrowserWeb=Chrome
```


> **NOTA**:
> * Para las pruebas de SerenityBDD, el reporte serenity se genera en la ruta **target/site/index.html**, los reportes
    cucumber se generan en la carpeta **build/reports/tests/test/index.html**.


## Construido con:

La automatización fue desarrollada con:

* BDD - Estrategia de desarrollo
* Screenplay
* Gradle - Manejador de dependencias
* Cucumber - Framework para automatizar pruebas BDD
* Serenity BDD - Biblioteca de código abierto para la generación de reportes
* Gherkin - Lenguaje Business Readable DSL (Lenguaje especifico de dominio legible por el negocio)