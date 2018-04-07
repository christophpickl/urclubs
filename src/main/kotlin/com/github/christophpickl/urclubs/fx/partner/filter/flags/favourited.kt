package com.github.christophpickl.urclubs.fx.partner.filter.flags

import com.github.christophpickl.urclubs.fx.ImageId
import com.github.christophpickl.urclubs.fx.Images
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPartnersView

class FavouritedFilterSpec(
    view: FilterPartnersView
) : FlagFilterSpec(view.favouritedFilterButton, { favourited })

class FavouritedFilterButton : FlagFilterButton(
    imageTrue = Images[ImageId.FAVOURITE_FULL],
    imageFalse = Images[ImageId.FAVOURITE_OUTLINE]
)
