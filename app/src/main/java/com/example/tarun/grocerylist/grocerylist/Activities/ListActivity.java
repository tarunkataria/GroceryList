package com.example.tarun.grocerylist.grocerylist.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.tarun.grocerylist.grocerylist.Data.DBHelper;
import com.example.tarun.grocerylist.grocerylist.Model.GroceryItem;
import com.example.tarun.grocerylist.grocerylist.R;
import com.example.tarun.grocerylist.grocerylist.UI.GroceryListReViAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GroceryListReViAdapter groceryAdapter;
    private List<GroceryItem> groceryItemList;
    //private List<GroceryItem> fromattedGroceryList;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        dbHelper = new DBHelper(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //get grocery items from database
        groceryItemList = new ArrayList<>();
        groceryItemList = dbHelper.getAllGroceryItems();

        /*fromattedGroceryList = new ArrayList<>();
        for(GroceryItem c : groceryItemList){
            GroceryItem item = new GroceryItem();
            item.setName(c.getName());
            item.setQuantity("Qty: " + c.getQuantity());
            item.setId(c.getId());
            item.setDateItemAdded("Added on: " + c.getDateItemAdded());

            fromattedGroceryList.add(item);
        }*/

        //We could have directly passed the list that we got from the DB like below and it would work...
        //groceryAdapter = new GroceryListReViAdapter(this, groceryItemList);

        //But as we have to format (QTY:, Added on:) the data before we show it
        //so we created another list inside the for loop above...
        //passing the formatted list in the adapter now
        //groceryAdapter = new GroceryListReViAdapter(this, fromattedGroceryList);

        //Moving the formatting part to the adapter where we SET the text is better design cos we don't have to
        //create 2 arrays. Also in the Adapter code we will have the raw data from the DB not some formatted data.
        groceryAdapter = new GroceryListReViAdapter(this, groceryItemList);

        recyclerView.setAdapter(groceryAdapter);

        groceryAdapter.notifyDataSetChanged(); //I DON'T KNOW WHY WE HAVE TO DO THIS???????????

    }

}
