import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.javaandroidapp"
    compileSdk = 34
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
    defaultConfig {
        applicationId = "com.example.javaandroidapp"
        minSdk = 26
        targetSdk = 34
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

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    dependencies {
        implementation("com.google.firebase:firebase-firestore:24.10.3")
        implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
        implementation("com.google.firebase:firebase-auth")
        implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
        implementation("com.algolia:algoliasearch-android:3.+")
        implementation("com.stripe:stripe-java:24.0.0")
        implementation("com.google.zxing:core:3.5.3")
        implementation("com.stripe:stripe-android:20.40.2")
        implementation ("com.android.volley:volley:1.2.1")
        implementation("io.getstream:stream-chat-java:1.21.0")
        implementation("io.getstream:stream-chat-android-ui-components:6.2.3")
        implementation("com.journeyapps:zxing-android-embedded:4.3.0")
//        implementation("com.algolia:algoliasearch-apache:3.16.5")
        implementation("com.github.bumptech.glide:glide:4.16.0")
        implementation("com.google.firebase:firebase-storage:20.3.0")
        val fragment_version = "1.6.2"
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.11.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.fragment:fragment:$fragment_version")
        implementation("com.google.firebase:firebase-messaging:23.4.1")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    }
    ndkVersion = "16"
}
dependencies {
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}
