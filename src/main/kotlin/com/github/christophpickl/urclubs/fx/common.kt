package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.urclubs.domain.partner.Category
import javafx.scene.control.ListCell

class CategoryCell : ListCell<Category>() {
    override fun updateItem(item: Category?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.label
    }
}
