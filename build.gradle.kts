plugins {
    java
}

group = "com.minesunny"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.antlr:antlr4:4.13.2")
    implementation("org.slf4j:slf4j-simple:2.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
