# Documento de Actualización Técnica: Migración a Java 21 y Configuración CI/CD con Jenkins

**Asignatura:** Gestión de Proyectos de Desarrollo de Software (GPDS) - Unidad 2  
**Repositorio GitHub:** [https://github.com/Enrique1189/admin-proyectos](https://github.com/Enrique1189/admin-proyectos)  
**Fecha de Actualización:** 21 de Octubre de 2026  

---

## 1. Resumen Ejecutivo del Upgrade

El presente documento detalla la actualización técnica integral realizada sobre los dos microservicios que componen el sistema **Admin Proyectos**:
1. **`Ejercicio5MyAPIGestor`** (Microservicio Backend REST / Capa de Persistencia).
2. **`Ejercicio5Cliente`** (Microservicio Frontend Web / Thymeleaf + Bootstrap).

### Objetivos Alcanzados:
- **Migración a Java 21 (LTS):** Actualización de la versión del compilador Java desde versiones anteriores (Java 11 y Java 17) hacia **Java 21 LTS**, aprovechando mejoras de rendimiento, Garbage Collector ZGC/G1 optimizado, y compatibilidad a largo plazo.
- **Actualización de Framework Spring Boot:** Subida a la versión **Spring Boot 3.3.4**, habilitando soporte nativo para Java 21 y el ecosistema Jakarta EE 10.
- **Migración de Namespace Java EE a Jakarta EE:** Cambio en entidades JPA de `javax.persistence.*` a `jakarta.persistence.*`.
- **Integración de Plugins para Calidad y Pruebas Unitarias:**
  - `sonar-maven-plugin` versión `5.5.0.6356` para análisis estático en SonarQube / SonarCloud.
  - `jacoco-maven-plugin` versión `0.8.12` para generación de métricas y cobertura de código.
  - `maven-surefire-plugin` versión `3.2.5` para la orquestación y ejecución de pruebas unitarias en el ciclo de vida de Maven.
- **Preparación de Entorno de Pruebas Autónomo (CI/CD):** Configuración de base de datos en memoria H2 para perfiles de prueba en `Ejercicio5MyAPIGestor`, garantizando que las pruebas unitarias y de integración del pipeline de Jenkins se ejecuten limpiamente sin requerir un servidor de base de datos MariaDB externo activo durante la construcción.
- **Configuración de Jenkins:** Compatibilidad garantizada mediante JDK 21 y Maven 3.9.x en la gestión de herramientas globales.

---

## 2. Comparativa de Archivos `pom.xml`

### 2.1 Microservicio 1: `Ejercicio5MyAPIGestor`

#### A. Archivo `pom.xml` en Estado Normal (Original)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.7.17</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.mx.cbtis</groupId>
	<artifactId>Ejercicio5MyAPIGestor</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Ejercicio5MyAPIGestor</name>
	<description>Proyecto My API Rest</description>
	<properties>
		<java.version>11</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.mariadb.jdbc</groupId>
			<artifactId>mariadb-java-client</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

#### B. Archivo `pom.xml` Actualizado (Java 21 + Plugins Sonar, JaCoCo, Surefire)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.3.4</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.mx.cbtis</groupId>
	<artifactId>Ejercicio5MyAPIGestor</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Ejercicio5MyAPIGestor</name>
	<description>Proyecto My API Rest</description>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.mariadb.jdbc</groupId>
			<artifactId>mariadb-java-client</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<jvmArguments>-Xms64m -Xmx256m</jvmArguments>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.sonarsource.scanner.maven</groupId>
				<artifactId>sonar-maven-plugin</artifactId>
				<version>5.5.0.6356</version>
			</plugin>
			<plugin>
				<groupId>org.jacoco</groupId>
				<artifactId>jacoco-maven-plugin</artifactId>
				<version>0.8.12</version>
				<executions>
					<execution>
						<goals>
							<goal>prepare-agent</goal>
						</goals>
					</execution>
					<execution>
						<id>report</id>
						<phase>test</phase>
						<goals>
							<goal>report</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>3.2.5</version>
			</plugin>
		</plugins>
	</build>

</project>
```

---

### 2.2 Microservicio 2: `Ejercicio5Cliente`

#### A. Archivo `pom.xml` en Estado Normal (Original)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.2.5</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.mx.cbtis</groupId>
	<artifactId>Ejercicio5Cliente</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Ejercicio5Cliente</name>
	<description>Interfaz del Gestor de Proyectos</description>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		
		<dependency>
		    <groupId>org.webjars</groupId>
		    <artifactId>bootstrap</artifactId>
		    <version>5.3.3</version> 
		</dependency>

	</dependencies>
	
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
	
</project>
```

#### B. Archivo `pom.xml` Actualizado (Java 21 + Plugins Sonar, JaCoCo, Surefire)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.3.4</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.mx.cbtis</groupId>
	<artifactId>Ejercicio5Cliente</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Ejercicio5Cliente</name>
	<description>Interfaz del Gestor de Proyectos</description>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		
		<dependency>
		    <groupId>org.webjars</groupId>
		    <artifactId>bootstrap</artifactId>
		    <version>5.3.3</version> 
		</dependency>

	</dependencies>
	
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<jvmArguments>-Xms64m -Xmx256m</jvmArguments>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.sonarsource.scanner.maven</groupId>
				<artifactId>sonar-maven-plugin</artifactId>
				<version>5.5.0.6356</version>
			</plugin>
			<plugin>
				<groupId>org.jacoco</groupId>
				<artifactId>jacoco-maven-plugin</artifactId>
				<version>0.8.12</version>
				<executions>
					<execution>
						<goals>
							<goal>prepare-agent</goal>
						</goals>
					</execution>
					<execution>
						<id>report</id>
						<phase>test</phase>
						<goals>
							<goal>report</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>3.2.5</version>
			</plugin>
		</plugins>
	</build>
	
</project>
```

---

## 3. Explicación Detallada de los Cambios Realizados

| Componente | Estado Anterior | Estado Actualizado | Justificación Técnica |
|---|---|---|---|
| **Versión Java (Gestor)** | `11` | `21` | Adopción de la versión Java 21 LTS para compatibilidad con Spring Boot 3.x, Virtual Threads y mejores algoritmos de optimización. |
| **Versión Java (Cliente)** | `17` | `21` | Estandarización de ambos microservicios sobre la misma versión base Java 21. |
| **Spring Boot Parent** | `2.7.17` (Gestor)<br>`3.2.5` (Cliente) | `3.3.4` (Ambos) | Spring Boot 3.3.4 ofrece soporte oficial y completo para JDK 21, mejoras de seguridad y compatibilidad de plugins Maven modernos. |
| **Namespace JPA** | `javax.persistence.*` | `jakarta.persistence.*` | Spring Boot 3 implementa Jakarta EE 10 / Hibernate 6, requiriendo el cambio de importaciones en las clases de modelo (`Usuario`, `Proyectos`, `Tareas`, `Comentarios`, `Asignaciones`). |
| **Estrategia ID** | `GenerationType.AUTO` | `GenerationType.IDENTITY` | Optimización del mapeo de llaves primarias autoincrementables en MariaDB/MySQL. |
| **sonar-maven-plugin** | No existía | `5.5.0.6356` | Requerimiento específico para análisis de código y métricas de calidad en SonarQube. |
| **jacoco-maven-plugin** | No existía | `0.8.12` | Plugin oficial de cobertura de código. La versión `0.8.12` tiene soporte pleno para el bytecode generado por Java 21. |
| **maven-surefire-plugin**| Gestionado implícitamente | `3.2.5` | Asegura la correcta ejecución de los tests unitarios con JUnit 5 Jupiter en compilaciones Maven bajo JDK 21. |
| **Base H2 de Prueba (Gestor)**| Dependencia directa de MariaDB | `h2` en scope `test` | Permite que las pruebas unitarias y de contexto se ejecuten en pipelines de CI/Jenkins sin requerir un servidor MariaDB encendido. |

---

## 4. Configuración Requerida en Jenkins

Para que Jenkins compile y ejecute los microservicios con Java 21 exitosamente, se deben configurar las herramientas globales en el servidor Jenkins:

### 4.1 Configuración de JDK 21 en Jenkins
1. Ir a: **Administrar Jenkins** (`Manage Jenkins`) $\rightarrow$ **Configuración de herramientas globales** (`Tools` o `Global Tool Configuration`).
2. En la sección **JDK**:
   - Clic en **Añadir JDK** (`Add JDK`).
   - **Nombre:** `JDK21` (o el nombre referenciado en el pipeline).
   - **Instalar automáticamente:** Opcional (se puede seleccionar *Adoptium OpenJDK 21* o especificar el `JAVA_HOME` local, por ejemplo: `C:\Program Files\Java\jdk-21.0.11`).

### 4.2 Configuración de Maven compatible con Java 21
> **Importante:** Java 21 requiere Maven versión 3.8.8 o superior (se recomienda **Maven 3.9.6 / 3.9.9**). Versiones antiguas como Maven 3.6.3 fallan al procesar el bytecode y los argumentos de compilación de Java 21.

1. En la misma pantalla de **Tools**, en la sección **Maven**:
   - Clic en **Añadir Maven** (`Add Maven`).
   - **Nombre:** `Maven3.9`
   - **Instalar automáticamente:** Seleccionar versión `3.9.9` (o indicar `MAVEN_HOME`, por ejemplo: `C:\Users\karen\OneDrive\Documentos\apache-maven-3.9.9`).

### 4.3 Configuración en el Job o Pipeline de Jenkins
Si se utiliza un **Pipeline declarativo**, se ha incluido un archivo `Jenkinsfile` en la raíz del repositorio con la siguiente estructura:

```groovy
pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3.9'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Compilación y Pruebas - Ejercicio5MyAPIGestor') {
            steps {
                dir('Ejercicio5MyAPIGestor') {
                    bat 'mvn clean test'
                }
            }
        }

        stage('Compilación y Pruebas - Ejercicio5Cliente') {
            steps {
                dir('Ejercicio5Cliente') {
                    bat 'mvn clean test'
                }
            }
        }

        stage('Análisis SonarQube') {
            steps {
                dir('Ejercicio5MyAPIGestor') {
                    echo 'Listo para análisis con sonar-maven-plugin:5.5.0.6356'
                    // bat 'mvn sonar:sonar'
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
        }
    }
}
```

---

## 5. Ligas de GitHub para Entrega

Los cambios han sido preparados para el repositorio en GitHub:

- **Repositorio Principal:**  
  [https://github.com/Enrique1189/admin-proyectos](https://github.com/Enrique1189/admin-proyectos)
- **Liga al archivo POM de `Ejercicio5MyAPIGestor` (Actualizado en rama main):**  
  [https://github.com/Enrique1189/admin-proyectos/blob/main/Ejercicio5MyAPIGestor/pom.xml](https://github.com/Enrique1189/admin-proyectos/blob/main/Ejercicio5MyAPIGestor/pom.xml)
- **Liga al archivo POM de `Ejercicio5Cliente` (Actualizado en rama main):**  
  [https://github.com/Enrique1189/admin-proyectos/blob/main/Ejercicio5Cliente/pom.xml](https://github.com/Enrique1189/admin-proyectos/blob/main/Ejercicio5Cliente/pom.xml)
- **Liga al `Jenkinsfile` (Listo para la práctica):**  
  [https://github.com/Enrique1189/admin-proyectos/blob/main/Jenkinsfile](https://github.com/Enrique1189/admin-proyectos/blob/main/Jenkinsfile)
