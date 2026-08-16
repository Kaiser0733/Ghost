plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23" apply false
}

repositories {
    google()
    mavenCentral()
}

rootProject.name = "Ghost"

include("ble-feasibility-lab")