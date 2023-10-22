package com.cjapps.prop.util

import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationUtilTest {

    @Test
    fun emptyParameterListHasNoEffect() {
        val originalString = "test/{userId}"
        val route = originalString.withNavParameters(mapOf())

        assertEquals(route, originalString)
    }

    @Test
    fun singleParameterIsReplaced() {
        val originalString = "test/{userId}"
        val route = originalString.withNavParameters(mapOf("userId" to "5"))

        assertEquals("test/5", route)
    }

    @Test
    fun multipleSameParametersReplaced() {
        val originalString = "test/{userId}?testId={userId}"
        val route = originalString.withNavParameters(mapOf("userId" to "5"))

        assertEquals("test/5?testId=5", route)
    }

    @Test
    fun multipleDistinctParametersReplaced() {
        val originalString = "test/{userId}/{nestedId}?testId={childId}"
        val route = originalString.withNavParameters(
            mapOf(
                "userId" to "5",
                "nestedId" to "90",
                "childId" to "1289"
            )
        )

        assertEquals("test/5/90?testId=1289", route)
    }

    @Test
    fun noMatchingParametersIgnored() {
        val originalString = "test/{userId}/{nestedId}?testId={childId}"
        val route = originalString.withNavParameters(
            mapOf(
                "userId" to "5",
                "nestedId" to "90",
                "erroneousId" to "failure",
                "childId" to "1289"
            )
        )

        assertEquals("test/5/90?testId=1289", route)
    }
}