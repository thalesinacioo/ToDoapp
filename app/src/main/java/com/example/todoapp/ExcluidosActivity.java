package com.example.todoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ExcluidosActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> itemList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excluidos);

        // Referenciar o componente ListView
        listView = findViewById(R.id.listView2);

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // VOLTAR A TELA DA MAINACTIVITY
                Intent intent = new Intent(ExcluidosActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Criar a lista de itens excluídos
        itemList = new ArrayList<>();

        // Criar o adaptador para a lista
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.text_item, itemList);

        // Associar o adaptador à ListView
        listView.setAdapter(adapter);

        // Recuperar os itens excluídos do banco de dados
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, null, DatabaseHelper.COLUMN_EXCLUIDO + "=1", null, null, null, null);
        while (cursor.moveToNext()) {
            String item = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT));
            itemList.add(item);
        }
        cursor.close();
        adapter.notifyDataSetChanged();

        // Configurar o clique nos itens da lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = itemList.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(ExcluidosActivity.this);
                builder.setTitle("Editar Item");

                final EditText editText = new EditText(ExcluidosActivity.this);
                editText.setText(selectedItem);
                builder.setView(editText);

                builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String editedItem = editText.getText().toString();
                        itemList.set(position, editedItem);
                        adapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("Cancelar", null);

                builder.setNeutralButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        itemList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
