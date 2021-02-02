package com.example.justchatting.ui.chattingRoom

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.justchatting.R
import com.example.justchatting.base.BaseActivity
import com.example.justchatting.data.DTO.UserModel
import com.example.justchatting.databinding.ActivityChattingRoomBinding
import com.example.justchatting.ui.chatting.SelectGroupActivity
import kotlinx.android.synthetic.main.activity_chatting_room.*
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val REQUEST_CODE = 1000

class ChattingRoomActivity : BaseActivity<ActivityChattingRoomBinding>() {

    private val viewModel: ChattingRoomViewModel by viewModel()


    private var groupId: String? = null
    private val friendsAdapter = FriendsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setSupportActionBar(toolbar)

        with(
            binding.navView.getHeaderView(0)
                .findViewById<RecyclerView>(R.id.chatting_room_drawer_recyclerview)
        ) {
            setHasFixedSize(true)
            adapter = friendsAdapter
        }

        binding.navView.getHeaderView(0)
            .findViewById<ImageView>(R.id.chatting_room_drawer_add_member).setOnClickListener {
            val intent = Intent(this, SelectGroupActivity::class.java)
            intent.putExtra("members", viewModel.getMembers().value)
            intent.putExtra("before", "chattingRoomActivity")
            startActivityForResult(intent, REQUEST_CODE)
        }

        binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.chtting_room_drawer_exit)
            .setOnClickListener {
                viewModel.exit()
                finish()
            }

        supportFragmentManager.beginTransaction()
            .replace(R.id.chatting_log, ChattingLogFragment()).commit()

        groupId = intent.getStringExtra("groupId")

        if (groupId == "") {
            val initMembers = intent.getSerializableExtra("groupMembers") as HashMap<String, UserModel>
            viewModel.addMember(initMembers)
        } else {
            viewModel.groupId = groupId!!
            viewModel.loadGroupMembers(groupId)
        }

        viewModel.getMembers().observe(this, Observer {
            friendsAdapter.setUsers(it)
            friendsAdapter.notifyDataSetChanged()
        })
    }

    override fun getLayoutId() = R.layout.activity_chatting_room

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chatting_room_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.chatting_room_drawer_summon -> {
                drawerLayout.openDrawer(GravityCompat.END)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val invitedMember =
                data.getSerializableExtra("invited member") as HashMap<String, UserModel>

            viewModel.addMember(invitedMember)
        }
    }
}