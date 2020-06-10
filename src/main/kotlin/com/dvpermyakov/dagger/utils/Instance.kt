package com.dvpermyakov.dagger.utils

data class Instance(
    val name: String,
    val dependencies: MutableList<Instance>
) {
    override fun toString(): String {
        return name
    }
}

fun Instance.printDependencies() {
    val innerDependencies = mutableListOf<Instance>()
    dependencies.forEach { dependency ->
        println(dependency)
        innerDependencies.addAll(dependency.dependencies)
    }
    innerDependencies.forEach { dependency ->
        dependency.printDependencies()
    }
}

fun String.parseInstance(): Instance {
    return Instance(
        name = this,
        dependencies = mutableListOf()
    )
}