package non_test

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.common.process.ProcessExecuterImpl
import com.github.christophpickl.kpotpourri.github.AssetUpload
import com.github.christophpickl.kpotpourri.github.CreateReleaseRequest
import com.github.christophpickl.kpotpourri.github.RepositoryConfig
import com.github.christophpickl.kpotpourri.github.buildGithub4k
import com.github.christophpickl.kpotpourri.github.detectGithubPass
import com.github.christophpickl.kpotpourri.logback4k.Logback4k
import java.io.File
import java.io.FileReader
import java.util.Properties

fun main(args: Array<String>) {
    configureLogging()

    println("Urclubs Release - START")
    println()

    ProcessExecuterImpl().execute("bash", "bin/release.sh", File("."))

    val version = readVersion()
    uploadGithub(version)

    println()
    println("Urclubs Release - END")

}

private fun readVersion(): String {
    val props = Properties()
    props.load(FileReader(File("version.properties")))
    return props.getProperty("version")
}

private fun uploadGithub(version: String) {
    // -Dgithub.pass=...
    println("Connecting to GitHub ...")
    val githubApi = buildGithub4k(RepositoryConfig(
        repositoryOwner = "christophpickl",
        repositoryName = "urclubs",
        username = "christoph.pickl@gmail.com",
        password = detectGithubPass()
    ))

    println("Creating new release ...")
    val release = githubApi.createNewRelease(CreateReleaseRequest(
        tag_name = version,
        name = "Release $version",
        body = "Simple auto release of UrClubs version $version"
    ))

    val file = File("build/distributions/UrClubs-$version.dmg")
    if (!file.exists()) {
        throw Exception("Expected release artifact does not exist at: ${file.canonicalPath}")
    }

    println("Uploading disk image located at ${file.canonicalPath} ...")
    githubApi.uploadReleaseAsset(AssetUpload(
        releaseId = release.id,
        bytes = file.readBytes(),
        fileName = file.name,
        contentType = "application/x-apple-diskimage"
    ))
}


private fun configureLogging() {
    Logback4k.reconfigure {
        rootLevel = Level.ALL
        packageLevel(Level.WARN,
            "org.apache",
            "com.github.christophpickl.kpotpourri"
        )
        addConsoleAppender()
    }
}
