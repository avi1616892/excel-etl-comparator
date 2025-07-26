plugins {
    id("java")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // ספריית POI - אקסל
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    // ספריית H2 - מסד נתונים בזיכרון
    implementation("com.h2database:h2:2.2.224")

    // JUnit 5
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

// קידוד UTF-8 כדי למנוע בעיות עם עברית
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
