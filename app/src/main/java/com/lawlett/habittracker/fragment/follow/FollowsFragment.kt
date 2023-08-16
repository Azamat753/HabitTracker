package com.lawlett.habittracker.fragment.follow

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drakeet.multitype.MultiTypeAdapter
import com.google.firebase.Timestamp
import com.lawlett.habittracker.R
import com.lawlett.habittracker.TAG
import com.lawlett.habittracker.adapter.FollowerAdapter
import com.lawlett.habittracker.adapter.NameAdapter
import com.lawlett.habittracker.bottomsheet.FollowDialog
import com.lawlett.habittracker.databinding.FragmentFollowBinding
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.EventCallback
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.models.HabitModel
import com.lawlett.habittracker.showToast
import com.lawlett.habittracker.toGone
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

    @Inject
    lateinit var firebaseHelper: FirebaseHelper

    @Inject
    lateinit var cacheManager: CacheManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMultiAdapter()
        fetchFromFB()
        binding.fab.setOnClickListener {
            FollowDialog(this).show(requireActivity().supportFragmentManager, "")
        }
//        if (!firebaseHelper.isSigned()){
//            requireContext().getDialog(R.layout.follow_dialog).show()
//        }
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
            array.forEach {userName->
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
                            allDays = allDays,
                            date = date,
                            startDate = startDate,
                            endDate = endDate,
                            fbName = userName
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