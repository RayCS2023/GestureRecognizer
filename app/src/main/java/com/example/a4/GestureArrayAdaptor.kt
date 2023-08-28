package com.example.a4starter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView

class GestureArrayAdaptor(activity: Activity, private val layoutResId: Int, gestureList: List<Gesture>, private val deleteClickListener: ((Gesture?) -> Unit)?): ArrayAdapter<Gesture>(activity, layoutResId, gestureList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val currentItemView = convertView ?: LayoutInflater.from(context).inflate(layoutResId, parent, false)
        val gesture = getItem(position)

        val nameField = currentItemView.findViewById<TextView>(R.id.gesture_name)
        nameField.text = gesture?.name

        val imgView = currentItemView.findViewById<ImageView>(R.id.imageview)
        imgView.setImageBitmap(gesture?.bitMap)

        val deleteBtn = currentItemView.findViewById<ImageView>(R.id.delete)
        deleteClickListener?.also { clickListenr ->
            deleteBtn.setOnClickListener { clickListenr(gesture) }
        } ?: run {
            deleteBtn.visibility = View.INVISIBLE
        }
        return currentItemView
    }
}