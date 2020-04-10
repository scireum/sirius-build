@file:Suppress("unused")

package sirius.parent

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.GroovySourceSet
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.testing.Test
import java.net.URI


class SyncIdeaSettingsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply {
            apply(JavaPlugin::class.java)
            apply(GroovyPlugin::class.java)
        }

        project.repositories.apply {
            add(project.repositories.mavenLocal())
            add(project.repositories.jcenter())
            add(project.repositories.maven {
                it.url = URI("https://mvn.scireum.com")
            })
            add(project.repositories.mavenCentral())
        }

        project.dependencies.apply {
            add("testImplementation", "junit:junit:4.12")
            add("testImplementation", "com.googlecode.junit-toolbox:junit-toolbox:2.2")
            add("testImplementation", "org.spockframework:spock-core:1.1-groovy-2.4")
            add("testImplementation", "cglib:cglib:3.2.5")
            add("testImplementation", "org.objenesis:objenesis:2.5.1")
        }

        val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java)

        javaConvention.sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME) {
            DslObject(it).convention.getPlugin(GroovySourceSet::class.java).run {
                groovy.srcDir("src/test/groovy")
                groovy.srcDir("src/test/java")
            }
            it.java.srcDir("src/test/java")
        }

        project.tasks.apply {
            val testTask = getByPath("test") as Test
            testTask.include("*TestSuite.class")
            testTask.jvmArgs = listOf("-Ddebug=true")

            register("syncIdeaSettings", SyncIdeaSettingsTask::class.java)

            getByName("build").finalizedBy(project.tasks.getByName("syncIdeaSettings"))

            addCopyMarker("copyJavaMarker", CopyJavaMarkerTask::class.java)
            addCopyMarker("copyGroovyMarker", CopyGroovyMarkerTask::class.java)
            addCopyMarker("copyKotlinMarker", CopyKotlinMarkerTask::class.java)
        }
    }

    private fun TaskContainer.addCopyMarker(name: String, clazz: Class<out CopyMarkerTask>) {
        create(name, clazz)

        getByName("processResources").finalizedBy(getByName(name))
        getByName("processTestResources").finalizedBy(getByName(name))
    }
}
