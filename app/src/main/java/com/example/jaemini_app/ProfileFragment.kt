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
        // 서버에서 프로필 데이터 가져오기
        RetrofitClient.api.getProfile()
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (!response.isSuccessful) {
                        showError("서버 오류: ${response.code()}")
                        loadDefaultData()
                        return
                    }

                    val body = response.body()
                    if (body == null) {
                        showError("데이터가 없습니다")
                        loadDefaultData()
                        return
                    }

                    updateUI(body)
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    showError("네트워크 오류: ${t.message}")
                    loadDefaultData()
                }
            })
    }

    private fun updateUI(data: ProfileResponse) {
        // 사용자 이름
        tvUsername.text = data.username
        tvProfileInitial.text = data.username.firstOrNull()?.toString() ?: "?"

        // 통계 데이터
        tvTotalDays.text = "${data.totalDays}일"
        tvTotalKcal.text = formatNumber(data.totalKcal)
        tvTotalPunch.text = formatNumber(data.totalPunch)
    }

    private fun loadDefaultData() {
        // 기본 데이터 (서버 연결 실패 시)
        val username = MainActivity.username ?: "사용자"
        tvUsername.text = username
        tvProfileInitial.text = username.firstOrNull()?.toString() ?: "?"

        tvTotalDays.text = "0일"
        tvTotalKcal.text = "0"
        tvTotalPunch.text = "0"
    }

    private fun formatNumber(number: Int): String {
        // 숫자를 천 단위 콤마로 포맷팅
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
                // SharedPreferences 또는 세션 정보 삭제
                TokenManager.clearToken(requireContext())
                MainActivity.username = null

                // LoginActivity로 이동
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