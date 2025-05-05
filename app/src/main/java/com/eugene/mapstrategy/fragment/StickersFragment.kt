package com.eugene.mapstrategy.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eugene.mapstrategy.R
import com.eugene.mapstrategy.adapter.StickersAdapter
import com.eugene.mapstrategy.bean.StickersBean
import com.eugene.mapstrategy.utils.TUtil
import com.google.gson.Gson

class StickersFragment(
    private val block: (img: String) -> Unit
): BaseFragment() {

    private var recyclerView: RecyclerView? = null
    private var stickers = StickersBean()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_stickers, container, false)
        recyclerView = layout.findViewById(R.id.recycler_view)

        parseData()
        initRecyclerView()
        return layout
    }

    private fun parseData() {
        val gson = Gson()
        val stickersData = TUtil.jsonFileOperate(context, "Stickers.json")
        stickers = gson.fromJson(stickersData, StickersBean::class.java)
    }

    private fun initRecyclerView() {
        recyclerView?.layoutManager = GridLayoutManager(context, 4)
        recyclerView?.adapter = StickersAdapter(context, stickers.emoji, block)
    }


}