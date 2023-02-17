package util

import io.quarkus.test.junit.QuarkusTestProfile

class WiremockTestProfile : QuarkusTestProfile {

    override fun getConfigProfile(): String {
        return "wiremock-test"
    }

    override fun testResources(): MutableList<QuarkusTestProfile.TestResourceEntry> {
        return mutableListOf(QuarkusTestProfile.TestResourceEntry(WireMockExtensions::class.java))
    }
}