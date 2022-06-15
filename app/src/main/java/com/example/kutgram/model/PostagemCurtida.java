package com.example.kutgram.model;

import com.example.kutgram.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class PostagemCurtida {

    public int qtdCurtidas = 0;
    public Usuario usuario;
    public Feed feed;

    public PostagemCurtida() {

    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        //objeto usuario
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNome());
        dadosUsuario.put("caminhoFoto", usuario.getCaminhofoto());


        DatabaseReference pCurtidasRef = firebaseRef
                .child("postagens-curtidas")
                .child( feed.getId() )
                .child( usuario.getId ());
        pCurtidasRef.setValue( dadosUsuario );

        atualizarQtd(1);
    }

    public void atualizarQtd(int valor){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pCurtidasRef = firebaseRef
                .child("postagens-curtidas")
                .child( feed.getId() )
                .child("qtdCurtidas");
        setQtdCurtidas( getQtdCurtidas() + valor );
        pCurtidasRef.setValue( getQtdCurtidas() );

    }

    public void remover(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference pCurtidasRef = firebaseRef
                .child("postagens-curtidas")
                .child( feed.getId() )
                .child( usuario.getId ());
        pCurtidasRef.removeValue();

        atualizarQtd(-1);
    }


    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public int getQtdCurtidas() {
        return qtdCurtidas;
    }

    public void setQtdCurtidas(int qtdCurtidas) {
        this.qtdCurtidas = qtdCurtidas;
    }
}
