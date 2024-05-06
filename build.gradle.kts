plugins {
    java
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("org.liquibase.gradle") version "2.2.0"
    id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
}

group = "am.dopomoga"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2022.0.4"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude("spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("org.postgresql:postgresql")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    liquibaseRuntime("org.liquibase:liquibase-core:4.27.0")
    liquibaseRuntime("info.picocli:picocli:4.7.5")
    liquibaseRuntime("org.postgresql:postgresql")
}


liquibase {
    activities.register("main") {
        val db_url by project.extra.properties
        val db_user by project.extra.properties
        val db_pass by project.extra.properties
        this.arguments = mapOf(
                "logLevel" to "info",
                "changelogFile" to "liquibase/changelog.xml",
                "url" to db_url,
                "username" to db_user,
                "password" to db_pass
        )
    }
    runList = "main"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.register<Copy>("getUIArtifactsFromSubproject") {
    from("${rootProject.projectDir}/aid-tools-ui/dist/aid-tools-ui")
    into("${rootProject.projectDir}/src/main/resources/static")
}

tasks.named("getUIArtifactsFromSubproject") {
    dependsOn(":aid-tools-ui:assembleFrontend")
}

tasks.named("build") {
    dependsOn("liquibaseUpdate")
}


tasks.processResources {
    dependsOn("getUIArtifactsFromSubproject")
}

tasks.withType<Test> {
    useJUnitPlatform()
}