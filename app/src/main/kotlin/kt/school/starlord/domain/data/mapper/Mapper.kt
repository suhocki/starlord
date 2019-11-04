package kt.school.starlord.domain.data.mapper

import timber.log.Timber

/**
 * Designed for mapping objects to different types using converters.
 */
class Mapper(inline val converters: Set<Converter<*, *>>) {

    init {
        validateConverters()
    }

    /**
     * Maps input object to specified type.
     *
     * @return required type object
     * @throws NoSuchElementException when there is no appropriate converter
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified To> map(input: Any): To {
        if (input is To) return input

        val converter = converters.find {
            Timber.d("${it.fromClass} can be converted to ${it.toClass}")
            it.fromClass.isAssignableFrom(input::class.java) &&
                    To::class.java.isAssignableFrom(it.toClass)
        } ?: throw NoSuchElementException("Cannot find converter from ${input::class.java} to ${To::class.java}")

        return (converter as Converter<Any, To>).convert(input)
    }

    private fun validateConverters() {
        converters.forEach {

        }
    }
}
