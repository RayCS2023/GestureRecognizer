package com.example.a4starter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider


class LibraryFragment : Fragment() {

    private var mViewModel: SharedViewModel? = null
    private lateinit var listView: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root: View = inflater.inflate(R.layout.fragment_library, container, false)
        listView = root.findViewById(R.id.listView)

        mViewModel?.gestureList?.observe(viewLifecycleOwner, { list ->
            activity?.let { activity ->
                val arrayAdaptor = GestureArrayAdaptor(activity, R.layout.gesture_listview_item, list) { gesture: Gesture? ->
                    deleteOnClick(gesture)
                }
                listView.adapter = arrayAdaptor
            }
        })

        return root
    }

    private fun deleteOnClick(gesture: Gesture?){
        mViewModel?.deleteGesture(gesture)
    }
}