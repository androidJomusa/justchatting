package com.example.justchatting.ui.friend

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justchatting.R
import com.example.justchatting.base.BaseFragment
import com.example.justchatting.databinding.FragmentFriendBinding
import com.example.justchatting.ui.friend.dialog.AddFriendFragment
import com.example.justchatting.ui.friend.dialog.TabDialogFragment

import com.squareup.picasso.Picasso
import com.example.justchatting.ui.login.RegisterActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_friend.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class FriendFragment : BaseFragment<FragmentFriendBinding>(), TabDialogFragment.OnTabDialogFragmentListener{
    private val viewModel: FriendViewModel by viewModel()
    private val friendAdapter : FriendAdapter = FriendAdapter()
    private val disposable = CompositeDisposable()

    override fun getLayoutId(): Int = R.layout.fragment_friend

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        friend_recyclerview.apply {
            setHasFixedSize(true)
            adapter = friendAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        setPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.friend_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.friend_sync_button -> {
                viewModel.sync()
            }
            R.id.friend_add_friend_button -> {
                var tabDialogFragment = TabDialogFragment()
                var fragmentManager = requireActivity()
                    .supportFragmentManager
                    .beginTransaction()
                    .addToBackStack(null)

                tabDialogFragment.show(fragmentManager,"dialog")
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun messageFromTabDialog(isAdd: Boolean) {
        if(isAdd)
        {
            Log.d("FriendFragment", "add friend")
        }else
        {
            Log.d("FriendFragment", "cancel to add friend")
            var prev = requireActivity().supportFragmentManager.findFragmentByTag("dialog")
            if(prev != null)
                requireActivity().supportFragmentManager.beginTransaction().remove(prev).commit()
        }
    }

    @SuppressLint("CheckResult")
    private fun loadFriends() {
        viewModel.getAnyUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("FriendFragment","data exist")
                setObserver()
            },{
                Log.d("FriendFragment","data doesn't exist")
                viewModel.sync()
                setObserver()
            })

    }

    @SuppressLint("CheckResult")
    private fun setObserver()
    {
        disposable.add(viewModel.getUsers().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pagedList->
                friendAdapter.submitList(pagedList)
            },{})
        )
        disposable.add(viewModel.getMyUser().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ myUser ->
                if(myUser!= null) {
                    friend_my_textview_username.text = myUser.username
                    Picasso.get().load(myUser.profileImageUrl)
                        .placeholder(R.drawable.person)
                        .into(friend_my_imageview_profile_image)
                }
            },{}))
    }

    private fun setPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                requireContext() , Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                RegisterActivity.PERMISSIONS_REQUEST_READ_CONTACTS
            )
        } else {
            loadFriends()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == RegisterActivity.PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setPermission()
            } else {
                requireActivity().finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }




}