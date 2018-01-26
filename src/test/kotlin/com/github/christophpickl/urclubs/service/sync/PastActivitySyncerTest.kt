package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test

@Test
class PastActivitySyncerTest {
    fun `foo`() {
        val myclubs = mock<MyClubsApi>()

        val report = PastActivitySyncer(myclubs).sync()

        assertThat(report).isEqualTo("foo")
    }
}
