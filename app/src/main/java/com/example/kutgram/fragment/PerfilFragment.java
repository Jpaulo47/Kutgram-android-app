package com.example.kutgram.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kutgram.R;
import com.example.kutgram.activity.EditarPerfilActivity;
import com.example.kutgram.activity.PerfilAmigoActivity;
import com.example.kutgram.adapter.AdapterGrid;
import com.example.kutgram.helper.ConfiguracaoFirebase;
import com.example.kutgram.helper.UsuarioFirebase;
import com.example.kutgram.model.Postagem;
import com.example.kutgram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    private ProgressBar progressBar;
    private CircleImageView imagePerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private Button buttonAcaoPerfil;
    public GridView gridViewPerfil;
    private Usuario usuarioLogado;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private ValueEventListener valueEventListenerPerfil;
    private DatabaseReference postagensUsuarioRef;
    private AdapterGrid adapterGrid;


    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //confirações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");

        //configurar referencia postagens usuarios
        postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens")
                .child( usuarioLogado.getId());

        //CONFIGURAÇÃO DOS COMPONENTES
       inicializarComponentes(view);

        //abre ediçao de perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        //inicializar image loader
        inicializarImageLoader();


        //Carrega as fotos das postagens de um usuario
        carregarFotosPostagem();



        return view;
    }


    public void carregarFotosPostagem(){

        //recuperar as fotos postadas pelo usuario
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //configurar tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth( tamanhoImagem );


                List<String> urlFotos = new ArrayList<>();
                for ( DataSnapshot ds: dataSnapshot.getChildren() ){
                    Postagem postagem = ds.getValue( Postagem.class );
                    urlFotos.add( postagem.getCaminhoFoto() );

                    //Log.i("postagens", "url" + postagem.getCaminhoFoto());

                }

                int qtdPostagem = urlFotos.size();
                textPublicacoes.setText( String.valueOf(qtdPostagem) );

                //configurar adapter
                adapterGrid = new AdapterGrid( getActivity(), R.layout.grid_postagem, urlFotos );
                gridViewPerfil.setAdapter( adapterGrid );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void inicializarImageLoader(){

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.
                Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);

    }

    private void inicializarComponentes(View view){

        gridViewPerfil     = view.findViewById(R.id.gridViewPerfil);
        progressBar        = view.findViewById(R.id.progressBarPerfil);
        imagePerfil        = view.findViewById(R.id.imagePerfil);
        textPublicacoes    = view.findViewById(R.id.textPublicacoes);
        textSeguidores     = view.findViewById(R.id.textSeguidores);
        textSeguindo       = view.findViewById(R.id.textSeguindo);
        buttonAcaoPerfil   = view.findViewById(R.id.buttonAcaoPerfil);
    }
    private void recuperarDadosUsuarioLogado(){

        usuarioLogadoRef = usuariosRef.child( usuarioLogado.getId() );
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Usuario usuario = dataSnapshot.getValue( Usuario.class );

                        //String postagens  = String.valueOf( usuario.getPostagens() );
                        String seguindo   = String.valueOf( usuario.getSeguindo() );
                        String seguidores = String.valueOf( usuario.getSeguidores() );

                        //configurar valores recuperados
                        //textPublicacoes.setText( postagens );
                        textSeguidores.setText( seguidores );
                        textSeguindo.setText( seguindo );


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void recuperarFotoUsuario(){

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Recuperar foto do usuário
        String caminhoFoto = usuarioLogado.getCaminhofoto();
        if( caminhoFoto != null ){
            Uri url = Uri.parse( caminhoFoto );
            Glide.with(getActivity())
                    .load( url )
                    .into( imagePerfil );
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //recuperar dados usuario logado
        recuperarDadosUsuarioLogado();

        //Recuperar foto usuário
        recuperarFotoUsuario();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioLogadoRef.removeEventListener( valueEventListenerPerfil );
    }
}
