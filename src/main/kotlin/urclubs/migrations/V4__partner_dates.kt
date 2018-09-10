package urclubs.migrations

import org.flywaydb.core.api.migration.jdbc.JdbcMigration
import java.sql.Connection

class V4__partner_dates : JdbcMigration {

    companion object {
        val defaultDate = "2000-01-01 00:00:00"
    }

    override fun migrate(connection: Connection) {
        connection.prepareStatement("ALTER TABLE PartnerDbo ADD COLUMN dateInserted TIMESTAMP DEFAULT TIMESTAMP '$defaultDate' NOT NULL ").execute()
        connection.prepareStatement("ALTER TABLE PartnerDbo ADD COLUMN dateDeleted TIMESTAMP").execute()

        connection.prepareStatement("UPDATE PartnerDbo SET dateDeleted = TIMESTAMP '$defaultDate' WHERE deletedByMyc = true").execute()
        connection.prepareStatement("ALTER TABLE PartnerDbo DROP COLUMN deletedByMyc").execute()

        connection.prepareStatement("ALTER TABLE PartnerDbo ALTER COLUMN dateInserted SET DEFAULT NULL").execute()
        connection.commit()
    }

}
