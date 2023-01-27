package com.propswift.Activities


import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.airbnb.epoxy.*
import com.propswift.R
import com.propswift.Shared.*
import com.propswift.databinding.*
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.coroutines.*


class ExpensesFragment : Fragment() {

    private lateinit var viewy: View
    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!
    private var filter = "general"
    private var date = "paid"

    @RequiresApi(Build.VERSION_CODES.O) override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentExpensesBinding.inflate(layoutInflater, container, false)
        viewy = binding.root
        initiate_Views()
        return viewy
    }

    private fun initiate_Views() {

        val powerMenu: PowerMenu = PowerMenu.Builder(requireContext()).addItem(PowerMenuItem("General", false)) // add an item.
            .addItem(PowerMenuItem("Incurred", false)) // aad an item list.
            .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
            .setMenuRadius(10f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.black)).setTextGravity(Gravity.CENTER).setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
            .setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE).setSelectedMenuColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            .setAutoDismiss(true)
            .setOnMenuItemClickListener { position, item ->
                filter = item?.title.toString().lowercase()
                activity?.makeLongToast("You chose ${filter}")
            }.build()


        CoroutineScope(Dispatchers.IO).launch() {

            val allGeneralExpenses = async { activity?.myViewModel(requireActivity())?.getExpensesGeneralAll() }
            val paidRent = allGeneralExpenses.await()

            withContext(Dispatchers.Main) {

                if (paidRent!!.details!!.isEmpty()) {
                    return@withContext
                }

                binding.epoxyRecyclerview.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
                    override fun buildModels(controller: EpoxyController) {
                        controller.apply {
                            paidRent.details!!.forEachIndexed { index, item ->
                                ExpensesGeneralModalClass_(activity, item).id(index).addTo(this@apply)
                            }
                        }
                    }

                })
            }

        }


        binding.generalIncurredBtn.setOnClickListener {
            powerMenu.showAsDropDown(binding.generalIncurredBtn)
        }

        binding.monthPickerButton.setOnClickListener {

        }



    }

}


@SuppressLint("NonConstantResourceId") @EpoxyModelClass(layout = R.layout.receipt_expenses) abstract class ExpensesGeneralModalClass(var activity: FragmentActivity?, var item: ExpenseDetail) :
        EpoxyModelWithHolder<ExpensesGeneralModalClass.ViewHolder>() {

    private lateinit var binding: ReceiptExpensesBinding
    override fun bind(holder: ViewHolder) = Unit

    inner class ViewHolder : EpoxyHolder() {
        @SuppressLint("SetTextI18n") override fun bindView(itemView: View) {
            binding = ReceiptExpensesBinding.bind(itemView)

            binding.expenseDateTv.setText(item.date_incurred)
            binding.expensereceiptNoTv.setText(item.id)
            binding.expenseAmountTv.setText("Expenses Incurred   :   KES 50,000")
            binding.propertyName.setText(item.property.name)

            itemView.setOnClickListener {
                activity?.showAlertDialog(item.toString())
            }

        }
    }

}
