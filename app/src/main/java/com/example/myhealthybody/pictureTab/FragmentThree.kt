package com.example.myhealthybody.pictureTab

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.model.PictureData
import com.example.myhealthybody.databinding.FragmentThreeBinding
import com.example.myhealthybody.login.LoginActivity
import com.example.myhealthybody.mainView.MyApplication
import com.example.myhealthybody.model.PictureViewModel
import com.google.firebase.firestore.Query
import com.example.myhealthybody.pictureTab.adapter.PictureRecyclerAdapter

class FragmentThree : Fragment() {
    private lateinit var viewModel: PictureViewModel
    private lateinit var mBinding: FragmentThreeBinding
    private lateinit var recyclerAdapter: PictureRecyclerAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentThreeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = mBinding.pictureRecyclerView
        val swipeRefreshLayout = mBinding.swipeRefreshLayout
        if (MyApplication.checkAuth()) {
            getItemList(false)
        } else {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        viewModel = ViewModelProvider(requireActivity())[PictureViewModel::class.java]
        viewModel.pictures.observe(viewLifecycleOwner, Observer { pictureData ->
            recyclerAdapter.addItem(pictureData)
        })
        mBinding.addFab.setOnClickListener {
            startActivity(Intent(requireContext(), AddPictureActivity::class.java))
        }
        swipeRefreshLayout.setOnRefreshListener {
            getItemList(true)
        }
    }

    private val dataUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            getItemList(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(dataUpdateReceiver)
    }

    private fun getItemList(isUpdate: Boolean) {
        MyApplication.db.collection("userImages").orderBy("date", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<PictureData>()
                for (document in result) {
                    val item = document.toObject(PictureData::class.java)
                    item.docId = document.id
                    itemList.add(item)
                }
                if (isUpdate) {
                    updateRecyclerView(itemList)
                } else {
                    makeRecyclerView(itemList)
                }
                mBinding.swipeRefreshLayout.isRefreshing = false
            }.addOnFailureListener { exception ->
                Log.d("kim", "error.. getting document..", exception)
                Toast.makeText(context, "사진 서버 데이터 획득 실패", Toast.LENGTH_SHORT).show()
                mBinding.swipeRefreshLayout.isRefreshing = false
            }
    }

    private fun makeRecyclerView(itemList: MutableList<PictureData>) {
        recyclerAdapter = PictureRecyclerAdapter(itemList)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerAdapter
        }
    }

    private fun updateRecyclerView(itemList: MutableList<PictureData>) {
        recyclerAdapter.updateItems(itemList)
    }

    override fun onResume() {
        super.onResume()
        getItemList(isUpdate = true)
    }
}