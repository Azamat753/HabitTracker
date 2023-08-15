package com.lawlett.habittracker.fragment.friends

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drakeet.multitype.MultiTypeAdapter
import com.lawlett.habittracker.R
import com.lawlett.habittracker.TAG
import com.lawlett.habittracker.adapter.FollowerAdapter
import com.lawlett.habittracker.adapter.NameAdapter
import com.lawlett.habittracker.databinding.FragmentFriendsBinding
import com.lawlett.habittracker.helper.FirebaseHelper
import com.lawlett.habittracker.models.HabitModel

class FriendsFragment : Fragment(R.layout.fragment_friends) {
    private val binding: FragmentFriendsBinding by viewBinding()
    lateinit var multiTypeAdapter: MultiTypeAdapter
    var ibrahimName = "Ibra Kasymov:8EEgmDq90vQzrgQI3RGtlCO5pQ12"
    var ibrahimName2 = "Red. Fox._23:k55n745emFP9brVsXs5Wzu9hpJC2"
    var arrayNames = arrayListOf(ibrahimName,ibrahimName2)
    internal lateinit var items: MutableList<Any>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMultiAdapter()
    }

    private fun initMultiAdapter() {
        multiTypeAdapter = MultiTypeAdapter()
        multiTypeAdapter.register(NameAdapter())
        multiTypeAdapter.register(FollowerAdapter())
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
        items = ArrayList()

        var fb = FirebaseHelper()
        arrayNames.forEach {

        fb.db.collection(it).get().addOnCompleteListener { result ->
            items.add(it.replaceAfter(":", ""))
            for (document in result.result) {
                var title = document.data.get("title") as String
                var icon = document.data.get("icon") as String
                var currentDay = (document.data.get("currentDay") as Long).toInt()
                var allDays = document.data.get("allDays") as String
                var model = HabitModel(
                    title = title,
                    icon = icon,
                    currentDay = currentDay,
                    allDays = allDays
                )
                items.add(model)
                if (items.size ==result.result.documents.size ) {
                    multiTypeAdapter.items = items
                    multiTypeAdapter.notifyDataSetChanged()
                }
            }
        }.addOnFailureListener {
            Log.e(TAG, "Error read document", it)
        }
        }

//        items.add("Patrick")
//        items.add(HabitModel(title = "1", icon = "1", allDays = "24", currentDay = 2))
//        items.add(HabitModel(title = "2", icon = "2", allDays = "12"))
//        items.add("Sponge")
//        items.add(HabitModel(title = "3", icon = "3", allDays = "24", currentDay = 2))
//        items.add(HabitModel(title = "4", icon = "4", allDays = "12"))
//        items.add(HabitModel(title = "5", icon = "5", allDays = "12"))

    }
}