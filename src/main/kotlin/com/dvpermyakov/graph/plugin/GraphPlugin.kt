package com.dvpermyakov.graph.plugin

import com.dvpermyakov.graph.task.KDaggerComponentTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class GraphPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val project = target

        project.tasks.register("component", KDaggerComponentTask::class.java) {
            group = "dagger-kotlin"
            description = "Parse component"
            dependsOn("compileKotlin")
            kotlinCompile = project.tasks.getByName("compileKotlin") as KotlinCompile
        }
    }
}