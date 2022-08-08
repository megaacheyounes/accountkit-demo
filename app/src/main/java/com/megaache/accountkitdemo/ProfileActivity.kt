package com.megaache.accountkitdemo

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.result.AuthAccount
import com.huawei.hms.support.account.service.AccountAuthService
import java.net.URL

const val EXTRA_HUAWEI_ACCOUNT = "huawei_account"

class ProfileActivity : AppCompatActivity() {

    var account: AuthAccount? = null
    lateinit var accountService: AccountAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        getHuaweiAccountFromIntent()
        initAccountKit()
        initUi()

    }

    fun getHuaweiAccountFromIntent() {
        account = intent.getParcelableExtra<AuthAccount>(EXTRA_HUAWEI_ACCOUNT)

    }


    fun initAccountKit() {
        val params = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setIdToken()
            .setEmail()
            .createParams()

        accountService = AccountAuthManager.getService(this, params)
    }

    fun initUi() {
        val avatar = findViewById<ImageView>(R.id.avatar)
        val email = findViewById<TextView>(R.id.email)
        val token = findViewById<TextView>(R.id.token)
        val signoutBtn = findViewById<Button>(R.id.signout)

        signoutBtn.setOnClickListener {
            signOutFromHuaweiID()
        }

        account?.let { account ->
            email.text = "email: ${account.email}"
            token.text = "token: ${account.idToken}"

            if (!account.avatarUriString.isNullOrBlank()) {
                //load profile picture
                Thread {
                    val stream = URL(account.avatarUriString).openStream()
                    val bitmap = BitmapFactory.decodeStream(stream)

                    runOnUiThread {
                        avatar.setImageBitmap(bitmap)
                    }
                }.start()
            }
        }
    }

    fun signOutFromHuaweiID() {
        accountService.signOut().addOnCompleteListener {
            accountService.cancelAuthorization().addOnCompleteListener {

                if (it.isSuccessful) {
                    Toast.makeText(this, "seee ya soon!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "ooops sign out failed!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}