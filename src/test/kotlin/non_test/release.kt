package non_test

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.github.RepositoryConfig
import com.github.christophpickl.kpotpourri.github.buildGithub4k
import com.github.christophpickl.kpotpourri.github.detectGithubPass
import com.github.christophpickl.kpotpourri.logback4k.Logback4k
import com.github.christophpickl.kpotpourri.release4k.release4k
import java.io.File

fun main(args: Array<String>) {
    configureLogging()

    release4k {
        println("Urclubs Release - START")
        println()

        execute("bash", "bin/release.sh", File("."))

        // -Dgithub.pass=...
        val githubApi = buildGithub4k(RepositoryConfig(
            repositoryOwner = "christophpickl",
            repositoryName = "urclubs",
            username = "christoph.pickl@gmail.com",
            password = detectGithubPass()
        ))

//        github4k.uploadReleaseAsset(AssetUpload(
//            releaseId = 444,
//            bytes = file.readBytes(),
//            fileName = file.name,
//            contentType = "application/x-apple-diskimage"
//        ))

        println()
        println("Urclubs Release - END")
    }

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
