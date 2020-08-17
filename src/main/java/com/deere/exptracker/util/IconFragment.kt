package com.deere.exptracker.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.deere.exptracker.R
import com.deere.exptracker.category.NewCategoryFragment
import com.deere.exptracker.databinding.FragmentIconBinding

class IconFragment : DialogFragment(), View.OnClickListener {
    val TAG = "IconFragment"
    lateinit var binding: FragmentIconBinding
    lateinit var imgSrc: String
    lateinit var bundle: Bundle
    lateinit var newCategoryFragment: NewCategoryFragment
    lateinit var intent: Intent


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIconBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Bind onclick event for Images
        binding.travel.setOnClickListener(this)
        binding.cloths.setOnClickListener(this)
        binding.eatingOut.setOnClickListener(this)
        binding.entertainment.setOnClickListener(this)
        binding.fuel.setOnClickListener(this)
        binding.general.setOnClickListener(this)
        binding.gift.setOnClickListener(this)
        binding.holiday.setOnClickListener(this)
        binding.kids.setOnClickListener(this)
        binding.shopping.setOnClickListener(this)
        binding.sports.setOnClickListener(this)
        binding.bills.setOnClickListener(this)
        binding.drinks.setOnClickListener(this)
        binding.medical.setOnClickListener(this)
        binding.mobile.setOnClickListener(this)
        binding.phone.setOnClickListener(this)
        binding.tea.setOnClickListener(this)
        binding.television.setOnClickListener(this)
        binding.wifi.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.travel -> setImageSrc(R.drawable.ic_travel, "ic_travel")
            R.id.cloths -> setImageSrc(R.drawable.ic_cloths, "ic_cloths")
            R.id.eating_out -> setImageSrc(R.drawable.ic_eating_out, "ic_eating_out")
            R.id.entertainment -> setImageSrc(R.drawable.ic_entertainment, "ic_entertainment")
            R.id.fuel -> setImageSrc(R.drawable.ic_fuel, "ic_fuel")
            R.id.general -> setImageSrc(R.drawable.ic_general, "ic_general")
            R.id.gift -> setImageSrc(R.drawable.ic_gift, "ic_gift")
            R.id.holiday -> setImageSrc(R.drawable.ic_holiday, "ic_holiday")
            R.id.kids -> setImageSrc(R.drawable.ic_kids, "ic_kids")
            R.id.shopping -> setImageSrc(R.drawable.ic_shopping, "ic_shopping")
            R.id.sports -> setImageSrc(R.drawable.ic_sports, "ic_sports")
            R.id.bills -> setImageSrc(R.drawable.ic_bills, "ic_bills")
            R.id.drinks -> setImageSrc(R.drawable.ic_drinks, "ic_drinks")
            R.id.mobile -> setImageSrc(R.drawable.ic_mobile, "ic_mobile")
            R.id.phone -> setImageSrc(R.drawable.ic_phone, "ic_phone")
            R.id.tea -> setImageSrc(R.drawable.ic_tea, "ic_tea")
            R.id.medical -> setImageSrc(R.drawable.ic_medical, "ic_medical")
            R.id.wifi -> setImageSrc(R.drawable.ic_wifi, "ic_wifi")
            R.id.television -> setImageSrc(R.drawable.ic_television, "ic_television")
        }
    }

    private fun setImageSrc(imgSrc: Int, imgName: String) {
        if (targetFragment == null) return
        newCategoryFragment = NewCategoryFragment()
        intent = newCategoryFragment.newIntent(imgSrc, imgName)
        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }


}
