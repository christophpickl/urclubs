package non_test

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.common.process.ProcessExecuterImpl
import com.github.christophpickl.kpotpourri.github.AssetUpload
import com.github.christophpickl.kpotpourri.github.CreateReleaseRequest
import com.github.christophpickl.kpotpourri.github.GithubApi
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

    val githubApi = connectGithub()

    ProcessExecuterImpl().execute("bash", "bin/release.sh", File("."))

    val version = readVersion()

    uploadGithub(githubApi, version)

    println()
    println("Urclubs Release - END")
}

private fun connectGithub(): GithubApi {
    // -Dgithub.pass=...
    println("Connecting to GitHub ...")
    return buildGithub4k(RepositoryConfig(
        repositoryOwner = "christophpickl",
        repositoryName = "urclubs",
        username = "christoph.pickl@gmail.com",
        password = detectGithubPass()
    ))
}

private fun readVersion(): String {
    val props = Properties()
    props.load(FileReader(File("version.properties")))
    return props.getProperty("version")
}

private fun uploadGithub(githubApi: GithubApi, version: String) {
    val dmg = File("build/distributions/UrClubs-$version.dmg")
    if (!dmg.exists()) {
        throw Exception("Expected release artifact does not exist at: ${dmg.canonicalPath}")
    }

    println("Creating new release ...")
    val release = githubApi.createNewRelease(CreateReleaseRequest(
        tag_name = version,
        name = "Release $version",
        body = "Simple auto release of UrClubs version $version"
    ))

    println("Uploading disk image located at ${dmg.canonicalPath} ...")
    githubApi.uploadReleaseAsset(AssetUpload(
        releaseId = release.id,
        bytes = dmg.readBytes(),
        fileName = dmg.name,
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
