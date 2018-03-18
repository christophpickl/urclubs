package com.github.christophpickl.urclubs

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.google.inject.Provider
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.stringType

data class MetaInf(
    val version: String,
    val builtDate: String
)

object MetaInfProvider : Provider<MetaInf> {

    private val log = LOG {}

    @Suppress("ClassName")
    private object metainf : PropertyGroup() {
        val application_version by stringType
        val built_date by stringType
    }

    private var metaInf: MetaInf? = null
    override fun get(): MetaInf {
        if (metaInf != null) {
            return metaInf!!
        }
        val config = ConfigurationProperties.fromResource("urclubs/metainf.properties")
        metaInf = MetaInf(
            version = config[metainf.application_version],
            builtDate = config[metainf.built_date]
        )
        log.info { "Successfully load metainf: $metaInf" }
        return metaInf!!
    }

}
