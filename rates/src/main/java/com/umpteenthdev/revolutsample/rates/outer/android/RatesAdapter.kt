package com.umpteenthdev.revolutsample.rates.outer.android

import android.content.Context
import android.text.Editable
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.perf.metrics.AddTrace
import com.umpteenthdev.revolutsample.core.outer.android.RegexInputFilter
import com.umpteenthdev.revolutsample.rates.R
import com.umpteenthdev.revolutsample.rates.adapters.imageloading.ImageLoader
import com.umpteenthdev.revolutsample.rates.adapters.ui.RateUiModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class RatesAdapter(
    private val imageLoader: ImageLoader,
    private val callback: Callback,
    appContext: Context
) : RecyclerView.Adapter<RatesAdapter.RateViewHolder>() {

    private val formatter: DecimalFormat = DecimalFormat("#.##")
    private val separator: Char = DecimalFormatSymbols(Locale.getDefault()).decimalSeparator
    private val inputFilters = arrayOf(RegexInputFilter("""((\d{0,6})(\.|,|)(\d{0,2})|)""".toRegex()))
    private val nonModifiableInputRegex = """([1-9]\d*|0)[.,]0*""".toRegex()
    private var data: List<RateUiModel> = listOf()
    private val doubleSpecialValues = setOf("NaN", "-Infinity", "Infinity")
    private var isOnBindExecuting = false
    private val inputMethodManager: InputMethodManager by lazy { appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    @AddTrace(name = "RatesAdapter_ViewHolder_Creation", enabled = true)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder = RateViewHolder(parent)

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun onBindViewHolder(holder: RateViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    /**
     * Calculate diff on UI thread to avoid inconsistency
     */
    fun submitList(newList: List<RateUiModel>) {
        if (isOnBindExecuting) return
        val callback = DiffUtilCallback(data, newList)
        val diffResult = DiffUtil.calculateDiff(callback)
        data = newList
        diffResult.dispatchUpdatesTo(this)
    }

    interface Callback {
        fun onItemClick(item: RateUiModel)
        fun onAmountChanged(item: RateUiModel, amount: Double?)
    }

    private class DiffUtilCallback(val oldList: List<RateUiModel>, val newList: List<RateUiModel>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].symbol == newList[newItemPosition].symbol
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        /**
         * Avoid creating additional [RateViewHolder] on item changing
         */
        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return Unit
        }
    }

    inner class RateViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_rate, parent, false)
    ) {

        private val currencySymbolView: TextView = itemView.findViewById(R.id.currencySymbol)
        private val currencyFullNameView: TextView = itemView.findViewById(R.id.currencyFullName)
        private val currencyIcon: ImageView = itemView.findViewById(R.id.currencyIcon)
        private val currencyInput: EditText = itemView.findViewById(R.id.currencyInput)

        init {
            currencyInput.keyListener = DigitsKeyListener.getInstance("0123456789$separator")
        }

        fun bind(item: RateUiModel) {
            isOnBindExecuting = true

            updateInputText(item)
            handleFocusChanges(item)
            updateCurrencyDependentViews(item)
            handleClicks(item)
            handleTextChanges(item)

            isOnBindExecuting = false
        }

        private fun updateInputText(item: RateUiModel) {
            val itemAmountString = item.amount?.toString()
            val formattedAmount = if (itemAmountString in doubleSpecialValues || itemAmountString == null) "" else formatter.format(item.amount)

            if (currencyInput.hasFocus()) {
                val currentText = currencyInput.text?.toString()
                if (currentText != formattedAmount &&
                    !(currentText != null &&
                            nonModifiableInputRegex.matches(currentText) &&
                            (itemAmountString == null || currentText.inputToDoubleOrNull() == formattedAmount.inputToDoubleOrNull()))
                ) {
                    currencyInput.setText(formattedAmount)
                    if (currencyInput.hasFocus()) {
                        // A case when leading zeros have to be removed
                        currencyInput.setSelection(currencyInput.text?.length ?: 0)
                    }
                }
            } else {
                currencyInput.setText(formattedAmount)
            }
        }

        private fun handleFocusChanges(item: RateUiModel) {
            currencyInput.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    currencyInput.filters = inputFilters
                    callback.onItemClick(item)
                } else {
                    currencyInput.filters = arrayOf()
                }
            }
        }

        private fun updateCurrencyDependentViews(item: RateUiModel) {
            if (item.symbol != currencySymbolView.text) {
                currencySymbolView.text = item.symbol
                currencyFullNameView.text = item.fullName
                imageLoader.loadTo(currencyIcon, item.iconUrl, R.drawable.ic_place)
            }
        }

        private fun handleClicks(item: RateUiModel) {
            itemView.setOnClickListener {
                currencyInput.requestFocus()
                currencyInput.setSelection(currencyInput.text?.length ?: 0)
                inputMethodManager.showSoftInput(currencyInput, InputMethodManager.SHOW_IMPLICIT)
                callback.onItemClick(item)
            }
        }

        private fun handleTextChanges(item: RateUiModel) {

            currencyInput.addTextChangedListener(object : AfterTextChangedListener() {

                override fun afterTextChanged(s: Editable?) {
                    if (!currencyInput.hasFocus()) return

                    var amount = s?.toString().inputToDoubleOrNull()
                    if (amount == .0) amount = null
                    callback.onAmountChanged(item, amount)
                }
            })
        }

        private fun String?.inputToDoubleOrNull(): Double? = this?.replace(',', '.')?.toDoubleOrNull()
    }
}
