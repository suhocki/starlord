package kt.school.starlord.domain.entity.product

import kt.school.starlord.domain.entity.global.EpochMilli
import kt.school.starlord.domain.entity.global.LocalizedTimePassed

/**
 * Domain product entity.
 * Can represents not only products but services.
 *
 * @param epochMilli time when the product was updated.
 * @param localizedTimePassed localized human-readable time elapsed after the product was updated.
 * @param isPaid if true, the product is premium item and should be located at the top of the list.
 */
data class Product(
    val id: Long,
    val title: String,
    val description: String,
    val type: ProductType,
    val location: String,
    val image: String,
    val owner: ProductOwner,
    val price: Price,
    var epochMilli: EpochMilli,
    val localizedTimePassed: LocalizedTimePassed,
    val commentsCount: Long,
    val subcategoryName: String = "", // todo. make it nullable
    val isPaid: Boolean = false
)
