Gradle Plugin
- Provides a gradle plugin to build, test, and deploy any sirius lib project
- Just include the following in your build.gradle (at the very top)

buildscript {
    repositories {
        mavenLocal()
        maven {
            url "https://oss.sonatype.org/content/repositories/releases"
        }
    }

    dependencies {
        classpath group: 'com.scireum', name: 'sirius-parent', version: '4.0-SNAPSHOT'
    }
}
apply plugin: 'sirius-parent'