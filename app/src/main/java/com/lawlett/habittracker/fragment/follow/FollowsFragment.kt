package com.lawlett.habittracker.fragment.follow

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.lawlett.habittracker.models.HabitModel
import com.takusemba.spotlight.Target
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowsFragment : Fragment(R.layout.fragment_follow), EventCallback, TokenCallback,
    SpotlightEnd {
    private val binding: FragmentFollowBinding by viewBinding()
    lateinit var multiTypeAdapter: MultiTypeAdapter
    lateinit var items: MutableList<Any>


    @Inject
    lateinit var firebaseHelper: FirebaseHelper

    lateinit var helper: GoogleSignInHelper

    @Inject
    lateinit var cacheManager: CacheManager
    private val requestCodeCameraPermission = 1001

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helper = GoogleSignInHelper(this, tokenCallback = this) { reInitAdapter() }
        initMultiAdapter()
        spotlight()
        getFromFb()
        initClickers()
        setupUI()
        checkOnEmpty()
    }

    private fun getFromFb() {
        if (firebaseHelper.isSigned()) {
            if (view != null) {
                fetchFromFB()
            }
        }
    }

    private fun spotlight() {
        if (!cacheManager.isPass()) {
            if (!cacheManager.isUserSeen(KEY_SEARCH_FOLLOWS)) {
                showSpotlight()
            }
        }
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showScanner()
            } else {
                showToast("Permission Denied")
            }
        }
    }

    private fun initClickers() {
        with(binding) {
            cameraFab.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(), android.Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    askForCameraPermission()
                } else {
                    showScanner()
                }
            }
            penFab.setOnClickListener {
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

    private fun showScanner() {
        ScannerDialog(this@FollowsFragment).show(
            requireActivity().supportFragmentManager,
            ""
        )
    }

    private fun setupUI() {
        with(binding) {
            if (!firebaseHelper.isSigned()) {
                recyclerFriends.toGone()
                signLayout.toVisible()
                checkOnEmpty()
            } else {
                recyclerFriends.toVisible()
                signLayout.toGone()
                binding.progressBar.toGone()
                checkOnEmpty()
            }
        }
    }

    private fun showSpotlight() {
        val targets = ArrayList<Target>()
        val root = FrameLayout(requireContext())
        val first = layoutInflater.inflate(R.layout.layout_target_follows, root)
        with(binding) {
            isClickableScreen(false, fab, signBtn)
        }
        Handler().postDelayed({
            cacheManager.saveUserSeen(KEY_SEARCH_FOLLOWS)
            val vi = setSpotLightTarget(
                binding.mainFollow, first, getString(R.string.follows_display)
            )

            val firstStop = setSpotLightTarget(
                binding.fabInvisible, first, getString(R.string.follows_fab)
            )

            targets.add(vi)
            targets.add(firstStop)
            setSpotLightBuilder(requireActivity(), targets, first, this)
        }, 100)
    }

    private fun checkOnEmpty() {
        if (view != null) {
            with(binding) {
                if (firebaseHelper.isSigned()) {
                    if (multiTypeAdapter.items.isEmpty()) {
                        recyclerFriends.toGone()
                        signLayout.toGone()
                        emptyLayout.toVisible()
                        if (cacheManager.getFollowers().isNullOrEmpty()) {
                            progressBar.toGone()
                        }
                    } else {
                        recyclerFriends.toVisible()
                        emptyLayout.toGone()
                    }
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

    @SuppressLint("SetTextI18n", "StringFormatInvalid")
    private fun removeFollower(name: String) {
        val dialog = requireContext().createDialog(DialogDeleteBinding::inflate)
        dialog.first.txtDescription.text = getString(R.string.follow_delete, name.makeUserName())
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
        if (activity != null && isAdded) {

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
                            val title = document.data["title"] as String?
                            val attempts = (document.data["attempts"] as Long).toInt()
                            val icon = document.data["icon"] as String?
                            val record = document.data["record"] as String?
                            val currentDay = (document.data["currentDay"] as Long).toInt()
                            val allDays = (document.data["allDays"] as Long).toInt()
                            val startDate = (document.data["startDate"] as Timestamp?)?.toDate()
                            val history = document.data["history"] as String?
                            val model = HabitModel(
                                title = title,
                                icon = icon,
                                currentDay = currentDay,
                                allDays = allDays,
                                startDate = startDate,
                                fbName = userName,
                                attempts = attempts,
                                record = record,
                                history = history
                            )
                            items.add(model)
                            if (items.size == result.result.documents.size + names) {
                                multiTypeAdapter.items = items
                                multiTypeAdapter.notifyDataSetChanged()
                                checkOnEmpty()
                            }
                        }
                        if (isAdded) {
                            binding.progressBar.toGone()
                        }
                    }.addOnFailureListener {
                        if (isAdded) {
                            binding.progressBar.toGone()
                        }
                        Log.e(TAG, "Error read document", it)
                    }
                }
            }
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
        successSign()
    }

    override fun signSuccess() {
        successSign()
    }

    private fun successSign() {
        showToast(getString(R.string.success))
        if (isAdded) {
            binding.progressBar.toGone()
        }
        reInitAdapter()
    }

    override fun end() {
        isClickableScreen(true, binding.fab, binding.signBtn)
    }
}