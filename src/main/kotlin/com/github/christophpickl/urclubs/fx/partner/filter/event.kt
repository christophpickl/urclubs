package com.github.christophpickl.urclubs.fx.partner.filter

import tornadofx.*

data class ApplyFilterFXEvent(val filter: Filter) : FXEvent() {
    companion object {
        fun noFilter() = ApplyFilterFXEvent(Filter.NoFilter)
    }
}
