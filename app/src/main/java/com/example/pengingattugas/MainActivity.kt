package com.example.pengingattugas

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging

// MainActivity.kt
class MainActivity : AppCompatActivity() {

    // Harus berupa properti kelas, bukan di dalam onCreate
    private val mintaIzin = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { diberikan ->
        if (!diberikan) Toast.makeText(this, "Notifikasi dimatikan", Toast.LENGTH_SHORT).show()
    }

    private lateinit var txtStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtStatus = findViewById(R.id.txtStatus)

        // Android 13 (API 33) ke atas wajib meminta izin notifikasi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mintaIzin.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Materi 05: ambil token perangkat, lihat di Logcat dengan filter FCM
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "Token: ${task.result}")
            }
        }

        // Lanjutan (opsional): berlangganan topik, lalu bisa menerima pesan ke topik ini
        FirebaseMessaging.getInstance().subscribeToTopic("tugas-kuliah")
            .addOnCompleteListener { task ->
                Log.d(
                    "FCM",
                    if (task.isSuccessful) "Berlangganan topik tugas-kuliah"
                    else "Gagal berlangganan topik tugas-kuliah"
                )
            }

        // Tampilkan tugas dari notifikasi yang diketuk, di TextView txtStatus
        tampilkanTugas(intent)
    }

    // Dipanggil saat MainActivity sudah ada (terbuka atau di latar belakang)
    // lalu notifikasi diketuk. Tanpa ini, txtStatus tidak ikut berubah.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        tampilkanTugas(intent)
    }

    private fun tampilkanTugas(intent: Intent) {
        val tugasId = intent.getStringExtra("tugas_id")
        txtStatus.text =
            if (tugasId != null) "Buka tugas $tugasId" else "Menunggu pengingat tugas"
    }
}
