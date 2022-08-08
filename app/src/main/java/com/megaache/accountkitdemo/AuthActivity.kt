package com.megaache.accountkitdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.result.AuthAccount
import com.huawei.hms.support.account.service.AccountAuthService
import com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton

const val HUAWEI_SIGNIN_REQ_CODE = 1

class AuthActivity : AppCompatActivity() {

    lateinit var accountService: AccountAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initAccountKit()
        initUi()
    }

    fun initAccountKit() {
        val params = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setIdToken()
            .setEmail()
            .createParams()

        accountService = AccountAuthManager.getService(this, params)

    }

    fun initUi() {
        findViewById<HuaweiIdAuthButton>(R.id.huaweiIdAuthButton).setOnClickListener {
            signInWithHuawei()
        }
    }

    fun signInWithHuawei() {
        startActivityForResult(
            accountService.signInIntent,
            HUAWEI_SIGNIN_REQ_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HUAWEI_SIGNIN_REQ_CODE) {
            val result = AccountAuthManager.parseAuthResultFromIntent(data)
            result.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val account = result.result
                    Toast.makeText(this, "welcome!", Toast.LENGTH_SHORT).show()
                    navigateToProfileActivity(account)
                } else {
                    Toast.makeText(this, "Authorization failed!", Toast.LENGTH_SHORT).show()
                    task.exception.printStackTrace()
                }
            }

        }
    }


    fun navigateToProfileActivity(account:AuthAccount) {
        startActivity(Intent(this, ProfileActivity::class.java).apply {
            putExtra(EXTRA_HUAWEI_ACCOUNT,account)
        })
        finish()
    }
}