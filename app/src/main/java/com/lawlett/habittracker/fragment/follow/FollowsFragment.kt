package com.lawlett.habittracker.fragment.follow

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drakeet.multitype.MultiTypeAdapter
import com.google.firebase.Timestamp
import com.lawlett.habittracker.R
import com.lawlett.habittracker.ext.TAG
import com.lawlett.habittracker.adapter.FollowerAdapter
import com.lawlett.habittracker.adapter.NameAdapter
import com.lawlett.habittracker.bottomsheet.FollowDialog
import com.lawlett.habittracker.databinding.FragmentFollowBinding
import com.lawlett.habittracker.ext.setSpotLightBuilder
import com.lawlett.habittracker.ext.setSpotLightTarget
import com.takusemba.spotlight.Target
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.EventCallback
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.models.HabitModel
import com.lawlett.habittracker.ext.toGone
import com.lawlett.habittracker.fragment.follow.viewModel.FollowsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowsFragment : Fragment(R.layout.fragment_follow), EventCallback {
    private val binding: FragmentFollowBinding by viewBinding()
    lateinit var multiTypeAdapter: MultiTypeAdapter
    var ibrahimName = "Ibra Kasymov:8EEgmDq90vQzrgQI3RGtlCO5pQ12"
    var ibrahimName2 = "Red. Fox._23:k55n745emFP9brVsXs5Wzu9hpJC2"
    var aza = "Azamat:nPqDXFOUCghapEMR08uxlX0xf3h1"
    var arrayNames = arrayListOf(ibrahimName, ibrahimName2, aza)
    lateinit var items: MutableList<Any>
    private val viewModel: FollowsViewModel by viewModels()

    @Inject
    lateinit var firebaseHelper: FirebaseHelper

    @Inject
    lateinit var cacheManager: CacheManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMultiAdapter()
        if ( !cacheManager.isPass()) {
            if (!cacheManager.isUserSeen()) {
                searchlight()
            }
        }

        fetchFromFB()
        binding.fab.setOnClickListener {
            FollowDialog(this).show(requireActivity().supportFragmentManager, "")
        }
//        if (!firebaseHelper.isSigned()){
//            requireContext().getDialog(R.layout.follow_dialog).show()
//        }
    }

    private fun searchlight() {
        val targets = ArrayList<Target>()
        val root = FrameLayout(requireContext())
        val first = layoutInflater.inflate(R.layout.layout_target, root)
        val view = View(requireContext())

        Handler().postDelayed({
            cacheManager.saveUserSeen()
            val vi = setSpotLightTarget(
                binding.mainFollow, first, getString(R.string.follows_display)
            )

            val firstStop = setSpotLightTarget(
                binding.fab, first, getString(R.string.follows_fab)
            )

            targets.add(vi)
            targets.add(firstStop)
            setSpotLightBuilder(requireActivity(), targets, first)
        }, 100)
    }

    private fun openDetail(habitModel: HabitModel) {
        val bundle = Bundle()
        bundle.putParcelable("key", habitModel)
        bundle.putBoolean("isFollow", true)
        findNavController().navigate(R.id.habitDetailFragment, bundle)
    }

    private fun initMultiAdapter() {
        multiTypeAdapter = MultiTypeAdapter()
        multiTypeAdapter.register(NameAdapter())
        multiTypeAdapter.register(FollowerAdapter(click = ({ model -> openDetail(model) })))
        val layoutManager = GridLayoutManager(requireContext(), 2)
        val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = items[position]
                return if (item is HabitModel) 1 else 2
            }
        }
        layoutManager.spanSizeLookup = spanSizeLookup
        binding.recyclerFriends.layoutManager = layoutManager
        binding.recyclerFriends.adapter = multiTypeAdapter
    }

    private fun fetchFromFB() {
        items = ArrayList()
        cacheManager.getFollowers()?.distinct()?.let { array ->
            array.forEach { userName ->
                firebaseHelper.db.collection(userName!!).get().addOnCompleteListener { result ->
                    if (result.result.size() != 0) {
                        items.add(userName.replaceAfter(":", ""))
                    }
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
                            allDays = allDays.toInt(),
                            startDate = startDate,
                            endDate = endDate,
                            fbName = userName,
                        )
                        items.add(model)
                        if (items.size == result.result.documents.size) {
                            binding.progressBar.toGone()
                            multiTypeAdapter.items = items
                            multiTypeAdapter.notifyDataSetChanged()
                        }
                    }
                }.addOnFailureListener {
                    Log.e(TAG, "Error read document", it)
                }
            }
        }
    }

    override fun call() {
        initMultiAdapter()
        fetchFromFB()
    }
}