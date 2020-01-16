plugins {
	kotlin("multiplatform") version "1.3.61"
	id("nebula.release") version "14.0.2"
	id("ru.capjack.bintray") version "1.0.0"
	id("ru.capjack.logging") version "1.0.1"
}

group = "ru.capjack.tool"

repositories {
	jcenter()
	maven("https://dl.bintray.com/capjack/public")
	mavenLocal()
}

kotlin {
	jvm {
		compilations.all { kotlinOptions.jvmTarget = "1.8" }
	}
	js {
		compilations.all { kotlinOptions.sourceMap = false }
		browser()
	}
	
	sourceSets {
		get("commonMain").dependencies {
			implementation(kotlin("stdlib-common"))
			implementation("ru.capjack.tool:tool-lang:1.1.0")
			implementation("ru.capjack.tool:tool-logging:1.0.1")
		}
		get("commonTest").dependencies {
			implementation(kotlin("test-common"))
			implementation(kotlin("test-annotations-common"))
		}
		
		get("jvmMain").dependencies {
			implementation(kotlin("stdlib-jdk8"))
		}
		get("jvmTest").dependencies {
			implementation(kotlin("test-junit"))
		}
		
		get("jsMain").dependencies {
			implementation(kotlin("stdlib-js"))
		}
		get("jsTest").dependencies {
			implementation(kotlin("test-js"))
		}
	}
}
