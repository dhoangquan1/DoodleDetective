plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.kapt)
	id("com.google.devtools.ksp") version "1.9.24-1.0.20"
}

android {
	namespace = "edu.wpi.cs.cs4518.doodledetective"
	compileSdk = 35

	defaultConfig {
		applicationId = "edu.wpi.cs.cs4518.doodledetective"
		minSdk = 24
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
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}

	buildFeatures {
		viewBinding = true
	}

	packagingOptions {
		exclude("META-INF/DEPENDENCIES")
		exclude("META-INF/LICENSE.md")
		exclude("META-INF/LICENSE-notice.md")
	}
}

dependencies {

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.constraintlayout)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)

	implementation("com.github.psambit9791:jdsp:3.1.0"){
		exclude(group = "org.apache.maven.surefire", module = "common-java5")
	}

	// Lifecycle components (ViewModel + LiveData)
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

	// Coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

	// OkHttp
	implementation("com.squareup.okhttp3:okhttp:4.12.0")

	// GSON
	implementation ("com.google.code.gson:gson:2.8.8")

	implementation ("com.github.bumptech.glide:glide:4.16.0")
	kapt ("com.github.bumptech.glide:compiler:4.16.0")

	// Room
	implementation ("androidx.room:room-runtime:2.4.3")
	implementation ("androidx.room:room-ktx:2.4.3")
	ksp ("androidx.room:room-compiler:2.4.3")

	implementation ("androidx.cardview:cardview:1.0.0")
}