package com.example.tarun.grocerylist.grocerylist.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.example.tarun.grocerylist.grocerylist.Model.GroceryItem;
import com.example.tarun.grocerylist.grocerylist.Util.Constants;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;

    public DBHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.context = context;
    }

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table
        String cmd = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE_NAME + "(" +
                    Constants.KEY_ID + " INTEGER PRIMARY KEY," +
                    Constants.KEY_GROCERY_ITEM + " TEXT," +
                    Constants.KEY_QUANTITY + " TEXT," +
                    Constants.KEY_DATE_ADDED + " LONG);";

        db.execSQL(cmd);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop the table
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        //create new table
        onCreate(db);

    }

    /*
    CRUD operations
     */

    //Add grocery Item
    public void addGroceryItem(GroceryItem groceryItem){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_GROCERY_ITEM, groceryItem.getName());
        values.put(Constants.KEY_QUANTITY,groceryItem.getQuantity());
        values.put(Constants.KEY_DATE_ADDED,System.currentTimeMillis());

        db.insert(Constants.TABLE_NAME, null, values);
    }

    public int deleteGroceryItem(GroceryItem groceryItem){
        SQLiteDatabase db = this.getWritableDatabase();

        String condition = Constants.KEY_GROCERY_ITEM + "=?";
        String[] args = new String[]{groceryItem.getName()};

        int result = db.delete(Constants.TABLE_NAME, condition, args );
        return result;
    }

    public int deleteGroceryItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        String condition = Constants.KEY_ID + "=?";
        String[] args = new String[]{String.valueOf(id)};

        int result = db.delete(Constants.TABLE_NAME, condition, args );
        return result;
    }

    public GroceryItem getGroceryItem(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columnsToSelect = new String[]{Constants.KEY_ID, Constants.KEY_GROCERY_ITEM,
                                                Constants.KEY_QUANTITY, Constants.KEY_DATE_ADDED};
        String selectCondition = Constants.KEY_ID + "=?";
        String[] inputArgs = new String[]{String.valueOf(id)};

        //USING "query()" METHOD
        Cursor cursor = db.query(Constants.TABLE_NAME, columnsToSelect,selectCondition,inputArgs,
                                null,null,null );

        //USING "rawQuery()" METHOD
        Cursor crs = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME + " Where " + Constants.KEY_ID + "=" + String.valueOf(id), null);

        //OR
        Cursor cur = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME, new String[]{Constants.KEY_ID + "=?"});

        if(cursor.moveToFirst()){ //Don't test cursor != null  as cursor will never be null. cursor.getCount is inefficient.

            //CREATE the GroceryItem by using constructor OR use Setter methods (look at getAllGroceryItems()).....
            /*GroceryItem groceryItem = new GroceryItem(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)),
                                                      cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)),
                                                      cursor.getString(cursor.getColumnIndex(Constants.KEY_QUANTITY)),
                                                      cursor.getString(cursor.getColumnIndex(Constants.KEY_DATE_ADDED)));*/

            //If all the values that we are getting would have been strings then we would have used above style to create the object.
            //As we have to format the date so creating by using constructor is not a good idea...lets use setter
            GroceryItem groceryItem = new GroceryItem();
            groceryItem.setId(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
            groceryItem.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
            groceryItem.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_QUANTITY)));

            //Convert timestamp to something more readable
            DateFormat dateFormat = DateFormat.getDateInstance();
            String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_ADDED)))); //.getTime()

            groceryItem.setDateItemAdded(formattedDate);

            db.close();
            return groceryItem;
        }

        db.close();
        return null;

    }

    public List<GroceryItem> getAllGroceryItems(){
        SQLiteDatabase db = this.getReadableDatabase();

        String cmd = "SELECT * FROM " + Constants.TABLE_NAME;

        Cursor cursor = db.rawQuery(cmd,null);

        List<GroceryItem> groceryItemList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                GroceryItem item = new GroceryItem();
                item.setId(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
                item.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_QUANTITY)));

                //Convert timestamp to something more readable
                DateFormat dateFormat = DateFormat.getDateInstance();
                String formatedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_ADDED)))); //.getTime()

                item.setDateItemAdded(formatedDate);

                //add item to the list
                groceryItemList.add(item);

            }while(cursor.moveToNext());
        }
        db.close();
        return groceryItemList;
    }

    public int updateGroceryItem(GroceryItem groceryItem){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_GROCERY_ITEM,groceryItem.getName());
        values.put(Constants.KEY_QUANTITY,groceryItem.getQuantity());

        int result = db.update(Constants.TABLE_NAME,values,Constants.KEY_ID + "=?",
                                new String[]{String.valueOf(groceryItem.getId())});
        return result;
    }

    public int getGroceryListCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int)DatabaseUtils.queryNumEntries(db,Constants.TABLE_NAME);
    }
}
