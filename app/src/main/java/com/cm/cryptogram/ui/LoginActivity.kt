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

            Log.i("BTN_BOOL", "BOOLS: " + isIdRight + " : " + isPwdRight)
            if ( !( isIdRight && isPwdRight ) ) {
                if ( !isIdRight ) {

                } else if ( !isPwdRight ) {

                } else {

                }
                preferenceHelper.loginState = false
            } else {
                val rndNum = generateRandomNum().toString();
                val hashedCode = hashSHA256( id + rndNum + pwd )
                var hm : HashMap<String, Any> = HashMap()

                database.child(idIndex.toString()).child("hash_code").get().addOnSuccessListener {
                    Log.i("firebase", "Got value ${it.value}")
                    Log.i("login_pahse", preferenceHelper.prevLoginToken + " : " + preferenceHelper.loginToken)

                    if ( it.value != null ) {
                        if (preferenceHelper.prevLoginToken.equals(it.value.toString())) {
                            preferenceHelper.prevLoginToken = it.value.toString();
                            preferenceHelper.loginToken = hashedCode

                            hm.put("hash_code", hashedCode)
                            database.child(idIndex.toString()).updateChildren(hm)

                            preferenceHelper.loginState = true

                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "로그인 실패! 다시 시도하세요!", Toast.LENGTH_SHORT).show()
                            preferenceHelper.loginState = false
                        }
                    } else {
                        Log.e("firebase", "서버에 해쉬 코드가 없습니다. 최초 로그인 시도입니다.")
                        preferenceHelper.prevLoginToken = hashedCode;
                        hm.put("hash_code", hashedCode)
                        database.child(idIndex.toString()).updateChildren(hm)
                        if ( preferenceHelper.loginToken == null ) {
                            database.child(idIndex.toString()).child("hash_code").get().addOnSuccessListener {
                                Log.i("firebase2", "Got value ${it.value}")

                                if (preferenceHelper.prevLoginToken.equals(it.value.toString())) {
                                    preferenceHelper.prevLoginToken = it.value.toString();
                                    preferenceHelper.loginToken = hashedCode

                                    hm.put("hash_code", hashedCode)
                                    database.child(idIndex.toString()).updateChildren(hm)

                                    preferenceHelper.loginState = true

                                    startActivity(Intent(this,MainActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginActivity, "로그인 실패! 다시 시도하세요!", Toast.LENGTH_SHORT).show()
                                    preferenceHelper.loginState = false
                                }

                            }.addOnFailureListener{
                                Log.e("firebase", "토큰 인증 에러 발생", it)
                                preferenceHelper.loginState = false
                            }
                        }
                    }
                }.addOnFailureListener{
                    Log.e("firebase", "Firebase 에러", it)
                    preferenceHelper.loginState = false
                }


            }
        }
    }
}