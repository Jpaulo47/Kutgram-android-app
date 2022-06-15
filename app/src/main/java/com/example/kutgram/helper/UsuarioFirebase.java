package com.example.kutgram.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.kutgram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();

    }

    public static void atualizaNomeUsuario( String nome){

        try{

            //usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName( nome )
                    .build();
            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (!task.isSuccessful()){
                        Log.d("perfil","Erro ao atualizar nome de perfil.");


                    }
                }
            });


        }catch (Exception e){
         e.printStackTrace();
        }

    }

    public static void atualizaFotoUsuario( Uri url){

        try{

            //usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri( url )
                    .build();
            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("perfil","Erro ao atualizar a foto de perfil.");


                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static Usuario getDadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail( firebaseUser.getEmail() );
        usuario.setNome( firebaseUser.getDisplayName() );
        usuario.setId( firebaseUser.getUid() );

        if ( firebaseUser.getPhotoUrl() == null ){
            usuario.setCaminhofoto("");

        }else{
            usuario.setCaminhofoto( firebaseUser.getPhotoUrl().toString() );

        }
        return usuario;
    }

}
