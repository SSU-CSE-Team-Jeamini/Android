package com.example.jaemini_app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private lateinit var tvUsername: TextView
    private lateinit var tvProfileInitial: TextView
    private lateinit var tvTotalDays: TextView
    private lateinit var tvTotalKcal: TextView
    private lateinit var tvTotalPunch: TextView
    private lateinit var btnLogout: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        loadProfileData()
        setupLogout()
    }

    private fun initViews(view: View) {
        tvUsername = view.findViewById(R.id.tv_username)
        tvProfileInitial = view.findViewById(R.id.tv_profile_initial)
        tvTotalDays = view.findViewById(R.id.tv_total_days)
        tvTotalKcal = view.findViewById(R.id.tv_total_kcal)
        tvTotalPunch = view.findViewById(R.id.tv_total_punch)
        btnLogout = view.findViewById(R.id.btn_logout_container)
    }

    private fun loadProfileData() {
        val username = MainActivity.username ?: TokenManager.getUserId(requireContext())

        if (username == null) {
            loadDefaultData()
            return
        }

        // 서버에서 프로필 데이터 가져오기
        RetrofitClient.api.getProfile(username)
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (!response.isSuccessful || response.body() == null) {
                        showError("서버 오류: ${response.code()}")
                        loadDummyDataAsFallback()
                        return
                    }

                    val body = response.body()!!

                    if (body.status == "success" || body.status == null) {
                        val profileData = body.getActualData()
                        updateUIWithServerData(profileData)
                    } else {
                        showError(body.message ?: "프로필 로드 실패")
                        loadDummyDataAsFallback()
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    showError("네트워크 오류: ${t.message}")
                    loadDummyDataAsFallback()
                }
            })
    }

    private fun updateUIWithServerData(data: ProfileData) {
        tvUsername.text = data.name
        tvProfileInitial.text = data.name.firstOrNull()?.toString() ?: "?"

        tvTotalDays.text = "${data.totalDays}일"
        tvTotalKcal.text = "${formatNumber(data.totalKcal)}kcal"
        tvTotalPunch.text = "${formatNumber(data.totalPunch)}회"
    }

    private fun loadDummyDataAsFallback() {
        val currentUser = DummyUserStore.currentUser
        if (currentUser != null) {
            tvUsername.text = currentUser.nickname
            tvProfileInitial.text = currentUser.nickname.firstOrNull()?.toString() ?: "?"

            tvTotalDays.text = "${currentUser.totalDays}일"
            tvTotalKcal.text = "${formatNumber(currentUser.totalCalorie)}kcal"
            tvTotalPunch.text = "${formatNumber(currentUser.totalPunch)}회"

            Toast.makeText(requireContext(), "서버 연결 실패 - 임시 데이터 사용", Toast.LENGTH_SHORT).show()
        } else {
            loadDefaultData()
        }
    }

    private fun loadDefaultData() {
        val username = MainActivity.username ?: "사용자"
        tvUsername.text = username
        tvProfileInitial.text = username.firstOrNull()?.toString() ?: "?"

        tvTotalDays.text = "0일"
        tvTotalKcal.text = "0kcal"
        tvTotalPunch.text = "0회"
    }

    private fun formatNumber(number: Int): String {
        return String.format("%,d", number)
    }

    private fun setupLogout() {
        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        AlertDialog.Builder(requireContext())
            .setTitle("로그아웃")
            .setMessage("정말 로그아웃 하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                TokenManager.clearToken(requireContext())
                MainActivity.username = null
                DummyUserStore.currentUser = null

                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()

                Toast.makeText(requireContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("아니오", null)
            .show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}