package com.kimjinwouk.petwalk.myinfo

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kimjinwouk.petwalk.BaseFragment
import com.kimjinwouk.petwalk.MainActivity
import com.kimjinwouk.petwalk.R
import com.kimjinwouk.petwalk.databinding.FragmentMyinfoBinding
import com.kimjinwouk.petwalk.login.LoginActivity

class MyInfoFragment : Fragment(R.layout.fragment_myinfo), View.OnClickListener {

    private lateinit var binding: FragmentMyinfoBinding
    private lateinit var selectedUri: Uri

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).Log(requireContext(), "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).Log(requireContext(), "onCreate")
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).Log(requireContext(), "onViewCreated")
        binding = FragmentMyinfoBinding.bind(view)
        initUser()
        initLitener()
    }

    private fun initLitener() {
        binding.userProfileImageView.setOnClickListener(this)
        binding.signOutButton.setOnClickListener(this)
    }

    private fun initUser() {
        (activity as MainActivity).auth.currentUser ?: (activity as MainActivity).finish()
        //TODO 로그인했지만 currentUser내용이없다면 로그인화면으로 전환되어야 한다.

        val UserItemModel = (activity as MainActivity).data.loginUser

        binding.userEmailTextView.text = UserItemModel.email
        binding.userNickNameTextView.text = UserItemModel.nickName
        (activity as MainActivity).Log(requireContext(), "initUser")
        Glide.with(binding.userProfileImageView)
            .load(UserItemModel.imageUri)
            .transform(
                CenterCrop(),
                RoundedCorners(dpToPx(binding.userProfileImageView.context, 30))
            )
            .thumbnail(0.1f)
            .error(R.drawable.ic_baseline_image_search_24)
            .placeholder(R.drawable.ic_baseline_image_search_24)
            .fallback(R.drawable.ic_baseline_image_search_24)
            .into(binding.userProfileImageView)


    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.userProfileImageView.id -> userimageUpdate()
            binding.signOutButton.id -> signOut()
        }
    }

    private fun signOut() {
        (activity as MainActivity).auth.signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        (activity as MainActivity).finish()

    }

    private fun userimageUpdate() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                //권한을 얻은 상태이면 이미지 가져오기
                startContentProvider()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                //권한을 거부했다면 왜그런지 보여주기
                showPermissionContextPopup()
            }
            else -> {
                //최초 권한등록
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_READ_EXTERNAL_STOREAGE_CODE
                )
            }
        }


    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.request_Permission))
            .setMessage(getString(R.string.request_Permission_msg))
            .setPositiveButton(getString(R.string.request_Permission_agree), { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_READ_EXTERNAL_STOREAGE_CODE
                )
            })
            .create()
            .show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_READ_EXTERNAL_STOREAGE_CODE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.request_Permission_denied_msg),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }


    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, GET_IMAGE_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            GET_IMAGE_CODE -> {
                val uri = data?.data
                if (uri != null) {
                    binding.userProfileImageView.setImageURI(uri)
                    Glide.with(binding.userProfileImageView)
                        .load(uri)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(dpToPx(binding.userProfileImageView.context, 30))
                        )
                        .into(binding.userProfileImageView)

                    selectedUri = uri
                    uploadProfileImage()
                } else {
                    Toast.makeText(requireContext(), "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(requireContext(), "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun uploadProfileImage() {
        val fileName = "${System.currentTimeMillis()}.png"

        (activity as MainActivity).storage.reference.child("users/photo").child(fileName)
            .putFile(selectedUri)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //업로드가 완료되면 업로드가된 주소가져와야한다.
                    (activity as MainActivity).storage.reference.child("users/photo")
                        .child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successImageUpload(uri.toString())
                        }
                        .addOnFailureListener {
                            failImageUpload()
                        }
                } else {
                    failImageUpload()
                }
            }
    }

    private fun successImageUpload(uri: String) {

        (activity as MainActivity).auth.currentUser?.let { it ->
            (activity as MainActivity).userDB.child(it.uid).child("imageUri").setValue(uri)

        }
    }

    private fun failImageUpload() {

    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    companion object : BaseFragment{
        override fun newInstance(): MyInfoFragment {
            return MyInfoFragment()
        }
        const val PERMISSION_READ_EXTERNAL_STOREAGE_CODE = 100
        const val GET_IMAGE_CODE = 101

    }

}