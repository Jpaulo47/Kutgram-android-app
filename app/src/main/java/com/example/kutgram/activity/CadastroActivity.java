package com.example.kutgram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kutgram.R;
import com.example.kutgram.helper.ConfiguracaoFirebase;
import com.example.kutgram.helper.UsuarioFirebase;
import com.example.kutgram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastro;
    private ProgressBar progressBar;

    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

            inicializarComponentes();

            //CADASTRAR USUARIO
        progressBar.setVisibility(View.GONE);
        botaoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome  = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if ( !textoNome.isEmpty ()){
                    if ( !textoEmail.isEmpty ()){
                        if ( !textoSenha.isEmpty ()){

                            usuario = new Usuario();
                            usuario.setNome( textoNome );
                            usuario.setEmail( textoEmail );
                            usuario.setSenha( textoSenha );
                            cadastrar( usuario );

                        }else{
                            Toast.makeText(CadastroActivity.this,
                                    "Preencha a senha",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(CadastroActivity.this,
                                "Preencha o email",
                                Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o nome",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    //método responsavel por cadastrar usuario com e-mail e senha e
    //fazer validações ao fazer o cadstro.
    public  void cadastrar (final Usuario usuario){

        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()

        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if( task.isSuccessful() ){

                                //Salvar dados do usuario no firebase

                            try {
                                progressBar.setVisibility( View.GONE );

                                String idUsuario = task.getResult().getUser().getUid();
                                usuario.setId(idUsuario);
                                usuario.salvar();

                                //Salvar dados no profile firebase
                                UsuarioFirebase.atualizaNomeUsuario( usuario.getNome() );

                                    Toast.makeText(CadastroActivity.this,
                                            "Cadastrado com sucesso",
                                            Toast.LENGTH_SHORT).show();

                                    startActivity( new Intent(getApplicationContext(), Main2Activity.class));
                                    finish();

                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                        }else {

                            progressBar.setVisibility( View.GONE );

                            String erroExcecao = "";
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                erroExcecao = "Digite uma senha mais forte!";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erroExcecao = "Por favor, digite um e-mail válido";
                            }catch (FirebaseAuthUserCollisionException e){
                                erroExcecao = "Esta conta já foi cadastrada";
                            }catch (Exception e){
                                erroExcecao = "ao cadastrar usuário:" + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(CadastroActivity.this,
                                    "Erro: " + erroExcecao,
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                }
        );


    }

    public void inicializarComponentes(){

        campoNome        = findViewById(R.id.editCadastroNome);
        campoEmail       = findViewById(R.id.editCadastroEmail);
        campoSenha       = findViewById(R.id.editCadastroSenha);
        botaoCadastro      = findViewById(R.id.buttonCadastrar);
        progressBar      = findViewById(R.id.progressCadastro);

        campoNome.requestFocus();


    }

}
