package com.example.artur_leonel_modulo03_pe

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.artur_leonel_modulo03_pe.API.LoginResponse
import com.example.artur_leonel_modulo03_pe.API.RetrofitClient
import com.example.artur_leonel_modulo03_pe.API.UserDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var attempCount = 0
    private val maxAttempts = 3
    private lateinit var Acessar: Button
    private lateinit var Cadastrar: Button
    private lateinit var Usuario: EditText
    private lateinit var Senha: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Acessar = findViewById(R.id.button)
        Cadastrar = findViewById(R.id.button2)
        Usuario = findViewById(R.id.editTextText)
        Senha = findViewById(R.id.editTextText2)

        Cadastrar.setOnClickListener {
            startActivity(Intent(this, CadastrarActivity::class.java))
        }

        Acessar.setOnClickListener {
            val inputUsuario = Usuario.text.toString()
            val inputSenha = Senha.text.toString()
            if (inputUsuario.isBlank() || inputSenha.isBlank()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                attempCount++
                trocarDeCor()
                verificarQuantidadeEBloquear()
            } else {
                realizarLogin(inputUsuario, inputSenha)


            }
        }


    }

    private fun trocarDeCor() {
        val red = ContextCompat.getDrawable(this, R.drawable.redblackgrounded)
        Senha.background = red
        Usuario.background = red
    }

    private fun verificarQuantidadeEBloquear() {
        val red = ContextCompat.getDrawable(this, R.drawable.redblackgrounded)
        val black = ContextCompat.getDrawable(this, R.drawable.blackgroundred)
        if (attempCount > maxAttempts) {
            Senha.isEnabled = false
            Usuario.isEnabled = false
            Cadastrar.isEnabled = false
            Acessar.isEnabled = false
            Usuario.background = black
            Senha.background = black
            Handler().postDelayed({
                Senha.isEnabled = true
                Usuario.isEnabled = true
                Cadastrar.isEnabled = true
                Acessar.isEnabled = true
                attempCount = 0
            }, 2000)
        }
    }

    private fun realizarLogin(email: String, senha: String) {
        RetrofitClient.UsuariosApi.realizarLogin(email, senha).enqueue(object:Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful) {
                    listarUsuarios(email)
                } else {
                    listarUsuarios(email)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

            }
        })

    }

    private fun listarUsuarios(email: String) {
        RetrofitClient.UsuariosApi.listarUsuarios().enqueue(object :Callback<List<UserDetails>> {
            override fun onResponse(
                call: Call<List<UserDetails>>,
                response: Response<List<UserDetails>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val usuarios = response.body()
                    val usuarioCerto = usuarios?.find {it.email == email}

                    if (usuarioCerto != null) {

                        val intent = Intent(this@MainActivity, TelaPrincipal::class.java)
                        val email = usuarioCerto.email
                        val dr = usuarioCerto.dr
                        val nome = usuarioCerto.nome
                        intent.putExtra("usuario", nome)
                        intent.putExtra("email", email)
                        intent.putExtra("dr", dr)
                        startActivity(intent)
                    } else {
                        attempCount++
                        verificarQuantidadeEBloquear()
                        Toast.makeText(this@MainActivity, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                    }


                } else {

                }
            }

            override fun onFailure(call: Call<List<UserDetails>>, t: Throwable) {
            }

        })
    }

}

