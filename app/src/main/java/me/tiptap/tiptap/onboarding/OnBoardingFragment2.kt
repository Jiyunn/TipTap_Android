package me.tiptap.tiptap.onboarding

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.FragmentOnboard2Binding

class OnBoardingFragment2 : Fragment() {

    private lateinit var binding : FragmentOnboard2Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboard2, container, false)
        return binding.root

    }
}