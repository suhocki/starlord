package kt.school.starlord.model.system.viewmodel

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import kt.school.starlord.domain.system.viewmodel.ErrorEmitter
import timber.log.Timber

/**
 * Implements ErrorEmitter.
 */
class ErrorViewModelFeature : ErrorEmitter {

    private val error = LiveEvent<Throwable>()

    override fun getError(): LiveData<Throwable> = error

    /**
     * Sends error to corresponding LiveData.
     */
    fun showError(throwable: Throwable) {
        Timber.e(throwable)
        error.value = throwable
    }
}
