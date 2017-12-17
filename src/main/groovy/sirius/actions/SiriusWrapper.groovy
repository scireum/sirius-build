package sirius.actions

import org.gradle.api.tasks.wrapper.Wrapper

class SiriusWrapper extends Wrapper {
    SiriusWrapper() {
        super()
        setGradleVersion "4.4"
    }
}