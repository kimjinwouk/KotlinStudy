package a.jinkim.trade.mypage

import a.jinkim.trade.R
import a.jinkim.trade.databinding.FragmentMypageBinding
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    private var binding: FragmentMypageBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentMyPageBind = FragmentMypageBinding.bind(view)
        binding = fragmentMyPageBind

        fragmentMyPageBind.signInOutButton.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                if (auth.currentUser == null) {
                    //로그인
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                successSignIn()
                            } else {
                                Toast.makeText(context, "로그인 실패!", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    //로그아웃
                    auth.signOut()
                    binding.emailEditText.text.clear()
                    binding.emailEditText.isEnabled = true
                    binding.passwordEditText.text.clear()
                    binding.passwordEditText.isEnabled = true

                    binding.signInOutButton.text = "로그인"
                    binding.signInOutButton.isEnabled = false
                    binding.signUpButton.isEnabled = false
                }

            }


        }

        fragmentMyPageBind.signUpButton.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                context,
                                "회원가입에 성공했습니다. 로그인 버튼을 눌러주세요.",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(context, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }


            }
        }

        fragmentMyPageBind.emailEditText.addTextChangedListener {
            binding?.let { it ->
                val enable =
                    it.emailEditText.text.isNotEmpty() && it.passwordEditText.text.isNotEmpty()

                it.signUpButton.isEnabled = enable
                it.signInOutButton.isEnabled = enable
            }
        }

        fragmentMyPageBind.passwordEditText.addTextChangedListener {
            binding?.let { it ->
                val enable =
                    it.emailEditText.text.isNotEmpty() && it.passwordEditText.text.isNotEmpty()
                it.signUpButton.isEnabled = enable
                it.signInOutButton.isEnabled = enable
            }
        }

    }

    private fun successSignIn() {
        if (auth.currentUser == null) {
            Toast.makeText(context, "로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        binding?.emailEditText?.isEnabled = false
        binding?.passwordEditText?.isEnabled = false
        binding?.signUpButton?.isEnabled = false
        binding?.signInOutButton?.text = "로그아웃"
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            binding?.let { binding ->
                binding.emailEditText.text.clear()
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.text.clear()
                binding.passwordEditText.isEnabled = true
                binding.signInOutButton.text = "로그인"
                binding.signInOutButton.isEnabled = false
                binding.signUpButton.isEnabled = false
            }

        } else {
            binding?.let { binding ->
                binding.emailEditText.setText(auth.currentUser.email)
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.setText("******")
                binding.passwordEditText.isEnabled = false
                binding.signInOutButton.text = "로그아웃"
                binding.signInOutButton.isEnabled = true
                binding.signUpButton.isEnabled = false
            }
        }
    }
}