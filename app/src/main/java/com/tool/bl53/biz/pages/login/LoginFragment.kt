package com.tool.bl53.biz.pages.login

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tool.bl53.R
import com.tool.bl53.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var viewBinding: FragmentLoginBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentLoginBinding.bind(view)
        viewBinding.username.addTextChangedListener(afterTextChanged = {
            if (viewBinding.username.text.isNullOrEmpty() || viewBinding.password.text.isNullOrEmpty()) {
                viewBinding.login.setBackgroundResource(R.drawable.shape_btn_disable)
                viewBinding.login.isEnabled = false
            } else {
                viewBinding.login.setBackgroundResource(R.drawable.selector_btn_eable)
                viewBinding.login.isEnabled = true
            }
        })
        viewBinding.password.addTextChangedListener(afterTextChanged = {
            if (viewBinding.username.text.isNullOrEmpty() || viewBinding.password.text.isNullOrEmpty()) {
                viewBinding.login.setBackgroundResource(R.drawable.shape_btn_disable)
                viewBinding.login.isEnabled = false
            } else {
                viewBinding.login.setBackgroundResource(R.drawable.selector_btn_eable)
                viewBinding.login.isEnabled = true
            }
        })
        viewBinding.login.setOnClickListener {
            findNavController().navigate(R.id.main_activity)
            requireActivity().finishAfterTransition()
        }
    }

}