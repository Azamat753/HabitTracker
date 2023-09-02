package com.lawlett.habittracker.helper.launge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.lawlett.habittracker.MainActivity
import com.lawlett.habittracker.base.BaseAdapter
import com.lawlett.habittracker.base.BaseBottomSheetDialog
import com.lawlett.habittracker.databinding.LaungeLayoutBinding
import com.lawlett.habittracker.helper.CacheManager
import com.lawlett.habittracker.helper.theme.ThemeModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseLoungeBottomSheetDialog :
    BaseBottomSheetDialog<LaungeLayoutBinding>(LaungeLayoutBinding::inflate) {

  //  private val adapter= LoungeAdapter()

    private lateinit var adapter:LoungesAdapter

    @Inject
    lateinit var cacheManager: CacheManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
     //   adapter.listener = this
        adapter = LoungesAdapter(this::onClick,fillLoungeModel())
        binding.loungeRecycler.adapter = adapter
       // adapter.setData(fillLoungeModel())
    }


    private fun fillLoungeModel(): ArrayList<LoungeModel> {
        val listLounge: ArrayList<LoungeModel> = ArrayList()
        listLounge.add(LoungeModel("Русский"))
        listLounge.add(LoungeModel("English"))
        listLounge.add(LoungeModel("Кыргызский"))
        return listLounge
    }
    private fun onClick(loungeModel: LoungeModel) {
        Log.e("OLOLO", "onClick: ${loungeModel}", )
        val selectedLoungeIndex = adapter.list.indexOf(loungeModel)
        if (selectedLoungeIndex != -1) {
            cacheManager.setLounge(selectedLoungeIndex)
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().overridePendingTransition(
                android.R.anim.fade_in, android.R.anim.fade_out
            )
        }
    }
}
//    override fun onClick(model: LoungeModel, position: Int) {
//        cacheManager.setLounge(position)
//        startActivity(Intent(requireContext(), MainActivity::class.java))
//        requireActivity().overridePendingTransition(
//            android.R.anim.fade_in, android.R.anim.fade_out
//        )
//    }