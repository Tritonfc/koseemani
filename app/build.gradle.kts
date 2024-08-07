plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.koseemani"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.koseemani"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {

        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.4.3"
//    }
    packaging {
        resources {
            excludes += "**/*"
        }
    }
    configurations.all{
        resolutionStrategy{
            force("com.google.api-client:google-api-client:1.30.5")
        }

    }
}

dependencies {

    val room_version = "2.6.1"

    implementation ("androidx.navigation:navigation-compose:2.8.0-alpha08")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-viewbinding")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.accompanist:accompanist-permissions:0.35.1-alpha")

    implementation ("com.google.android.gms:play-services-location:21.3.0")

    implementation ("com.google.android.gms:play-services-auth:21.2.0")

    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    implementation("androidx.camera:camera-view:1.3.4")
    implementation ("com.google.guava:guava:28.0-android")
// Guava fix
    implementation ("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    //Google drive
    implementation ("com.google.apis:google-api-services-drive:v3-rev20190926-1.30.3"){
        exclude(group = "org.apache.httpcomponents",module = "guava-jdk5")
    }

    //Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    //Room
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")



    // To use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$$room_version")

    implementation ("com.google.api-client:google-api-client:1.30.5"){
        exclude(group = "org.apache.httpcomponents",module = "guava-jdk5")
    }

    implementation ("com.google.api-client:google-api-client-android:1.30.5"){
        exclude(group = "org.apache.httpcomponents",module = "guava-jdk5")
    }
    implementation ("com.google.oauth-client:google-oauth-client-jetty:1.36.0")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.23.0")

    //Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}