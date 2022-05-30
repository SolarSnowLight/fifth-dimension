package com.game.app.containers.payment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.game.app.containers.home.HomeActivity
import com.game.app.data.UserPreferences
import com.game.app.databinding.ActivityCourseBinding
import com.game.app.databinding.ActivityPaymentBinding
import com.game.app.models.payment.PaymentTokenModel
import com.game.app.network.RemoteDataSource
import com.game.app.network.apis.UserApi
import com.game.app.utils.makeStatusBarTransparent
import com.game.app.utils.startNewActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.*
import ru.yoomoney.sdk.kassa.payments.ui.color.ColorScheme
import java.math.BigDecimal
import java.util.*

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var api: UserApi
    private val remoteDataSource = RemoteDataSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startNewActivity(
            HomeActivity::class.java,
            "Модуль оплаты находится в стадии разработки"
        )

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeStatusBarTransparent()
        /*userPreferences = UserPreferences(applicationContext)
        api = remoteDataSource.buildApi(UserApi::class.java, userPreferences, true)

        onTokenizeButtonCLick()*/
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_TOKENIZE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    showToken(data)
                }

                Activity.RESULT_CANCELED -> {
                    showError()
                }
            }
        }
    }

    /*private fun initUi() {
        setContentView(R.layout.activity_main)
        tokenizeButton.setOnClickListener {
            onTokenizeButtonCLick()
        }
    }*/

    private fun onTokenizeButtonCLick() {
        val paymentMethodTypes = setOf(
            PaymentMethodType.BANK_CARD,
            PaymentMethodType.SBERBANK
        )

        val paymentParameters = PaymentParameters(
            amount = Amount(BigDecimal.valueOf(1500), Currency.getInstance("RUB")),
            title = "Недельная подписка",
            subtitle = "Подписка на просмотр платного контента ровно на 1 неделю",
            // key for client apps from the YooMoney Merchant Profile
            clientApplicationKey = "test_OTAyOTQ2Qe97b5YiTKUDxL0C1TyV01pUkMmtNyv24IM",
            // ID of the store in the YooMoney system
            shopId = "902946",
            // flag of the disabled option to save payment methods,
            savePaymentMethod = SavePaymentMethod.OFF,
            // the full list of available payment methods has been provided
            paymentMethodTypes = setOf(
                PaymentMethodType.BANK_CARD,
                PaymentMethodType.SBERBANK
            ),
            // url of the page (only https is supported) that the user should be returned to after completing 3ds.
            customReturnUrl = "https://custom.redirect.url",
            // user's phone number for autofilling the user phone number field in SberPay. Supported data format: "+7XXXXXXXXXX"
            // userPhoneNumber = "+79041234567",
            // ID received upon registering the app on the https://yookassa.ru website
            authCenterClientId = "test_W0NqMvFPS4OtP1q4XB8qVDF8pc_SrXXRDteOmicIjK8"
        )

        val uiParameters = UiParameters(
            showLogo = false,
            colorScheme = ColorScheme(Color.rgb(0, 114, 245))
        )

        val intent = Checkout.createTokenizeIntent(
            this,
            paymentParameters,
            TestParameters(showLogs = true),
            uiParameters = uiParameters
        )

        startActivityForResult(intent, REQUEST_CODE_TOKENIZE)
    }

    private fun showToken(data: Intent?) {
        if (data != null) {
            val token = Checkout.createTokenizationResult(data).paymentToken

            CoroutineScope(Dispatchers.IO).launch {
                var paymentToken = Gson().toJson(
                    PaymentTokenModel(
                        paymentToken = token
                    )
                )

                val paymentTokenRequest = paymentToken.toRequestBody("application/json".toMediaTypeOrNull())

                val response = api.paymentToken(paymentTokenRequest)

                /*if ((response.isSuccessful)
                    && (response.body() != null)
                    && (response.code() == 201)
                ) {
                    val courses = Gson().fromJson(
                        Gson().toJson(
                            JsonParser.parseString(response.body()?.string())
                        ),
                        CourseDataModel::class.java
                    )
                }*/
            }
        } else {
            showError()
        }
    }

    private fun showError() {
        Log.i("MYTAG", "OPA ERROR")
        //Toast.makeText(this, R.string.tokenization_canceled, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CODE_TOKENIZE = 1
    }
}