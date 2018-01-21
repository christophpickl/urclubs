package com.github.christophpickl.urclubs.testInfra

import com.fasterxml.jackson.databind.node.ArrayNode

fun ArrayNode.textValues() = elements().asSequence().map { it.textValue() }.toList()
