package android.bestfood.com.br.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.bestfood.com.br.R;
import android.bestfood.com.br.adapter.AdapterPedido;
import android.bestfood.com.br.adapter.AdapterProduto;
import android.bestfood.com.br.helper.ConfiguracaoFirebase;
import android.bestfood.com.br.helper.UsuarioFirebase;
import android.bestfood.com.br.listener.RecyclerItemClickListener;
import android.bestfood.com.br.model.Pedido;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getIdUsuario();

        //Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configuração RecyclerView
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setAdapter(adapterPedido);

        recuperarPedidos();

        //ao clicar no pedido
        recyclerPedidos.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerPedidos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                Pedido pedido = pedidos.get(position);

                pedido.setStatus("Finalizado");
                pedido.atualizarStatus();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    private void recuperarPedidos() {
        dialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Carregando dados...")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef.child("pedidos").child(idEmpresa);
        Query pedidoPesquisa = pedidoRef.orderByChild("status").equalTo("Confirmado");

        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pedidos.clear();

                if (dataSnapshot.getValue() != null){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);

                        pedidos.add(pedido);
                    }

                    adapterPedido.notifyDataSetChanged();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarComponentes() {
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
    }
}
