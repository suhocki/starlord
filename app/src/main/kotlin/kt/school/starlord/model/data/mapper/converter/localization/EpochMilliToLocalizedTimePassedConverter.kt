package kt.school.starlord.model.data.mapper.converter.localization

import kt.school.starlord.R
import kt.school.starlord.domain.data.mapper.BaseConverter
import kt.school.starlord.domain.entity.global.EpochMilli
import kt.school.starlord.ui.global.entity.wrapper.LocalizedTimePassed
import kt.school.starlord.model.data.resources.ResourceManager
import org.threeten.bp.Duration

/**
 * Converts EpochMilli to LocalizedTimePassed entity from UI layer.
 */
open class EpochMilliToLocalizedTimePassedConverter(
    private val resources: ResourceManager
) : BaseConverter<EpochMilli, LocalizedTimePassed>(EpochMilli::class.java, LocalizedTimePassed::class.java) {

    override fun convert(value: EpochMilli): LocalizedTimePassed {
        val epochMilli = value.value

        return LocalizedTimePassed(
            when {
                epochMilli >= MILLIS_IN_MONTH -> {
                    val month = Duration.ofMillis(epochMilli).toDays() / 30
                    resources.getPlural(R.plurals.month_ago, month.toInt(), month)
                }
                epochMilli >= MILLIS_IN_DAY -> {
                    val days = Duration.ofMillis(epochMilli).toDays()
                    resources.getPlural(R.plurals.days_ago, days.toInt(), days)
                }
                epochMilli >= MILLIS_IN_HOUR -> {
                    val hours = Duration.ofMillis(epochMilli).toHours()
                    resources.getPlural(R.plurals.hours_ago, hours.toInt(), hours)
                }
                epochMilli >= MILLIS_IN_MINUTE -> {
                    val minutes = Duration.ofMillis(epochMilli).toMinutes()
                    resources.getPlural(R.plurals.minutes_ago, minutes.toInt(), minutes)
                }
                else -> resources.getString(R.string.less_than_minute_ago)
            }
        )
    }

    companion object {
        private const val MILLIS_IN_MINUTE = 60 * 1000L
        private const val MILLIS_IN_HOUR = 60 * MILLIS_IN_MINUTE
        private const val MILLIS_IN_DAY = 24 * MILLIS_IN_HOUR
        private const val MILLIS_IN_MONTH = 30 * MILLIS_IN_DAY
    }
}
