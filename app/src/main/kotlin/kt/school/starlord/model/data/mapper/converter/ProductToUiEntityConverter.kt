package kt.school.starlord.model.data.mapper.converter

import android.view.View
import kt.school.starlord.domain.data.mapper.BaseConverter
import kt.school.starlord.domain.data.mapper.Mapper
import kt.school.starlord.domain.entity.product.Product
import kt.school.starlord.ui.global.entity.UiEntity
import kt.school.starlord.ui.global.entity.wrapper.LocalizedMoney
import kt.school.starlord.ui.global.entity.wrapper.LocalizedTimePassed
import kt.school.starlord.ui.products.entity.UiProduct
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.threeten.bp.Instant

/**
 * Converts Product entity from domain layer to Product entity from UI layer.
 */
class ProductToUiEntityConverter : BaseConverter<Product, UiEntity>(Product::class, UiEntity::class), KoinComponent {

    private val mapper: Mapper by inject()

    override fun convert(value: Product): UiProduct {
        val epochMilliNow = Instant.now().toEpochMilli()

        return UiProduct(
            id = value.id,
            title = value.title,
            description = value.description,
            type = value.type.stringRes,
            typeColor = value.type.colorRes,
            location = value.location,
            image = value.image,
            owner = value.owner.name,
            lastUpdate = mapper.map<LocalizedTimePassed>(epochMilliNow - value.lastUpdate).value,
            isPaid = value.isPaid,
            comments = value.commentsCount.toString(),
            price = mapper.map<LocalizedMoney>(value.price).value,
            commentsCountVisibility = if (value.commentsCount > 0) View.VISIBLE else View.INVISIBLE,
            priceVisibility = if (value.price.hasPrice) View.VISIBLE else View.GONE,
            bargainVisibility = if (value.price.isBargainAvailable) View.VISIBLE else View.GONE
        )
    }
}
