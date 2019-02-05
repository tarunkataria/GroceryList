package com.example.tarun.grocerylist.grocerylist.Activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tarun.grocerylist.grocerylist.Data.DBHelper;
import com.example.tarun.grocerylist.grocerylist.Model.GroceryItem;
import com.example.tarun.grocerylist.grocerylist.R;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText groceryItem;
    private EditText quantity;
    private Button saveBtn;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        //check if there are already items in database
        byPassActivity();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                createPopupDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createPopupDialog(){
        dialogBuilder = new AlertDialog.Builder(this);

        //load the popup xml in view object
        View view = getLayoutInflater().inflate(R.layout.popup,null);

        groceryItem = view.findViewById(R.id.groceryItem);
        quantity = view.findViewById(R.id.groceryQty);
        saveBtn = view.findViewById(R.id.savebutton);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: Save to DB
                //Todo: Go to next screen
                if(!groceryItem.getText().toString().isEmpty() &&
                   !quantity.getText().toString().isEmpty()) {
                    saveGroceryItemtoDB(v);
                }else{
                    Toast.makeText(MainActivity.this, "Please enter name and quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveGroceryItemtoDB(View v) {
        GroceryItem item = new GroceryItem();
        item.setName(groceryItem.getText().toString());
        item.setQuantity(quantity.getText().toString());

        dbHelper.addGroceryItem(item);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss(); //close the add item popup dialog

                //start new activity which will show the list.
                startActivity(new Intent(MainActivity.this,ListActivity.class));
            }
        }, 1000);

    }

    private void byPassActivity(){
        //checks if database is empty, if not then we just
        //go to ListActivity and show all added items
        if(dbHelper.getGroceryListCount() > 0){
            startActivity(new Intent(MainActivity.this, ListActivity.class));
        }
    }
}
