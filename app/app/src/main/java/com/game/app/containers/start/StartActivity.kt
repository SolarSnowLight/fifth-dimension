package com.game.app.containers.start

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.game.app.databinding.ActivityStartBinding
import com.game.app.utils.makeStatusBarTransparent

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeStatusBarTransparent()
    }
}