package dev.trotrohailer.passenger.ui.driver

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import dev.trotrohailer.passenger.R
import dev.trotrohailer.shared.data.Driver
import dev.trotrohailer.shared.glide.load
import kotlinx.android.synthetic.main.item_driver.view.*

class BusDriversAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val datasource = mutableListOf<Driver>()
    private var isLoading: Boolean = true

    private val inflater: LayoutInflater get() = LayoutInflater.from(context)

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoading -> R.layout.loading
            !isLoading && datasource.isEmpty() -> R.layout.item_empty_list_drivers
            else -> R.layout.item_driver
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_driver -> DriverViewHolder(inflater.inflate(viewType, parent, false))
            R.layout.loading -> LoadingViewHolder(inflater.inflate(viewType, parent, false))
            R.layout.item_empty_list_drivers -> EmptyViewHolder(
                inflater.inflate(
                    viewType,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int = if (isLoading || datasource.isEmpty()) 1 else datasource.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_driver -> bindDriverView(datasource[position], holder as DriverViewHolder)
        }
    }

    private fun bindDriverView(driver: Driver, holder: DriverViewHolder) {
        holder.v.driver_name.text = driver.name
        holder.v.driver_vehicle_number.text = driver.vehicleNumber
        holder.v.driver_vehicle.text = driver.vehicle
        holder.v.driver_avatar.load(driver.avatar?.toUri(), true)
        holder.v.driver_vehicle_image.load(R.drawable.ic_bus)

        holder.v.setOnClickListener {
            // todo: request driver and receive updates on vehicle location

        }
    }

    fun addDrivers(drivers: MutableList<Driver>) {
        isLoading = false
        datasource.clear()
        datasource.addAll(drivers)
        notifyDataSetChanged()
    }


    class DriverViewHolder(val v: View) : RecyclerView.ViewHolder(v)

    class LoadingViewHolder(val v: View) : RecyclerView.ViewHolder(v)

    class EmptyViewHolder(val v: View) : RecyclerView.ViewHolder(v)
}