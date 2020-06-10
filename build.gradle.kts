plugins {
	`java-gradle-plugin`
	`kotlin-dsl`
	id("com.gradle.plugin-publish") version "0.12.0"
}

repositories {
	jcenter()
}

dependencies {
	implementation(kotlin("stdlib-jdk8", "1.3.72"))
	implementation(kotlin("gradle-plugin", "1.3.72"))

	implementation("org.ow2.asm:asm:8.0.1")
	implementation("org.ow2.asm:asm-commons:8.0.1")
}

gradlePlugin {
	plugins {
		create("graph-plugin") {
			id = "com.dvpermyakov.graph.plugin"
			version = "0.0.1"
			displayName = "graph plugin"
			description = "You can see your graph"
			implementationClass = "com.dvpermyakov.graph.plugin.GraphPlugin"
		}
	}
}

pluginBundle {
	website = "https://github.com/dvpermyakov/graph-gradle-plugin"
	vcsUrl = "https://github.com/dvpermyakov/graph-gradle-plugin"
	tags = listOf("graph")
}