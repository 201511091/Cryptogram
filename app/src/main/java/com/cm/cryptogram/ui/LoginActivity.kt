package com.cm.cryptogram.ui

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.cm.cryptogram.R
import com.cm.cryptogram.base.BaseActivity
import com.cm.cryptogram.base.HttpRequestHelper
import com.cm.cryptogram.databinding.ActivityLoginBinding
import com.cm.cryptogram.ui.tab.CardItem
import com.cm.cryptogram.ui.tab.TabCard
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.security.DigestException
import java.security.MessageDigest
import kotlin.coroutines.CoroutineContext

class LoginActivity : BaseActivity<ActivityLoginBinding>(), CoroutineScope {
    private lateinit var job: Job
    private lateinit var userInfos : String
    private lateinit var database: DatabaseReference
    private var isFinished = false
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }
    fun hashSHA256(msg: String): String {
        val hash: ByteArray
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(msg.toByteArray())
            hash = md.digest()
        } catch (e: CloneNotSupportedException) {
            throw DigestException("couldn't make digest of partial content");
        }

        return bytesToHex(hash)
    }
    fun bytesToHex(byteArray: ByteArray): String {
        val digits = "0123456789ABCDEF"
        val hexChars = CharArray(byteArray.size * 2)
        for (i in byteArray.indices) {
            val v = byteArray[i].toInt() and 0xff
            hexChars[i * 2] = digits[v shr 4]
            hexChars[i * 2 + 1] = digits[v and 0xf]
        }
        return String(hexChars)
    }
    fun generateRandomNum() : Int {
        val rnds = (0..10).random()
        var res = 0
        for (i in 1..10)
            res += rnds
        return rnds
    }
    override fun onViewCreated() {
        job = Job()
        launch(Dispatchers.Main) {
            val httpClient = HttpRequestHelper()
            httpClient.setTargetUrl("https://cryptogram-users-default-rtdb.asia-southeast1.firebasedatabase.app/.json")
            userInfos = httpClient.requestKtorIo()
            Log.i("http", userInfos)
        }

        //로그인 버튼을 누를시
        binding.btnLogin.setOnClickListener {

            database = Firebase.database.reference

            val id = binding.editId.text.toString()
            val pwd = binding.editPass.text.toString()

            Log.i("USER_BTN", userInfos);

            Log.i("USER_BTN", "input Id : " + id + "input pwd : " + pwd);

            val jsonObj = JSONArray(userInfos)
            var isIdRight = false
            var idIndex = -1
            var isPwdRight = false
            for (i in 0..(jsonObj.length() - 1)){
                val item = jsonObj.getJSONObject(i)
                Log.i("USER_BTN", "INLOOP: " + i + " : " + item)
                if ( item.getString("id").equals(id) ) {
                    isIdRight = true
                    idIndex = i
                    preferenceHelper.userIndex = idIndex.toString()
                    preferenceHelper.userPrefInfo = item.getString("keyword_history")
                }
                if ( item.getString("password").equals(pwd) )
                    isPwdRight = true
                if ( isIdRight )
                    break;
            }
            Log.i("LOGIN_BOOL", "BOOLS: " + isIdRight + " : " + isPwdRight)
            if ( !( isIdRight && isPwdRight ) ) {
                if ( !isIdRight ) {
                    Log.i("LOGIN_FAIL", "아이디가 틀렸습니다!")
                    Toast.makeText(this@LoginActivity, "아이디가 틀렸습니다!", Toast.LENGTH_SHORT).show()
                } else if ( !isPwdRight ) {
                    Log.i("LOGIN_FAIL", "비밀번호가 틀렸습니다!")
                    Toast.makeText(this@LoginActivity, "비밀번호가 틀렸습니다!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i("LOGIN_FAIL", "아이디 비밀번호가 모두 틀렸습니다!")
                    Toast.makeText(this@LoginActivity, "아이디 비밀번호가 모두 틀렸습니다!", Toast.LENGTH_SHORT).show()
                }
                preferenceHelper.loginState = false
            } else {
                val rndNum = generateRandomNum().toString();
                val hashedCode = hashSHA256( id + rndNum + pwd )
                var hm : HashMap<String, Any> = HashMap()

                database.child(idIndex.toString()).child("hash_code").get().addOnSuccessListener {
                    Log.i("firebase", "Got value ${it.value}")

                    if ( it.value != null ) {
                        if ( preferenceHelper.loginToken.equals(it.value.toString()) ) {
                            preferenceHelper.loginState = true
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "인증 되지 않은 기기 입니다!", Toast.LENGTH_SHORT).show()
                            preferenceHelper.loginState = false

                        }
                    } else {
                        Log.e("firebase", "서버에 해쉬 코드가 없습니다. 최초 로그인 시도입니다.")
                        Toast.makeText(this@LoginActivity, "서버에 해쉬 코드가 없습니다. 최초 로그인 시도입니다.", Toast.LENGTH_SHORT).show()
                        preferenceHelper.loginToken = hashedCode;

                        val restoreKeyRndNum = generateRandomNum().toString();
                        val restoreKey = hashSHA256( hashedCode + restoreKeyRndNum )

                        hm.put("hash_code", hashedCode)
                        hm.put("hash_restore_key", restoreKey)
                        database.child(idIndex.toString()).updateChildren(hm)

                        preferenceHelper.loginState = true

                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                }.addOnFailureListener{
                    Log.e("firebase", "Firebase 에러", it)
                    Toast.makeText(this@LoginActivity, "서버 에러", Toast.LENGTH_SHORT).show()
                    preferenceHelper.loginState = false
                }
            }
        }
    }
    override fun onBackPressed() {
        if ( isFinished ) {
            super.onBackPressed()
        } else {
            Log.i("BACKBUTTON_PRESSED", "NOT DONE YET")
        }
    }

}