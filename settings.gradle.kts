apply(from = File(settingsDir, "gradle/repositoriesSettings.gradle.kts"))

rootProject.name = "gpt-worker"

include(
    "gpt-command-builder",
    "gpt-command-processor",
)
