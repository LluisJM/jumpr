plugins {
	// Apply the kotlin.jvm plugin to add support for Kotlin.
	alias(libs.plugins.kotlin)

	// Apply the application plugin to add support for running the application.
	application

	kotlin("plugin.serialization") version "2.4.0"
}

repositories {
	// Use Maven Central for resolving dependencies.
	mavenCentral()
	maven("https://central.sonatype.com/repository/maven-snapshots/")
}

dependencies {
	// Install Kore.
	implementation(libs.kore)
	implementation(libs.kotlinx.io)

	implementation("io.github.ayfri.kore:oop:2.0.3-1.21.11")
	implementation("io.github.ayfri.kore:helpers:2.0.3-1.21.11")

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
}

// Apply a specific Java toolchain to ease working on different environments.
kotlin {
	jvmToolchain(21)

	// Activate required compiler options for using Kore.
	compilerOptions {
		freeCompilerArgs.add("-Xcontext-parameters")
	}
}

application {
	// Define the main class for the application.
	mainClass = "MainKt"
}
