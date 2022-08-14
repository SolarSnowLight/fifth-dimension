package com.game.app.containers.payment.tariffs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.game.app.R

class TariffMonthFragment(
    private var value: String,
    private var description: String,
    private var price: Int
) : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_tariff_month, container, false)

        view.findViewById<TextView>(R.id.textViewDescription).text = description
        view.findViewById<TextView>(R.id.textViewValue).text = value
        view.findViewById<TextView>(R.id.textViewPrice).text = "$price руб."

        return view
    }
}