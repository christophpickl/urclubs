package com.github.christophpickl.urclubs.myclubs

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.christophpickl.urclubs.testInfra.containsExactly
import com.github.christophpickl.urclubs.testInfra.testInstance
import com.github.christophpickl.urclubs.testInfra.textValues
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test

@Test
class JsonSerializingTest {

    private val jackson = jacksonObjectMapper()

    fun `ActivityType enum serializes with custom json property value`() {
        val filter = FilterMycJson.testInstance().copy(type = ActivityTypeMyc.all)

        val json = jackson.readTree(jackson.writeValueAsString(filter))

        val typeJson = json.get("type") as ArrayNode
        assertThat(typeJson.textValues()).containsExactly(ActivityTypeMyc.all.map { it.json })
    }

}
