package com.example.a4starter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class HomeFragment : Fragment(), View.OnClickListener {
    private var mViewModel: SharedViewModel? = null
    private var okButton: Button? = null
    private var clearButton: Button? = null
    private var canvasView: CanvasView? = null
    private lateinit var listView: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        listView = root.findViewById(R.id.listView)
        okButton = root.findViewById(R.id.ok_btn) as? Button
        okButton?.setOnClickListener(this)
        clearButton = root.findViewById(R.id.clear_btn) as? Button
        clearButton?.setOnClickListener(this)
        canvasView = root.findViewById(R.id.canvas_view) as? CanvasView

        return root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ok_btn -> {
                if (canvasView?.pointListSize == 0) {
                    Toast.makeText(activity, R.string.no_gesture_on_canvas, Toast.LENGTH_SHORT).show();
                } else {
                    canvasView?.let {
                        val matches = canvasView?.gesturePath?.let { it1 -> mViewModel?.findBestMatches(it1) }
                        if (matches != null && matches.isNotEmpty()) {
                            val arrayAdaptor = activity?.let {
                                GestureArrayAdaptor(it, R.layout.gesture_listview_item, matches,null)
                            }
                            listView.adapter = arrayAdaptor
                        }
                    }
//                    val list = canvasView?.gesturePath?.let { mViewModel?.findBestMatchesTest(it) }
//                    if (list != null) {
//                        canvasView?.drawList(list)
//                    }
                }
            }
            R.id.clear_btn -> {
                canvasView?.resetCanvas()
                listView.adapter = null
            }
            else -> {}
        }
    }

    private fun populateListViewTest() {
        mViewModel?.gestureListSize?.let { size ->
            if (size >= 3) {
                val list = ArrayList<Gesture>()
                mViewModel?.gestureList?.value?.get(0)?.let { list.add(it) }
                mViewModel?.gestureList?.value?.get(1)?.let { list.add(it) }
                mViewModel?.gestureList?.value?.get(2)?.let { list.add(it) }
                val arrayAdaptor = activity?.let {
                        GestureArrayAdaptor(it, R.layout.gesture_listview_item, list,null)
                }
                listView.adapter = arrayAdaptor
            }
        }
    }
}