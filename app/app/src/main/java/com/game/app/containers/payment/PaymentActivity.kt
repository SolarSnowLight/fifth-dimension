package com.game.app.containers.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.game.app.databinding.ActivityPaymentBinding
import com.game.app.utils.makeStatusBarTransparent

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*startNewActivity(
            HomeActivity::class.java,
            "Модуль оплаты находится в стадии разработки"
        )*/

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeStatusBarTransparent()
    }
}