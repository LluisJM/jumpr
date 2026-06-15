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
	implementation(libs.kore.oop)
	implementation(libs.kore.helpers)
	implementation(libs.kotlinx.io)

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
}

// Apply a specific Java toolchain to ease working on different environments.
kotlin {
	jvmToolchain(25)

	// Activate required compiler options for using Kore.
	compilerOptions {
		freeCompilerArgs.add("-Xcontext-parameters")
	}
}

application {
	// Define the main class for the application.
	mainClass = "MainKt"
}
