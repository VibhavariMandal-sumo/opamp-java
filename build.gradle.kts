import java.time.Duration

plugins {
  id("otel.java-conventions")
  id("java-library")
  id("maven-publish")
  id("io.github.gradle-nexus.publish-plugin")
}

apply(from = "version.gradle.kts")

nexusPublishing {
  packageGroup.set("io.opentelemetry")

  repositories {
    sonatype {
      username.set(System.getenv("SONATYPE_USER"))
      password.set(System.getenv("SONATYPE_KEY"))
    }
  }

  connectTimeout.set(Duration.ofMinutes(5))
  clientTimeout.set(Duration.ofMinutes(5))

  transitionCheckOptions {
    // We have many artifacts so Maven Central takes a long time on its compliance checks. This sets
    // the timeout for waiting for the repository to close to a comfortable 50 minutes.
    maxRetries.set(300)
    delayBetween.set(Duration.ofSeconds(10))
  }
}


repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  implementation(project(":proto"))
}

group = "io.opentelemetry"
version = "1.0.0"

publishing {
  publications.create<MavenPublication>("lib") {
    from(components["java"])
  }
  repositories.maven("/tmp/opamp-java")
}

tasks.jar {
  from(sourceSets.main.get().output)
  dependsOn(configurations.runtimeClasspath)
  from({
    configurations.runtimeClasspath.get().filter { it.name.equals("proto.jar") }.map { zipTree(it) }
  })
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
