package com.lawlett.habittracker.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.Timestamp
import com.lawlett.habittracker.*
import com.lawlett.habittracker.adapter.HabitAdapter
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.bottomsheet.CreateHabitDialog
import com.lawlett.habittracker.databinding.DialogDeleteBinding
import com.lawlett.habittracker.databinding.FollowDialogBinding
import com.lawlett.habittracker.databinding.FragmentMainBinding
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.models.HabitModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main),
    BaseAdapter.IBaseAdapterClickListener<HabitModel>,
    BaseAdapter.IBaseAdapterLongClickListenerWithModel<HabitModel> {
    private val binding: FragmentMainBinding by viewBinding()
    private val viewModel: MainViewModel by viewModels()
    private val adapter = HabitAdapter()
    val list = arrayListOf<HabitModel>()
    var container: ViewGroup? = null

    @Inject
    lateinit var firebaseHelper: FirebaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickers()
        initAdapter()
        observe()
        viewModel.getHabits()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.container = container
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getHabits()
    }

    private fun initClickers() {
        binding.fab.setOnClickListener {
            CreateHabitDialog().show(requireActivity().supportFragmentManager, "")
        }
    }

    private fun initAdapter() {
        adapter.listener = this
        adapter.longListener = this
        binding.habitRecycler.adapter = adapter
    }

    fun getFBDataCount(): Int? {
        var isComplete = false
        var listSize = 0
        firebaseHelper.db.collection(firebaseHelper.getUserName()).get()
            .addOnCompleteListener { result ->
                listSize = result.result.size()
                isComplete = true
            }.addOnFailureListener {
                Log.e(TAG, "Error read document", it)
            }
        return if (isComplete){
            listSize
        }else{
            null
        }
    }

    private fun getHabitsFromFB() {
        binding.progressBar.toVisible()
        firebaseHelper.db.collection(firebaseHelper.getUserName()).get()
            .addOnCompleteListener { result ->
                var listHabit = arrayListOf<HabitModel>()
                for (document in result.result) {
                    val title = document.data["title"] as String
                    val icon = document.data["icon"] as String
                    val currentDay = (document.data["currentDay"] as Long).toInt()
                    val allDays = document.data["allDays"] as String
                    val date = document.data["date"] as String?
                    val startDate = (document.data["startDate"] as Timestamp?)?.toDate()
                    val endDate = (document.data["endDate"] as Timestamp?)?.toDate()
                    val model = HabitModel(
                        title = title,
                        icon = icon,
                        currentDay = currentDay,
                        allDays = allDays,
                        date = date,
                        startDate = startDate,
                        endDate = endDate,
                    )
                    listHabit.add(model)
                    if (listHabit.size == result.result.documents.size) {
                        listHabit.forEach {
                            viewModel.insert(it)
                        }
                        binding.progressBar.toGone()
                    }
                }
            }.addOnFailureListener {
                Log.e(TAG, "Error read document", it)
            }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habitFlow.asSharedFlow().collect() { habits->
                    getFBDataCount()?.let {
                        if (it != habits.size){
                            getHabitsFromFB()
                        }
                    }
                    adapter.setData(habits)
                }
            }
        }
    }

    override fun onClick(model: HabitModel, position: Int) {
        val bundle = Bundle()
        bundle.putParcelable("key", model)
        findNavController().navigate(R.id.habitDetailFragment, bundle)
    }

    override fun onLongClick(model: HabitModel, itemView: View, position: Int) {
        val dialog = requireContext().createDialog(DialogDeleteBinding::inflate)
        dialog.first.btnYes.setOnClickListener {
            viewModel.delete(model)
            adapter.notifyItemRemoved(position)
            dialog.second.dismiss()
        }
        dialog.first.btnNo.setOnClickListener { dialog.second.dismiss() }
    }

}