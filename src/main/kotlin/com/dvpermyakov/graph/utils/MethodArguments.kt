package com.dvpermyakov.graph.utils

data class MethodArguments(
    val arguments: List<String>,
    val returnType: String
)

fun String.parseMethodArguments(): MethodArguments {
    val items = split("(", ")", ";").filter { argument ->
        argument.isNotBlank()
    }

    return MethodArguments(
        arguments = items.subList(0, items.lastIndex),
        returnType = items.last()
    )
}