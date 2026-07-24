package com.rakshasetu.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rakshasetu.app.R

class OnboardingPagerAdapter : RecyclerView.Adapter<OnboardingPagerAdapter.OnboardingViewHolder>() {

    private val pages = listOf(
        R.layout.page_onboarding_welcome,
        R.layout.page_onboarding_permissions,
        R.layout.page_onboarding_location,
        R.layout.page_onboarding_battery,
        R.layout.page_onboarding_contacts,
        R.layout.page_onboarding_calibration
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        // Pages are static layouts — no dynamic binding needed
    }

    override fun getItemCount(): Int = pages.size

    override fun getItemViewType(position: Int): Int = pages[position]

    class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
