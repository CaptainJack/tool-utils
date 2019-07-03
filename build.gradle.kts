import org.jetbrains.kotlin.cli.common.arguments.K2JsArgumentConstants

plugins {
	kotlin("multiplatform") version "1.3.40"
	id("nebula.release") version "10.1.2"
	id("ru.capjack.bintray") version "0.18.1"
}

group = "ru.capjack.tool"

repositories {
	jcenter()
	maven("https://dl.bintray.com/capjack/public")
}

kotlin {
	sourceSets {
		commonMain.get().dependencies {
			implementation(kotlin("stdlib-common"))
//			implementation("ru.capjack.tool:tool-lang:0.4.2")
			implementation("ru.capjack.tool:tool-logging:0.14.0")
		}
	}
	
	jvm().compilations {
		all {
			kotlinOptions.jvmTarget = "1.8"
		}
		get("main").defaultSourceSet.dependencies {
			implementation(kotlin("stdlib-jdk8"))
		}
	}
	
	js().browser()
	js().compilations {
		get("main").defaultSourceSet.dependencies {
			implementation(kotlin("stdlib-js"))
		}
	}
}