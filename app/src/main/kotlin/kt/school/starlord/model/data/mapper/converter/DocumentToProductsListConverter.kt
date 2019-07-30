package kt.school.starlord.model.data.mapper.converter

import kt.school.starlord.entity.ProductOwner
import kt.school.starlord.entity.product.Product
import kt.school.starlord.entity.product.ProductPrice
import kt.school.starlord.entity.product.ProductType
import kt.school.starlord.entity.product.ProductsList
import kt.school.starlord.model.data.mapper.converter.DocumentToCategoriesWithSubcategoriesConverter.Companion.LINK
import kt.school.starlord.model.data.mapper.entity.BaseConverter
import org.jsoup.nodes.Document

/**
 * Contains logic on how to convert Jsoup Docu  ment to CategoriesWithSubcategories entity.
 */
class DocumentToProductsListConverter : BaseConverter<Document, ProductsList>(
    Document::class.java, ProductsList::class.java
) {
    override fun convert(value: Document): ProductsList = ProductsList(
        HashSet<Product>().apply {
            value.getElementsByClass(TABLE).forEach { table ->
                table.getElementsByTag(TR).forEach { product ->
                    val signature = product.getElementsByClass(SIGNATURE).first()
                    val title = product.getElementsByClass(TITLE)
                    val cost = product.getElementsByClass(COST).first()
                    if (title.hasText() && signature != null && cost != null) add(
                        Product(
                            id = title.first().getElementsByTag(A).attr(LINK).split("=").last().toLong(),
                            title = title.text(),
                            description = if (product.getElementsByClass(DESCRIPTION).isNotEmpty()) {
                                product.getElementsByClass(DESCRIPTION).text()
                            } else {
                                ""
                            },
                            image = product.getElementsByClass(IMAGE)
                                .first()
                                .getElementsByTag(IMG)
                                .first()
                                .attr(SRC),
                            price = ProductPrice(
                                amount = cost.let { c ->
                                    if (c.hasText()) {
                                        c.getElementsByClass(PRICE).text()
                                            .replace(",", ".")
                                            .split(" ")[0]
                                            .toDoubleOrNull()
                                    } else {
                                        null
                                    }
                                },
                                isBargainAvailable = cost.getElementsByClass(COST_TORG).hasText()
                            ),
                            location = if (signature.hasText()) {
                                signature.getElementsByTag(STRONG).first().text()
                            } else {
                                ""
                            },
                            lastUpdate = product.getElementsByClass(LAST_UPDATE).first().text(),
                            commentsCount = if (product.getElementsByClass(COMMENTS).isNotEmpty()) {
                                product.getElementsByClass(COMMENTS).text().toLong()
                            } else {
                                0L
                            },
                            type = product.getElementsByClass(TYPE).let {
                                when {
                                    it.hasClass(SELL) -> ProductType.SELL
                                    it.hasClass(BUY) -> ProductType.BUY
                                    it.hasClass(RENT) -> ProductType.RENT
                                    it.hasClass(EXCHANGE) -> ProductType.EXCHANGE
                                    it.hasClass(SERVICE) -> ProductType.SERVICE
                                    it.hasClass(CLOSED) -> ProductType.CLOSED
                                    else -> ProductType.NON
                                }
                            },
                            owner = signature.let {
                                val owner = it.getElementsByTag(A).first()
                                ProductOwner(
                                    id = owner.attr(LINK).split(SEPARATOR).last().toLong(),
                                    name = owner.text()
                                )
                            },
                            isPaid = product.hasClass(M_IMP)
                        )
                    )
                }
            }
        }.toList()
    )

    companion object {
        const val A = "a"
        const val COST = "cost"
        const val COST_TORG = "cost-torg"
        const val TABLE = "ba-tbl-list__table"
        const val TR = "tr"
        const val TITLE = "wraptxt"
        const val DESCRIPTION = "ba-description"
        const val PRICE = "price-primary"
        const val IMAGE = "img-va"
        const val SIGNATURE = "ba-signature"
        const val COMMENTS = "c-org"
        const val IMG = "img"
        const val SRC = "src"
        const val STRONG = "strong"
        const val LAST_UPDATE = "ba-post-up"
        const val SEPARATOR = "user/"
        const val M_IMP = "m-imp"
        const val TYPE = "ba-label"
        const val SELL = "ba-label-2"
        const val BUY = "ba-label-3"
        const val EXCHANGE = "ba-label-4"
        const val SERVICE = "ba-label-5"
        const val RENT = "ba-label-6"
        const val CLOSED = "ba-label-7"
    }
}

