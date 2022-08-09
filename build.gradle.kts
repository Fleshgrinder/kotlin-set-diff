@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("multiplatform") version embeddedKotlinVersion
}

//java {
//    withJavadocJar()
//    withSourcesJar()
//    consistentResolution {
//        useCompileClasspathVersions()
//    }
//}

kotlin {
    explicitApi()
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    sourceSets {
        named("jvmTest") {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.9.0")
                implementation("io.strikt:strikt-jvm:0.31.0")
            }
        }
    }
}

tasks {
    register<NotImplemented>("publishSnapshot")
    register<NotImplemented>("publishRelease")
}

abstract class NotImplemented : DefaultTask() {
    @TaskAction fun execute() {
        if (System.getenv("GITHUB_ACTIONS") == "true") {
            val file = buildString {
                append(".github/workflows/gradle-")
                name.forEach { c ->
                    if (c in 'A'..'Z') append('-').append(c.toLowerCase())
                    else append(c)
                }
                append(".yaml")
            }
            println("::error file=$file,title=Task $path::Not implemented!")
        } else {
            println("\u001B[31mNot implemented!\u001B[0m")
        }
    }
}
