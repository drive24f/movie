package com.movie.ui.categories

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.movie.common.Constanst.NOWPLAYING
import com.movie.common.Constanst.POPULAR
import com.movie.common.Constanst.TOPRATED
import com.movie.databinding.FragmentCategoriesBinding
import java.lang.Boolean.FALSE

class CategoriesFragment : BottomSheetDialogFragment() {

    private lateinit var getActivity: FragmentActivity
    private lateinit var binding: FragmentCategoriesBinding

    private lateinit var listener: (Int) -> Unit

    companion object {
        fun create() = CategoriesFragment()
    }

    fun show(context: Context) {
        when {
            this.isAdded -> {
                this.dismissAllowingStateLoss()
                onStart()
            }
            else -> {
                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.add(this, CategoriesFragment::class.java.simpleName)
                fragmentTransaction.commitAllowingStateLoss()
                this.isCancelable = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let { getActivity = it }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, FALSE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        initButton()
    }

    private fun initButton() {
        binding.let {
            it.btnPopular.setOnClickListener {
                listener(POPULAR)
                destroyFragment()
            }

            it.btnTopRated.setOnClickListener {
                listener(TOPRATED)
                destroyFragment()
            }

            it.btnNowPlaying.setOnClickListener {
                listener(NOWPLAYING)
                destroyFragment()
            }
        }
    }

    private fun destroyFragment() {
        val fragmentManager: FragmentManager = (context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(CategoriesFragment()).commitAllowingStateLoss()
        this.dismissAllowingStateLoss()
    }

    fun getItem(listener: (Int) -> Unit) {
        this.listener = listener
    }

    override fun onStop() {
        super.onStop()
        destroyFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyFragment()
    }
}