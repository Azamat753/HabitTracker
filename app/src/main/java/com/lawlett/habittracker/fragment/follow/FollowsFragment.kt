package com.lawlett.habittracker.fragment.follow

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.drakeet.multitype.MultiTypeAdapter
import com.google.firebase.Timestamp
import com.google.firebase.messaging.FirebaseMessaging
import com.lawlett.habittracker.R
import com.lawlett.habittracker.adapter.FollowerAdapter
import com.lawlett.habittracker.adapter.NameAdapter
import com.lawlett.habittracker.bottomsheet.FollowDialog
import com.lawlett.habittracker.databinding.DialogDeleteBinding
import com.lawlett.habittracker.databinding.FragmentFollowBinding
import com.lawlett.habittracker.ext.*
import com.lawlett.habittracker.helper.*
import com.lawlett.habittracker.helper.Key.KEY_SEARCH_FOLLOWS
import com.takusemba.spotlight.Target
import com.lawlett.habittracker.models.HabitModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowsFragment : Fragment(R.layout.fragment_follow), EventCallback,TokenCallback {
    private val binding: FragmentFollowBinding by viewBinding()
    lateinit var multiTypeAdapter: MultiTypeAdapter
    lateinit var items: MutableList<Any>

    @Inject
    lateinit var firebaseHelper: FirebaseHelper

    lateinit var helper: GoogleSignInHelper

    @Inject
    lateinit var cacheManager: CacheManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helper = GoogleSignInHelper(this, tokenCallback = this)
        initMultiAdapter()
        spotlight()
        getFromFb()
        initClickers()
        setupUI()
        checkOnEmpty()
    }

    private fun getFromFb() {
        if (firebaseHelper.isSigned()) {
            fetchFromFB()
        }
    }

    private fun spotlight() {
        if (!cacheManager.isPass()) {
            if (!cacheManager.isUserSeen(KEY_SEARCH_FOLLOWS)) {
                searchlight()
            }
        }
    }

    private fun initClickers() {
        with(binding) {
            fab.setOnClickListener {
                FollowDialog(this@FollowsFragment).show(
                    requireActivity().supportFragmentManager,
                    ""
                )
            }
            signBtn.setOnClickListener {
                helper.signInGoogle()
            }
        }
    }

    private fun setupUI() {
        with(binding) {
            if (!firebaseHelper.isSigned()) {
                recyclerFriends.toGone()
                signLayout.toVisible()
            } else {
                progressBar.toGone()
                recyclerFriends.toVisible()
                signLayout.toGone()
            }
        }
    }

    private fun searchlight() {
        val targets = ArrayList<Target>()
        val root = FrameLayout(requireContext())
        val first = layoutInflater.inflate(R.layout.layout_target_follows, root)

        Handler().postDelayed({
            cacheManager.saveUserSeen(KEY_SEARCH_FOLLOWS)
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

    private fun checkOnEmpty() {
        with(binding) {
            if (firebaseHelper.isSigned()) {
                if (multiTypeAdapter.items.isEmpty()) {
                    recyclerFriends.toGone()
                    signLayout.toGone()
                    emptyLayout.toVisible()
                } else {
                    recyclerFriends.toVisible()
                    emptyLayout.toGone()
                }
            }
        }
    }

    private fun openDetail(habitModel: HabitModel) {
        val bundle = Bundle()
        bundle.putParcelable("key", habitModel)
        bundle.putBoolean("isFollow", true)
        findNavController().navigate(R.id.habitDetailFragment, bundle)
    }

    private fun initMultiAdapter() {
        multiTypeAdapter = MultiTypeAdapter()
        multiTypeAdapter.register(NameAdapter(click = ({ name -> removeFollower(name) })))
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

    @SuppressLint("SetTextI18n")
    private fun removeFollower(name: String) {
        val dialog = requireContext().createDialog(DialogDeleteBinding::inflate)
        dialog.first.txtDescription.text = "Подписка на ${name.makeUserName()} будет удалена"
        dialog.first.btnYes.setOnClickListener {
            val array = cacheManager.getFollowers()!!
            array.remove(name)
            cacheManager.saveFollowers(array)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(name.makeTopic())
            reInitAdapter()
            dialog.second.dismiss()
        }
        dialog.first.btnNo.setOnClickListener { dialog.second.dismiss() }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchFromFB() {
        binding.progressBar.toVisible()
        items = ArrayList()
        var names = 0
        cacheManager.getFollowers()?.distinct()?.let { array ->
            array.forEach { userName ->
                firebaseHelper.db.collection(userName!!).get().addOnCompleteListener { result ->
                    if (result.result.size() != 0) {
                        items.add(Pair(userName.makeUserName(), userName))
                        ++names
                    } else {
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
                            fbName = userName,
                            attempts = attempts
                        )
                        items.add(model)
                        if (items.size == result.result.documents.size + names) {
                            multiTypeAdapter.items = items
                            multiTypeAdapter.notifyDataSetChanged()
                            checkOnEmpty()
                        }
                    }
                    binding.progressBar.toGone()
                }.addOnFailureListener {
                    binding.progressBar.toGone()
                    Log.e(TAG, "Error read document", it)
                }
            }
        }.run {
            binding.progressBar.toGone()
        }
    }

    override fun call() {
        reInitAdapter()
    }

    private fun reInitAdapter() {
        initMultiAdapter()
        fetchFromFB()
        checkOnEmpty()
    }

    override fun newToken(authCode: String) {
        reInitAdapter()
    }
}