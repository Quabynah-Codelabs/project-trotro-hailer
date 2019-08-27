package dev.trotrohailer.passenger.util.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.trotrohailer.shared.util.Constants

class PaymentPrefs private constructor(context: Context) {
    companion object {
        @Volatile
        private var instance: PaymentPrefs? = null

        fun get(context: Context): PaymentPrefs = instance ?: synchronized(this) {
            instance ?: PaymentPrefs(context).also { instance = it }
        }
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(
            Constants.PASSENGERS,
            Context.MODE_PRIVATE
        )
    }

    private var defPaymentMethod: String? = null
    private val _paymentMethod = MutableLiveData<String?>()
    val paymentMethod: LiveData<String?> = _paymentMethod

    init {
        defPaymentMethod = prefs.getString("key_payment", null)
        _paymentMethod.value = defPaymentMethod
    }

    fun updatePayment(payment: String) {
        defPaymentMethod = payment
        prefs.edit {
            putString("key_payment", payment)
            apply()
        }
        _paymentMethod.value = payment
    }

    fun removePayment() {
        defPaymentMethod = null
        prefs.edit {
            putString("key_payment", null)
            apply()
        }
        _paymentMethod.value = defPaymentMethod
    }

}
