package com.lawlett.habittracker.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lawlett.habittracker.R
import com.lawlett.habittracker.adapter.HabitAdapter
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.bottomsheet.CreateHabitDialog
import com.lawlett.habittracker.databinding.FragmentMainBinding
import com.lawlett.habittracker.models.HabitModel
import com.lawlett.habittracker.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main),
    BaseAdapter.IBaseAdapterClickListener<HabitModel>,
    BaseAdapter.IBaseAdapterLongClickListenerWithModel<HabitModel> {

    private val binding: FragmentMainBinding by viewBinding()
    private val viewModel: MainViewModel by viewModels()
    private val adapter = HabitAdapter()
    val list = arrayListOf<HabitModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getHabits()
        initClickers()
        initAdapter()
        observe()
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

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.habitFlow.asSharedFlow().collect(){
                    adapter.setData(it)

                }
            }
        }
    }

    override fun onClick(model: HabitModel, position: Int) {
        val bundle = Bundle()
        bundle.putParcelable("key",model)
        findNavController().navigate(R.id.habitDetailFragment,)
    }

    override fun onLongClick(model: HabitModel, itemView: View, position: Int) {
        viewModel.delete(model)
        showToast(model.title.toString())
    }
}