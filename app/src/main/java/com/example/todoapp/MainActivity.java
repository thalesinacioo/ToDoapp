package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import android.widget.CheckBox;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button addButton;
    private ListView listView;
    private ArrayList<String> itemList;
    private ArrayAdapter<String> adapter;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public void addItemToList(View view) {
        String item = editText.getText().toString();

        if (!item.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CONTENT, item);

            long id = database.insert(DatabaseHelper.TABLE_NAME, null, values);
            if (id != -1) {
                itemList.add(item);
                adapter.notifyDataSetChanged();
                editText.getText().clear();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referenciando os componentes do layout
        editText = findViewById(R.id.editText);
        addButton = findViewById(R.id.button);
        listView = findViewById(R.id.listView);

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir a nova tela (ExcluidosActivity)
                Intent intent = new Intent(MainActivity.this, ExcluidosActivity.class);
                startActivity(intent);
            }
        });

        // Criando a lista de itens
        itemList = new ArrayList<>();

        // Criando o adaptador para a lista
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.text_item, itemList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                CheckBox checkBox = view.findViewById(R.id.checkbox_item);
                checkBox.setChecked(false);

                // Ouvindo o clique para o CheckBox
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Obtém o item selecionado
                        String selectedItem = itemList.get(position);

                        // Exibe o diálogo de edição
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Editar Item");

                        final EditText editText = new EditText(MainActivity.this);
                        editText.setText(selectedItem);
                        builder.setView(editText);

                        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Obtém o novo conteúdo do item editado
                                String editedItem = editText.getText().toString();

                                // Atualiza o conteúdo do item na lista
                                itemList.set(position, editedItem);

                                // Notifica o adaptador sobre a mudança
                                adapter.notifyDataSetChanged();
                            }
                        });

                        builder.setNegativeButton("Cancelar", null);

                        // Botão de Excluir
                        builder.setNeutralButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Remove o item da lista
                                itemList.remove(position);

                                // Atualiza o item no banco de dados marcando como excluído
                                ContentValues updateValues = new ContentValues();
                                updateValues.put(DatabaseHelper.COLUMN_EXCLUIDO, 1); // Marca como excluído

                                int rowsAffected = database.update(DatabaseHelper.TABLE_NAME, updateValues,
                                        DatabaseHelper.COLUMN_CONTENT + " = ?", new String[]{selectedItem});

                                if (rowsAffected > 0) {

                                    // Notifica o adaptador sobre a mudança
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });

                return view;
            }
        };

        // Associando o adaptador à ListView
        listView.setAdapter(adapter);

        // Configurando o clique do botão de adicionar
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtendo o texto do EditText
                String item = editText.getText().toString();

                // Verificando se o texto não está vazio
                if (!item.isEmpty()) {
                    // Adicionando o item à lista
                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_CONTENT, item);

                    long id = database.insert(DatabaseHelper.TABLE_NAME, null, values);
                    if (id != -1) {
                        itemList.add(item);

                        // Notificando o adaptador que os dados foram alterados
                        adapter.notifyDataSetChanged();

                        // Limpando o EditText
                        editText.getText().clear();
                    }
                }
            }
        });

        // Configurando o clique do item na lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Verifica se o clique foi no CheckBox
                if (view.findViewById(R.id.checkbox_item).isPressed()) {
                    // Se o clique foi no CheckBox, não faz nada
                    return;
                }
                // Obtém o item selecionado
                String selectedItem = itemList.get(position);

                // Exibe o diálogo de edição
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Editar Item");

                final EditText editText = new EditText(MainActivity.this);
                editText.setText(selectedItem);
                builder.setView(editText);

                builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtém o novo conteúdo do item editado
                        String editedItem = editText.getText().toString();

                        // Atualiza o conteúdo do item na lista
                        itemList.set(position, editedItem);

                        // Notifica o adaptador sobre a mudança
                        adapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("Cancelar", null);

                // Botão de Excluir
                builder.setNeutralButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove o item da lista
                        itemList.remove(position);

                        // Atualiza o item no banco de dados marcando como excluído
                        ContentValues updateValues = new ContentValues();
                        updateValues.put(DatabaseHelper.COLUMN_EXCLUIDO, 1); // Marca como excluído

                        int rowsAffected = database.update(DatabaseHelper.TABLE_NAME, updateValues,
                                DatabaseHelper.COLUMN_CONTENT + " = ?", new String[]{selectedItem});

                        if (rowsAffected > 0) {
                            // Notifica o adaptador sobre a mudança
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        // Inicializando o DatabaseHelper e o SQLiteDatabase
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();

        // Recuperando os itens salvos do banco de dados
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, null, DatabaseHelper.COLUMN_EXCLUIDO + "=0", null, null, null, null);
        while (cursor.moveToNext()) {
            String item = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT));
            itemList.add(item);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
