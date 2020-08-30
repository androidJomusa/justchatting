package com.example.justchatting.ui.friend.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.justchatting.R

class AddFriendFragment(text: String) : Fragment() {

    private var mText = text
    private var addFriendFragmentListener: OnAddFriendFragmentButtonListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_fragment, container, false)
        val detailTextView = view.findViewById<TextView>(R.id.dialog_textview_detail)
        val addButton = view.findViewById<Button>(R.id.btn_add)
        val cancelButton = view.findViewById<Button>(R.id.btn_cancel)
        val inputEditTextView = view.findViewById<EditText>(R.id.dialog_edittext_input)
        detailTextView.text = mText

        addButton.setOnClickListener { view ->
            Log.d("AddFriendFragment", "onclick add")
            addFriendFragmentListener?.messageFromAddFriendFragment(
                true,
                inputEditTextView.text.toString()
            )
        }
        cancelButton.setOnClickListener { view ->
            Log.d("AddFriendFragment", "onclick cancel")
            addFriendFragmentListener?.messageFromAddFriendFragment(false, "")
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is OnAddFriendFragmentButtonListener) {
            addFriendFragmentListener = parentFragment as OnAddFriendFragmentButtonListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        addFriendFragmentListener = null
    }

    interface OnAddFriendFragmentButtonListener {
        fun messageFromAddFriendFragment(isAdd: Boolean, input: String)
    }

}