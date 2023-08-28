package com.example.a4starter

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider


class AdditionFragment : Fragment(), View.OnClickListener {

    private var mViewModel: SharedViewModel? = null
    private var addButton: Button? = null
    private var clearButton: Button? = null
    private var canvasView: CanvasView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root: View = inflater.inflate(R.layout.fragment_addition, container, false)

        addButton = root.findViewById(R.id.add_btn) as? Button
        addButton?.setOnClickListener(this)
        clearButton = root.findViewById(R.id.clear_btn) as? Button
        clearButton?.setOnClickListener(this)
        canvasView = root.findViewById(R.id.canvas_view) as? CanvasView

        return root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_btn -> {
                if (canvasView?.pointListSize == 0) {
                    Toast.makeText(activity, R.string.no_gesture_on_canvas, Toast.LENGTH_SHORT).show();
                } else {
                    promptSaveGestureDialogue()
                }
            }
            R.id.clear_btn -> {
                canvasView?.resetCanvas()
            }
            else -> {}
        }
    }

    private fun promptSaveGestureDialogue() {
        if (null != activity) {
            val builder = AlertDialog.Builder(activity)
            val nameField = EditText(activity)
            nameField.hint = getString(R.string.add_gesture_dialogue_hint)
            builder.setTitle(R.string.add_gesture)
            builder.setView(nameField)

            builder.setPositiveButton(R.string.ok){ dialog,which->
                val name = nameField.text.toString()
                val badString = null == name || name.trim() == ""
                if (badString){
                    Toast.makeText(activity, R.string.invalid_name, Toast.LENGTH_SHORT).show();
                } else {
                    canvasView?.saveGesture(name, mViewModel)
                }
            }
            builder.setNegativeButton(R.string.cancel){_,_ -> null}

            val dialog = builder.create()
            dialog.show()
        }

    }

}
