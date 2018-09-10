package com.github.christophpickl.urclubs.persistence

import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationVersion
import org.hsqldb.jdbc.JDBCDataSource
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import urclubs.migrations.V4__partner_dates
import java.sql.Timestamp
import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource

@Test
class FlywayMigrationTest {

    private var testCounter = AtomicInteger()

    private lateinit var dataSource: DataSource
    private lateinit var flyway: Flyway

    @BeforeMethod
    fun `setup flyway`() {
        dataSource = newTestDataSource(javaClass.simpleName + testCounter.incrementAndGet())
        flyway = FlywayManager(dataSource).buildFlyway()
    }

    fun `migrate from 3 to 4 changes partner columns, deletion and inserted dates, not deleted partner`() {
        migrateDb("3", 3)

        insertV3Partner(deletedByMc = false)

        migrateDb("4", 1)

        val rs = dataSource.connection.prepareStatement("SELECT * FROM PartnerDbo").executeQuery()
        rs.next()
        assertThat(rs.getTimestamp("dateInserted")).isEqualTo(Timestamp.valueOf(V4__partner_dates.defaultDate))
        assertThat(rs.getTimestamp("dateDeleted")).isNull()

        val columnNames = 1.rangeTo(rs.metaData.columnCount).map { rs.metaData.getColumnName(it) }
        assertThat(columnNames).doesNotContain("deletedByMyc")
    }

    fun `migrate from 3 to 4 changes partner columns, deletion and inserted dates, deleted partner`() {
        migrateDb("3", 3)

        insertV3Partner(deletedByMc = true)

        migrateDb("4", 1)

        val rs = dataSource.connection.prepareStatement("SELECT * FROM PartnerDbo").executeQuery()
        rs.next()
        assertThat(rs.getTimestamp("dateInserted")).isEqualTo(Timestamp.valueOf(V4__partner_dates.defaultDate))
        assertThat(rs.getTimestamp("dateDeleted")).isEqualTo(Timestamp.valueOf(V4__partner_dates.defaultDate))
    }

    private fun insertV3Partner(deletedByMc: Boolean) {
        dataSource.connection.prepareStatement("""
            INSERT INTO PartnerDbo (
              idMyc, name, shortName, note, linkMyclubs,
              linkPartner, maxCredits, rating, category,
              deletedByMyc, favourited, wishlisted, ignored, picture
            ) VALUES (
              'idMy', 'name', 'shortName', 'note', 'linkMyclubs',
              'linkPartner', 2, 'OK', 1,
              $deletedByMc, false, false, false, NULL
            )
            """).execute()
    }

    private fun newTestDataSource(dbNameSuffix: String) = JDBCDataSource().apply {
        url = "jdbc:hsqldb:mem:testDb$dbNameSuffix"
        user = "SA"
    }

    private fun migrateDb(toVersion: String, expectedMigrations: Int? = null) {
        flyway.target = MigrationVersion.fromVersion(toVersion)

        val executedMigrations = flyway.migrate()
        if (expectedMigrations != null) {
            assertThat(executedMigrations).isEqualTo(expectedMigrations)
        }
    }
}
