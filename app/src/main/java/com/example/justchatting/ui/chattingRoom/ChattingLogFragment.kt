package com.example.justchatting.ui.chattingRoom

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justchatting.R
import com.example.justchatting.base.BaseFragment
import com.example.justchatting.databinding.FragmentChattingLogBinding
import kotlinx.android.synthetic.main.fragment_chatting_log.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChattingLogFragment : BaseFragment<FragmentChattingLogBinding>() {
    private lateinit var chattingRoomAdapter: ChattingLogdapter
    private val viewModel: ChattingRoomViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        chattingRoomAdapter = ChattingLogdapter()

        chatting_room_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chattingRoomAdapter
        }

        if(viewModel.groupId != ""){
            viewModel.setChatLogAddListener(viewModel.groupId)
        }

        viewModel.getChatLogs().observe(this.viewLifecycleOwner, Observer {
            chattingRoomAdapter.setChattingLog(it)
            chattingRoomAdapter.notifyDataSetChanged()
            chatting_room_recyclerview.scrollToPosition(it.size-1)
        })

        chatting_room_button_send.setOnClickListener {
            if(chatting_room_edittext_input.text.isNotEmpty()){
                if(viewModel.groupId == ""){
                    viewModel.createGroupId()
                } else{
                    viewModel.sendText(chatting_room_edittext_input.text.toString(), viewModel.groupId)
                    chatting_room_edittext_input.setText("")
                }
            }
        }

        viewModel.getNewGroupId().observe(this.viewLifecycleOwner , Observer {newId->
            viewModel.groupId = newId
            viewModel.setChatLogAddListener(viewModel.groupId)
            viewModel.sendText(chatting_room_edittext_input.text.toString(), viewModel.groupId)
            chatting_room_edittext_input.setText("")
        })

        viewModel.chatLogFetchError().observe(this.viewLifecycleOwner, Observer {
            //에러 처리
        })
    }

    override fun getLayoutId() = R.layout.fragment_chatting_log
}