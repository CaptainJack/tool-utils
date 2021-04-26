plugins {
	kotlin("multiplatform") version "1.4.32"
	`maven-publish`
	id("nebula.release") version "15.3.1"
	id("ru.capjack.reposit") version "0.3.0"
}

group = "ru.capjack.tool"

repositories {
	mavenCentral()
	mavenCapjack()
}

publishing {
	repositories {
		mavenCapjackPublic(reposit)
	}
}

kotlin {
	jvm {
		compilations.all { kotlinOptions.jvmTarget = "11" }
	}
	js(IR) {
		browser()
	}
	
	sourceSets {
		get("commonMain").dependencies {
			implementation("ru.capjack.tool:tool-lang:1.9.0")
			implementation("ru.capjack.tool:tool-logging:1.4.0")
		}
		get("commonTest").dependencies {
			implementation(kotlin("test-common"))
			implementation(kotlin("test-annotations-common"))
		}
		
		get("jvmMain").dependencies {
			implementation(kotlin("reflect"))
		}
		get("jvmTest").dependencies {
			implementation(kotlin("test-junit"))
		}
		
		get("jsTest").dependencies {
			implementation(kotlin("test-js"))
		}
	}
}
