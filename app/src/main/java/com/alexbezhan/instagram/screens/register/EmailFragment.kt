package com.alexbezhan.instagram.screens.register

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.screens.common.coordinateBtnAndInputs
import kotlinx.android.synthetic.main.fragment_register_email.*

class EmailFragment : Fragment() {
    private lateinit var mListener: Listener

    interface Listener {
        fun onEmailEntered(email: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnAndInputs(next_btn, email_input)

        next_btn.setOnClickListener {
            val email = email_input.text.toString()
            mListener.onEmailEntered(email)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }
}