package com.rakshasetu.app.ui.alertlog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rakshasetu.app.R
import com.rakshasetu.app.data.entity.AlertLog
import com.rakshasetu.app.data.repository.AlertRepository
import com.rakshasetu.app.databinding.ActivityAlertLogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AlertLogActivity : AppCompatActivity() {

    @Inject lateinit var alertRepository: AlertRepository

    private lateinit var binding: ActivityAlertLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            alertRepository.getRecentAlerts(100).collectLatest { alerts ->
                if (alerts.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.recyclerView.adapter = AlertLogAdapter(alerts)
                }
            }
        }
    }

    class AlertLogAdapter(
        private val alerts: List<AlertLog>
    ) : RecyclerView.Adapter<AlertLogAdapter.ViewHolder>() {

        private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvTriggerType: TextView = view.findViewById(R.id.tvTriggerType)
            val tvTime: TextView = view.findViewById(R.id.tvTime)
            val tvLocation: TextView = view.findViewById(R.id.tvLocation)
            val tvStatus: TextView = view.findViewById(R.id.tvStatus)
            val tvStats: TextView = view.findViewById(R.id.tvStats)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_alert_log, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val alert = alerts[position]

            holder.tvTriggerType.text = when (alert.triggerType) {
                AlertLog.TRIGGER_SHAKE -> "📱 Shake Trigger"
                AlertLog.TRIGGER_VOLUME -> "🔊 Volume Button"
                AlertLog.TRIGGER_MANUAL -> "👆 Manual SOS"
                AlertLog.TRIGGER_NOTIFICATION -> "🔔 Notification"
                AlertLog.TRIGGER_AIRPLANE -> "✈️ Airplane Mode"
                else -> "❓ Unknown"
            }

            holder.tvTime.text = dateFormat.format(Date(alert.startedAt))

            holder.tvLocation.text = if (alert.latitude != null && alert.longitude != null) {
                "📍 ${String.format("%.4f, %.4f", alert.latitude, alert.longitude)}" +
                    if (alert.accuracy != null) " (±${alert.accuracy.toInt()}m)" else ""
            } else {
                "📍 Location not available"
            }

            holder.tvStatus.text = when {
                alert.isCancelled -> "❌ Cancelled"
                alert.isDuress -> "⚠️ Duress Alert"
                alert.endedAt != null -> "✅ Completed"
                else -> "🔴 Active"
            }

            holder.tvStats.text = buildString {
                append("SMS: ${alert.smsSentCount}")
                append(" | Calls: ${alert.callsMadeCount}")
                if (alert.isEscalated) append(" | ⬆️ Escalated")
                alert.batteryLevel?.let { append(" | 🔋 $it%") }
            }
        }

        override fun getItemCount(): Int = alerts.size
    }
}
