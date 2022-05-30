package com.game.app.containers.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.game.app.R
import com.game.app.containers.home.HomeActivity
import com.game.app.databinding.ActivityAuthBinding
import com.game.app.utils.handleErrorMessage
import com.game.app.utils.handleMessage
import com.game.app.utils.handleWarningMessage
import com.game.app.utils.startNewActivity


class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.getStringExtra("message") != null){
            if(intent.getStringExtra("type") != null){
                when(intent.getStringExtra("type").toString()){
                    "error" -> {
                        handleErrorMessage(binding.root, intent.getStringExtra("message").toString())
                    }
                    "warning" -> {
                        handleWarningMessage(binding.root, intent.getStringExtra("message").toString())
                    }
                    else -> {
                        handleMessage(binding.root, intent.getStringExtra("message").toString())
                    }
                }
            }else{
                handleMessage(binding.root, intent.getStringExtra("message").toString())
            }
        }
    }

    override fun onBackPressed() {
        when(binding.fragmentContainerView.findNavController().currentDestination?.id){
            R.id.loginFragment -> {
                startNewActivity(HomeActivity::class.java)
            }
            R.id.registerFragment -> {
                binding.fragmentContainerView.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            else -> {
                startNewActivity(HomeActivity::class.java)
            }
        }
    }
}