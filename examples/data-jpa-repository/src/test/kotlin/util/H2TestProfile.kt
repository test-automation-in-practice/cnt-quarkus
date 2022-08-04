package util

import io.quarkus.test.junit.QuarkusTestProfile

class H2TestProfile : QuarkusTestProfile {

    override fun getConfigProfile(): String {
        return "h2-test"
    }
}