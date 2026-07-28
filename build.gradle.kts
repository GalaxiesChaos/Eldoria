plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.onarandombox.com/content/groups/public/")
}


dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")

    //MultiVerse
    compileOnly("org.mvplugins.multiverse.core:multiverse-core:5.7.2")

    //Minecraft API
    paperweight.paperDevBundle("26.2.build.+")

    //Luckyperms
    compileOnly("net.luckperms:api:5.5")

    //Plotsquared implementation
    implementation(platform("com.intellectualsites.bom:bom-newest:1.56"))
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core")


}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("26.2")
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
