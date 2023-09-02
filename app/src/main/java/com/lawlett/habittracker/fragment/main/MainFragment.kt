package com.lawlett.habittracker.fragment.main


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.Timestamp
import com.lawlett.habittracker.MainActivity
import com.lawlett.habittracker.R
import com.lawlett.habittracker.adapter.HabitAdapter
import com.lawlett.habittracker.api.SignApi
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.bottomsheet.CreateHabitDialog
import com.lawlett.habittracker.databinding.DialogDeleteBinding
import com.lawlett.habittracker.databinding.FragmentMainBinding
import com.lawlett.habittracker.ext.*
import com.lawlett.habittracker.fragment.main.viewModel.MainViewModel
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.EventCallback
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.bottomsheet.ChooseLanguageBottomSheetDialog
import com.lawlett.habittracker.models.HabitModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main),
    BaseAdapter.IBaseAdapterClickListener<HabitModel>,
    BaseAdapter.IBaseAdapterLongClickListenerWithModel<HabitModel> ,EventCallback{

    private val binding: FragmentMainBinding by viewBinding()
    private val viewModel: MainViewModel by viewModels()
    private val adapter = HabitAdapter()

    @Inject
    lateinit var firebaseHelper: FirebaseHelper

    @Inject
    lateinit var signApi: SignApi

    @Inject
    lateinit var cacheManager: CacheManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initClickers()
//        firstLaunchDialog()
        viewModel.getHabits()
        observe()
        viewModel.viewModelScope.launch {
            delay(100)
            checkOnEmpty()
        }
    }

    private fun firstLaunchDialog() {
        if (!cacheManager.isLangeSeen()) {
            languageChanged()
        } else if (!cacheManager.isUserSeenDialog()) {
            dialogTest()
        }
    }

    private fun dialogTest() {
        cacheManager.saveUserSeenDialog()
        val dialog = requireContext().createDialog(DialogDeleteBinding::inflate)
        dialog.first.txtTitle.text = "Желаете ли вы пройти обучение?"
        dialog.first.txtDescription.toGone()
        dialog.first.btnYes.setOnClickListener {
            searchlight()
            dialog.second.dismiss()
        }
        dialog.first.btnNo.setOnClickListener {
            cacheManager.saveInstruction(true)
            dialog.second.dismiss()
        }
    }

    private fun languageChanged() {
        cacheManager.saveLangeSeen()
        val bottomDialog = ChooseLanguageBottomSheetDialog()
        bottomDialog.show(requireActivity().supportFragmentManager, "TAG")
       // requireActivity().changeLanguage()
    }

    private fun searchlight() {
        val targets = ArrayList<com.takusemba.spotlight.Target>()
        val root = FrameLayout(requireContext())
        val first = layoutInflater.inflate(R.layout.layout_target_main, root)

        Handler().postDelayed({
            val views = setSpotLightTarget(
                binding.mainDisplay,
                first,
                getString(R.string.main_habit_display)
            )

            val firstSpot = setSpotLightTarget(
                binding.mainDisplay,
                first,
                getString(R.string.main_habit_list)
            )

            val secondSpot = setSpotLightTarget(
                binding.fab,
                first,
                getString(R.string.main_habit_fab)
            )

            targets.add(views)
            targets.add(firstSpot)
            targets.add(secondSpot)
            setSpotLightBuilder(requireActivity(), targets, first)
        }, 100)
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

    private fun getHabitsFromFB() {
        binding.progressBar.toVisible()
        firebaseHelper.db.collection(firebaseHelper.getUserName()).get()
            .addOnCompleteListener { result ->
                val listHabit = arrayListOf<HabitModel>()
                if (result.result.documents.size == 0) {
                    binding.progressBar.toGone()
                }
                for (document in result.result) {
                    val title = document.data["title"] as String
                    val attempts = (document.data["attempts"] as Long).toInt()
                    val icon = document.data["icon"] as String
                    val currentDay = (document.data["currentDay"] as Long).toInt()
                    val allDays = (document.data["allDays"] as Long).toInt()
                    val startDate = (document.data["startDate"] as Timestamp?)?.toDate()
                    val model = HabitModel(
                        title = title,
                        icon = icon,
                        currentDay = currentDay,
                        allDays = allDays,
                        startDate = startDate,
                        fbName = firebaseHelper.getUserName(),
                        attempts = attempts
                    )
                    listHabit.add(model)
                    if (listHabit.size == result.result.documents.size ) {
                        adapter.setData(listHabit)
                        checkOnEmpty()
                        adapter.data.forEach {
                            viewModel.insert(it)
                        }
                        binding.progressBar.toGone()
                    }else{
                        binding.progressBar.toGone()
                    }
                }
            }.addOnFailureListener {
                binding.progressBar.toGone()
                Log.e(TAG, "Error read document", it)
            }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habitFlow.asSharedFlow().collect() { habits ->
                    adapter.setData(habits)
                }
            }
        }
    }

    private fun checkOnEmpty() {
        with(binding) {
            if (adapter.data.isEmpty()) {
                habitRecycler.toGone()
                emptyLayout.toVisible()
                if (firebaseHelper.isSigned()) {
                    getHabitsFromFB()
                }
            } else {
                binding.progressBar.toGone()
                habitRecycler.toVisible()
                emptyLayout.toGone()
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
            firebaseHelper.delete(model)
            viewModel.viewModelScope.launch {
                delay(100)
                adapter.notifyItemRemoved(position)
            }
            dialog.second.dismiss()
        }
        dialog.first.btnNo.setOnClickListener { dialog.second.dismiss() }
    }

    override fun call() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().overridePendingTransition(
            android.R.anim.fade_in, android.R.anim.fade_out
        )
    }

}