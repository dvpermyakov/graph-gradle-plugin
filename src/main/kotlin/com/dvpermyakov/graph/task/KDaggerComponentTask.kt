package com.dvpermyakov.graph.task

import com.dvpermyakov.graph.visitors.InitMethodVisitor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.objectweb.asm.*
import java.io.File

abstract class KDaggerComponentTask : DefaultTask() {

    @get:Input
    lateinit var kotlinCompile: KotlinCompile

    @TaskAction
    fun action() {
        kotlinCompile.outputs.files.asFileTree.visit {
            if (file.name.startsWith("KDaggerAsm") && !file.name.contains("Companion") && !file.name.contains('$')) {
                parseFile(file)
            }
        }
    }

    private fun parseFile(file: File) {
        val classReader = ClassReader(file.readBytes())
        classReader.accept(object : ClassVisitor(Opcodes.ASM5) {
            override fun visitMethod(
                access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<out String>?
            ): MethodVisitor {
                return if (name == "<init>") {
                    InitMethodVisitor(descriptor)
                } else object : MethodVisitor(Opcodes.ASM5) {}
            }
        }, ClassReader.SKIP_FRAMES)
    }
}