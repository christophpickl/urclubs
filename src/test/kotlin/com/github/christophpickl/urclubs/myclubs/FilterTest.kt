package com.github.christophpickl.urclubs.myclubs

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.testng.annotations.Test
import java.time.LocalDateTime

@Test
class FilterTest {

    private val date = LocalDateTime.parse("2004-05-06T09:10:11")

    fun `CourseFilter toFilterMycJson - sunshine`() {
        val filter = CourseFilter(
                start = LocalDateTime.parse("2000-12-31T09:00:00"),
                end = LocalDateTime.parse("2000-12-31T14:30:00")
        )

        val filterJson = filter.toFilterMycJson()

        assertThat(filterJson.date).containsExactly("31.12.2000")
        assertThat(filterJson.time).containsExactly("09:00", "14:30")
    }

    fun `CourseFilter - When start is after end Then throw`() {
        assertThatThrownBy {
            CourseFilter(
                    start = date,
                    end = date.minusHours(1)
            )
        }
    }

    fun `CourseFilter - When start and end different day Then throw`() {
        assertThatThrownBy {
            CourseFilter(
                    start = date,
                    end = date.plusDays(1)
            )
        }
    }

}
