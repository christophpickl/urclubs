package com.github.christophpickl.urclubs.fx.partner.filter.flags

import com.github.christophpickl.urclubs.fx.ImageId
import com.github.christophpickl.urclubs.fx.Images
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPartnersView

class WishlistedFilterSpec(
    view: FilterPartnersView
) : FlagFilterSpec(view.wishlistedFilterButton, { wishlisted })

class WishlistedFilterButton : FlagFilterButton(
    imageTrue = Images[ImageId.WISHLIST_FULL],
    imageFalse = Images[ImageId.WISHLIST_OUTLINE]
)
