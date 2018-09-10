package non_test

import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.fx.SyncReport
import com.github.christophpickl.urclubs.fx.SyncReportDialog
import com.github.christophpickl.urclubs.fx.SyncResultEvent
import com.github.christophpickl.urclubs.service.sync.FinishedActivitySyncReport
import com.github.christophpickl.urclubs.service.sync.PartnerSyncReport
import com.github.christophpickl.urclubs.service.sync.UpcomingActivitySyncReport
import javafx.application.Application
import javafx.scene.layout.BorderPane
import tornadofx.*

fun main(args: Array<String>) {
    Application.launch(SyncReportViewApp::class.java, *args)
}

class MyView : View() {
    override val root = BorderPane()

    init {
        with(root) {
            center {
                button("open") {
                    SyncReportDialog().apply {
                        updateView(SyncResultEvent(SyncReport(
                                partners = PartnerSyncReport(
                                        insertedPartners = listOf(
                                                Partner.prototype().copy(name = "inserted1"),
                                                Partner.prototype().copy(name = "inserted2"),
                                                Partner.prototype().copy(name = "inserted3"),
                                                Partner.prototype().copy(name = "inserted4"),
                                                Partner.prototype().copy(name = "inserted5")
                                        ),
                                        deletedPartners = listOf(Partner.prototype().copy(name = "deleted"))
                                ),
                                finishedActivities = FinishedActivitySyncReport(emptyList()),
                                upcomingActivities = UpcomingActivitySyncReport(emptyList())
                        )))
                    }
                            .showAndWait()
                    action {
                    }
                }
            }
        }
    }
}

class SyncReportViewApp : App(primaryView = MyView::class) {
    init {

    }
}
