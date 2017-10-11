package sirius.actions

import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskAction

class CopyMarker extends Copy {
    CopyMarker(String output) {
        super()

        from("$project.buildDir/resources/") {
            include "**/*.marker"
        }

        into "$project.buildDir/classes/$output"
    }

    @TaskAction
    def doCopy() {
        super.copy()
    }
}