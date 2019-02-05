package com.example.tarun.grocerylist.grocerylist.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tarun.grocerylist.grocerylist.Activities.DetailsActivity;
import com.example.tarun.grocerylist.grocerylist.Data.DBHelper;
import com.example.tarun.grocerylist.grocerylist.Model.GroceryItem;
import com.example.tarun.grocerylist.grocerylist.R;
import com.example.tarun.grocerylist.grocerylist.Util.Constants;

import org.w3c.dom.Text;

import java.util.List;
import java.util.zip.Inflater;

public class GroceryListReViAdapter extends RecyclerView.Adapter<GroceryListReViAdapter.ViewHolder> {

    private Context context;
    private List<GroceryItem> groceryItems;

    public GroceryListReViAdapter(Context context, List<GroceryItem> groceryItems) {
        this.context = context;
        this.groceryItems = groceryItems; //this groceryItem list is pure from DB not formatted one. Thanks Tarun!
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GroceryItem groceryItem = groceryItems.get(position);

        //Do the formatting here, if needed, before we display it. Better design then doing formatting
        //in ListActivity and creating 2 arrayLists
        holder.groceryItem.setText(groceryItem.getName());
        holder.groceryQty.setText("Qty: " + groceryItem.getQuantity());
        holder.dateAdded.setText("Added on: " + groceryItem.getDateItemAdded());
    }

    @Override
    public int getItemCount() {

        return groceryItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView groceryItem;
        public TextView groceryQty;
        public TextView dateAdded;
        public Button btnEdit;
        public Button btnDelete;

        public int id;

        public ViewHolder(View view) {
            super(view);

            groceryItem = view.findViewById(R.id.txtGroceryName);
            groceryQty = view.findViewById(R.id.txtGroceryQty);
            dateAdded = view.findViewById(R.id.txtDateAdded);
            btnEdit = view.findViewById(R.id.editButton);
            btnDelete = view.findViewById(R.id.deleteButton);

            btnEdit.setOnClickListener(this);
            btnDelete.setOnClickListener(this);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to next screen meaning go to detailsActivity
                    int position = getAdapterPosition();
                    GroceryItem gItem = groceryItems.get(position);

                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("name", gItem.getName());
                    intent.putExtra("Quantity", gItem.getQuantity());
                    intent.putExtra("DateAdded", gItem.getDateItemAdded());
                    intent.putExtra("id",gItem.getId());

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.editButton:
                    //todo
                    int pos = getAdapterPosition();
                    GroceryItem grocery = groceryItems.get(pos);
                    updateItem(grocery);
                    break;
                case R.id.deleteButton:
                    //todo
                    int position = getAdapterPosition();
                    GroceryItem item = groceryItems.get(position); //we get the groceryItem object that needs to be deleted.
                    deleteItem(item.getId());
                    break;
            }

        }

        public void deleteItem(final int groceryId){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context).setTitle("Delete")
                                      .setMessage("Are you sure you want to Delete?")
                                      .setIcon(android.R.drawable.ic_delete)
                                      .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              //delete code
                                              DBHelper dbHelper = new DBHelper(context);
                                              dbHelper.deleteGroceryItem(groceryId);

                                              //to reflect the change on recycler view
                                              groceryItems.remove(getAdapterPosition());
                                              notifyItemRemoved(getAdapterPosition());

                                              dialog.dismiss();
                                          }
                                      })
                                      .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              dialog.dismiss();
                                          }
                                      });

            AlertDialog confirmDialog = dialogBuilder.create();

            confirmDialog.show();

        }

        public void updateItem(final GroceryItem grocery){

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

            //As we are not in an activity so we cannot call getLayoutInflater directly.
            //We need to get the LayoutInflater object from the context, which is ListActivity.
            //Inflater inflater = LayoutInflater.from(context);

            LayoutInflater layoutInflater = LayoutInflater.from(context);

            View view = layoutInflater.inflate(R.layout.popup, null);

            final EditText txtGroceryName = view.findViewById(R.id.groceryItem);
            final EditText txtGroceryQty = view.findViewById(R.id.groceryQty);
            Button saveBtn = view.findViewById(R.id.savebutton);

            //get the title of the popup and change it to reflect that its for Edit
            TextView txtTitle = view.findViewById(R.id.title);
            txtTitle.setText("Edit Grocery Item");

            //prepopulate the text fields with the grocery name and qty selected
            txtGroceryName.setText(grocery.getName());
            txtGroceryQty.setText(grocery.getQuantity());

            dialogBuilder.setView(view); //set view in the dialog builder
            //create the dialog...
            final AlertDialog updateDialog = dialogBuilder.create();
            updateDialog.show();

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!txtGroceryName.getText().toString().isEmpty() &&
                            !txtGroceryQty.getText().toString().isEmpty()){
                        DBHelper dbHelper = new DBHelper(context);
                        //the grocery object we get as argument needs to be updated...
                        grocery.setName(txtGroceryName.getText().toString());
                        grocery.setQuantity(txtGroceryQty.getText().toString());

                        //now pass this updated grocery object to the DB
                        dbHelper.updateGroceryItem(grocery);

                        //update the recycler view
                        notifyItemChanged(getAdapterPosition(),grocery); //pass the updated grocery object to display

                        //dismiss the dialog
                        updateDialog.dismiss();

                    }


                }
            });


        }

    }
}
