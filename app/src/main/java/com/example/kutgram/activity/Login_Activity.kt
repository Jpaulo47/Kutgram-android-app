package com.example.kutgram.activity

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.ProgressBar
import com.example.kutgram.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import com.example.kutgram.R
import android.widget.Toast
import com.example.kutgram.helper.ConfiguracaoFirebase
import android.content.Intent
import android.view.View
import android.widget.Button
import com.example.kutgram.activity.Main2Activity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.example.kutgram.activity.CadastroActivity
import com.example.kutgram.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task

class Login_Activity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    private var usuario: Usuario? = null
    private var autenticacao: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        verificarUsuarioLogado()
        inicializarComponentes()

        //Fazer login do usuario
        binding.progressLogin.visibility = View.GONE
        binding.buttonEntrar.setOnClickListener {
            val textoEmail = binding.editLoginEmail.text.toString()
            val textosenha = binding.editLoginSenha.text.toString()
            if (!textoEmail.isEmpty()) {
                if (!textosenha.isEmpty()) {
                    usuario = Usuario()
                    usuario!!.email = textoEmail
                    usuario!!.senha = textosenha
                    validarLogin(usuario!!)
                } else {
                    Toast.makeText(this@Login_Activity,
                            "Preencha a senha!",
                            Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@Login_Activity,
                        "Preencha o e-mail!",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun verificarUsuarioLogado() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
        if (autenticacao?.getCurrentUser() != null) {
            startActivity(Intent(applicationContext, Main2Activity::class.java))
            finish()
        }
    }

    fun validarLogin(usuario: Usuario) {
        binding.progressLogin.visibility = View.VISIBLE
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
        autenticacao?.signInWithEmailAndPassword(
                usuario.email,
                usuario.senha
        )?.addOnCompleteListener(object : OnCompleteListener<AuthResult> {
            override fun onComplete(task: Task<AuthResult>) {
                if (task.isSuccessful) {
                    binding.progressLogin.visibility = View.GONE
                    startActivity(Intent(applicationContext, Main2Activity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@Login_Activity,
                            "Erro ao fazer login",
                            Toast.LENGTH_SHORT).show()
                    binding.progressLogin.visibility = View.GONE
                }
            }
        })
    }

    fun abrirCadastro(view: View?) {
        val i = Intent(this@Login_Activity, CadastroActivity::class.java)
        startActivity(i)
    }

    fun inicializarComponentes() {

        binding.editLoginEmail.requestFocus()
    }
}