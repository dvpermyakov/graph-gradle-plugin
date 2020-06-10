package com.dvpermyakov.dagger.visitors

import com.dvpermyakov.dagger.utils.Instance
import com.dvpermyakov.dagger.utils.parseInstance
import com.dvpermyakov.dagger.utils.parseMethodArguments
import com.dvpermyakov.dagger.utils.printDependencies
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.util.*

class InitMethodVisitor(
    methodDescriptor: String
) : MethodVisitor(Opcodes.ASM5) {

    private val localVariables = listOf("this") + methodDescriptor.parseMethodArguments().arguments
    private val stack = Stack<Instance>()
    private val fieldMap = mutableMapOf<String, Instance>()
    private val heap = mutableMapOf<String, Instance>()

    init {
        println("-------LocalVariables------")
        localVariables.forEach { variable ->
            println("variable = $variable")
        }
    }

    override fun visitFieldInsn(opcode: Int, owner: String, name: String, descriptor: String) {
        super.visitFieldInsn(opcode, owner, name, descriptor)
        println("-------visitFieldInsn--------")
        println("opcode = $opcode")
        println("owner = $owner")
        println("name = $name")
        println("descriptor = $descriptor")
        when (opcode) {
            Opcodes.PUTFIELD -> {
                println("PUTFIELD")
                val value = stack.pop()
                println("value = $value")
                val reference = stack.pop()
                println("reference = $reference")
                fieldMap[name] = value
            }
            Opcodes.GETFIELD -> {
                println("GETFIELD")
                val value = fieldMap[name]
                println("value = $value")
                val reference = stack.pop()
                println("reference = $reference")
                stack.push(value)
            }
        }
    }

    override fun visitInsn(opcode: Int) {
        super.visitInsn(opcode)
        println("-------visitInsn--------")
        println("opcode = $opcode")
        when (opcode) {
            Opcodes.DUP -> {
                println("DUP")
                val value = stack.pop()
                println("value = $value")
                stack.push(value)
                stack.push(value)
            }
        }
    }

    override fun visitVarInsn(opcode: Int, `var`: Int) {
        super.visitVarInsn(opcode, `var`)
        println("-------visitVarInsn--------")
        println("opcode = $opcode")
        println("var = $`var`")
        when (opcode) {
            Opcodes.ALOAD -> {
                println("ALOAD")
                val instance = localVariables[`var`].parseInstance()
                println("value = $instance")
                stack.push(instance)
            }
        }
    }

    override fun visitLdcInsn(value: Any?) {
        super.visitLdcInsn(value)
        println("-------visitLdcInsn--------")
        val instance = value.toString().parseInstance()
        println("value = $instance")
        stack.push(instance)
    }

    override fun visitTypeInsn(opcode: Int, type: String) {
        super.visitTypeInsn(opcode, type)
        println("-------visitTypeInsn--------")
        println("opcode = $opcode")
        println("type = $type")
        when (opcode) {
            Opcodes.NEW -> {
                println("NEW")
                val instance = type.parseInstance()
                stack.push(instance)
            }
            Opcodes.CHECKCAST -> {
                println("CHECKCAST")
            }
        }
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean
    ) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        println("------visitMethodInsn---------")
        println("opcode = $opcode")
        println("owner = $owner")
        println("name = $name")
        println("descriptor = $descriptor")
        when (opcode) {
            Opcodes.INVOKESTATIC -> {
                println("INVOKESTATIC")
                val args = descriptor.parseMethodArguments()
                invokeMethod(true, args.arguments, args.returnType)
            }
            Opcodes.INVOKESPECIAL -> {
                println("INVOKESPECIAL")
                val args = descriptor.parseMethodArguments()
                invokeMethod(false, args.arguments, args.returnType)
            }
            Opcodes.INVOKEINTERFACE -> {
                println("INVOKEINTERFACE")
                val args = descriptor.parseMethodArguments()
                invokeMethod(false, args.arguments, args.returnType)
            }
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        fieldMap.forEach { (name, instance) ->
            println("------$name-------")
            println(instance.name)
            instance.printDependencies()
        }
    }

    private fun invokeMethod(isStatic: Boolean, arguments: List<String>, returnType: String) {
        val values = mutableListOf<Instance>()
        arguments.reversed().forEach { argument ->
            val value = stack.pop()
            values.add(value)
            println("argument = $argument, value = $value")
        }
        if (!isStatic) {
            val reference = stack.pop()
            println("reference = $reference")
            reference.dependencies.addAll(values)
        }
        if (returnType != "V") {
            stack.push(returnType.parseInstance())
        }
    }
}