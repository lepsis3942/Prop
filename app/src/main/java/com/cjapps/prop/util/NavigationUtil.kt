package com.cjapps.prop.util

/**
 * Replace all template variables in a string (ex- `{var_name}`) with the value
 * from the provided map
 */
fun String.withNavParameters(parameters: Map<String, String>): String {
    if (parameters.isEmpty()) return this

    var route = this
    parameters.forEach { entry ->
        val replaceableEntry = "{${entry.key}}"
        route = route.replace(replaceableEntry, entry.value)
    }

    return route
}