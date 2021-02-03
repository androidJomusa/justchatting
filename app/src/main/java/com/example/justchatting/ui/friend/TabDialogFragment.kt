package com.example.justchatting.ui.friend

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.justchatting.R
import com.example.justchatting.ui.friend.AddFriendDialogFragment
import com.google.android.material.tabs.TabLayout

class TabDialogFragment: DialogFragment(){

    lateinit var tabLayout : TabLayout
    lateinit var inputIdDialogFragment : AddFriendDialogFragment
    lateinit var inputPhoneDialogFragment : AddFriendDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_add_friend_layout, null)
        tabLayout = view.findViewById(R.id.dialog_add_friend_tab)
        tabLayout.addTab(tabLayout.newTab().setText("연락처로 추가"))
        tabLayout.addTab(tabLayout.newTab().setText("ID로 추가"))

        inputPhoneDialogFragment =
            AddFriendDialogFragment(
                1,
                "친구로 추가하기 위해 전화번호를 입력하세요."
            )
        inputIdDialogFragment =
            AddFriendDialogFragment(
                2,
                "친구로 추가하기 위해 ID를 입력하세요."
            )

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position)
                {
                    0->{
                        childFragmentManager.beginTransaction().replace(R.id.dialog_fragment_container, inputPhoneDialogFragment).commit()
                    }
                    1->{
                        childFragmentManager.beginTransaction().replace(R.id.dialog_fragment_container, inputIdDialogFragment).commit()
                    }
                }
            }
        })
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        childFragmentManager.beginTransaction().replace(R.id.dialog_fragment_container, inputPhoneDialogFragment).commit()
    }

    override fun onResume() {
        super.onResume()
        val width = Resources.getSystem().displayMetrics.widthPixels*0.9
        val height = Resources.getSystem().displayMetrics.heightPixels*0.5
        childFragmentManager.beginTransaction().replace(R.id.dialog_fragment_container, inputIdDialogFragment)
        dialog!!.window!!.setLayout(width.toInt(), height.toInt())
    }
}

