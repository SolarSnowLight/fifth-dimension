package com.game.app.containers.payment.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.game.app.R
import com.game.app.containers.base.BaseFragment
import com.game.app.containers.home.models.HomeViewModel
import com.game.app.containers.payment.PaymentActivity
import com.game.app.containers.payment.adapters.SwitchTariffFragmentAdapter
import com.game.app.containers.payment.models.PaymentViewModel
import com.game.app.data.UserPreferences
import com.game.app.databinding.FragmentPaymentBinding
import com.game.app.databinding.FragmentPaymentSubscribeBinding
import com.game.app.models.payment.PaymentTokenModel
import com.game.app.network.RemoteDataSource
import com.game.app.network.Resource
import com.game.app.network.apis.UserApi
import com.game.app.repositories.UserRepository
import com.game.app.utils.handleApiError
import com.game.app.utils.navigation
import com.game.app.utils.setMarginTop
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonParser
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

class PaymentSubscribeFragment : BaseFragment<PaymentViewModel, FragmentPaymentSubscribeBinding, UserRepository>() {
    companion object {
        private const val REQUEST_CODE_TOKENIZE = 1
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(!checkAuth("Для оформления подписки необходимо авторизоваться!")){
            return
        }

        binding.toolbar.setMarginTop(128)

        binding.toolbar.setNavigationOnClickListener {
            navigation(R.id.action_paymentSubscribeFragment_to_paymentFragment)
        }

        binding.buttonPayment.setOnClickListener {
            onTokenizeButtonCLick()
        }

        onTokenizeButtonCLick()

        viewModel.paymentToken.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    if(it.value.isSuccessful){
                        lifecycleScope.launch {
                            Log.i("MYTAG", it.value.body().toString())
                        }
                    }else{
                        //handleMessage(Gson().fromJson(it.value.errorBody()?.string().toString(), ErrorModel::class.java).message!!)
                    }
                }
                is Resource.Failure -> {}
                else -> {}
            }
        }
    }

    override fun getViewModel() = PaymentViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPaymentSubscribeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() : UserRepository {
        return UserRepository(remoteDataSource.buildApi(UserApi::class.java, userPreferences, true))
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

    private fun onTokenizeButtonCLick() {
        val paymentMethodTypes = setOf(
            PaymentMethodType.BANK_CARD
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
                PaymentMethodType.BANK_CARD
            ),
            // url of the page (only https is supported) that the user should be returned to after completing 3ds.
            // customReturnUrl = "https://custom.redirect.url",

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
            requireActivity(),
            paymentParameters,
            TestParameters(showLogs = true),
            uiParameters = uiParameters
        )

        startActivityForResult(intent, REQUEST_CODE_TOKENIZE)
    }

    private fun showToken(data: Intent?) {
        if (data != null) {
            val token = Checkout.createTokenizationResult(data).paymentToken

            viewModel.uploadPaymentToken(token)

            /*CoroutineScope(Dispatchers.IO).launch {
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
            }*/
        } else {
            showError()
        }
    }

    private fun showError() {
        Log.i("MYTAG", "OPA ERROR")
        //Toast.makeText(this, R.string.tokenization_canceled, Toast.LENGTH_SHORT).show()
    }
}