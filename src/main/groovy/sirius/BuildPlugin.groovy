package sirius

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import sirius.actions.CopyGroovyMarker
import sirius.actions.CopyJavaMarker
import sirius.actions.SiriusWrapper

class BuildPlugin implements Plugin<Project> {
    String COPY_JAVA_MARKER = "copyJavaMarker"
    String COPY_GROOVY_MARKER = "copyGroovyMarker"
    String GRADLE_WRAPPER = "wrapper"

    @Override
    void apply(Project project) {
        project.getPlugins().apply(JavaPlugin.class)
        project.getPlugins().apply(GroovyPlugin.class)
        project.getPlugins().apply(ApplicationPlugin.class)

        project.mainClassName = "IPL"
        project.applicationDefaultJvmArgs = ["-Ddebug=true"]

        // add repositories
        project.repositories {
            mavenLocal()
            mavenCentral()
            maven {
                url "https://oss.sonatype.org/content/repositories/snapshots"
            }
            maven {
                url "https://oss.sonatype.org/content/repositories/releases"
            }
        }

        project.dependencies {
            testCompile 'junit:junit:4.12'
            testCompile 'com.googlecode.junit-toolbox:junit-toolbox:2.2'
            testCompile 'org.spockframework:spock-core:1.1-groovy-2.4'
            testCompile 'cglib:cglib:3.2.5'
            testCompile 'org.objenesis:objenesis:2.5.1'
        }


        // define test suites:
        project.test {
            includes ['*TestSuite.class']
        }

        project.sourceSets {
            test {
                groovy {
                    srcDirs = ['src/test/groovy', 'src/test/java']
                    exclude '**/*.java'
                }
            }
        }

        addCopyMarker(project, COPY_JAVA_MARKER, CopyJavaMarker.class)
        addCopyMarker(project, COPY_GROOVY_MARKER, CopyGroovyMarker.class)

        project.getTasks().create(GRADLE_WRAPPER, SiriusWrapper.class)
    }

    void addCopyMarker (Project project, String name, Class clazz){
        project.getTasks().create(name, clazz)

        project.processResources.finalizedBy project.getTasks().getByName(name)
        project.processTestResources.finalizedBy project.getTasks().getByName(name)
    }
}