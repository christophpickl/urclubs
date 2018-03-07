package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.domain.partner.Rating
import javafx.scene.control.ListCell

private val log = LOG {}

class CategoryCell : ListCell<Category>() {
    override fun updateItem(item: Category?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.label
    }
}

class RatingCell : ListCell<Rating>() {
    override fun updateItem(item: Rating?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.label
    }
}
