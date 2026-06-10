plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.aonews"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.aonews"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.register("deleteDuplicateResources") {
    doLast {
        val mipmapDirs = listOf("mipmap-hdpi", "mipmap-mdpi", "mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi")
        mipmapDirs.forEach { dirName ->
            val dir = file("src/main/res/$dirName")
            if (dir.exists()) {
                val filesToDelete = listOf("ic_launcher.xml", "ic_launcher_round.xml")
                filesToDelete.forEach { fileName ->
                    val file = file("src/main/res/$dirName/$fileName")
                    if (file.exists()) {
                        println("Deleting duplicate resource: ${file.absolutePath}")
                        file.delete()
                    }
                }
            }
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.swiperefreshlayout)
    implementation(libs.androidx.browser)
    
    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    
    // Glide
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // GSON
    implementation(libs.gson)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
