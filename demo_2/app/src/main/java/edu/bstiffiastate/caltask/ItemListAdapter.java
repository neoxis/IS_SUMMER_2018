package edu.bstiffiastate.caltask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ItemListAdapter extends BaseAdapter
{
    private LocalDBAdapter helper;
    private FirebaseDatabase database;
    private ArrayList<MainActivity.TEI_Object> items;

    ItemListAdapter(ArrayList<MainActivity.TEI_Object> objects)
    {
        database = FirebaseDatabase.getInstance();
        items = objects;
        helper = new LocalDBAdapter(MainActivity.getAppContext());
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int i) { return items.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup vg) {
        final MainActivity.TEI_Object cur = (MainActivity.TEI_Object) getItem(i);
        ItemViewHolder viewHolder;

        if(view == null)
        {
            view = LayoutInflater.from(MainActivity.getAppContext()).
                    inflate(R.layout.grocery_list_item, vg, false);
            viewHolder = new ItemViewHolder(view);
            view.setTag(viewHolder);
        }
        else viewHolder = (ItemViewHolder)view.getTag();

        viewHolder.item_title.setText(cur.getTitle());

        if(cur.getId().startsWith("-")) //public
        {
            viewHolder.item_title.setTextColor(Color.parseColor("#FF4081"));
            viewHolder.item_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),cur.getTitle(),Toast.LENGTH_LONG).show();
                }
            });
            viewHolder.item_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference ref = database.getReference("objects").child(helper.getAccountID()+"-table").child(cur.getId());
                    ref.removeValue();
                    MainActivity.getF_items().remove(cur);
                    updateItemLists();
                }
            });
        }
        else
        {
            viewHolder.item_title.setTextColor(viewHolder.c);
            //delete private item
            viewHolder.item_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),cur.getTitle(),Toast.LENGTH_LONG).show();
                }
            });

            viewHolder.item_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    helper.deleteObject(cur.getId());
                    updateItemLists();
                }
            });
        }
        return view;
    }

    //updates listview upon object entry
    public void updateItems(ArrayList<MainActivity.TEI_Object> update)
    {
        items = update;
        notifyDataSetChanged();
    }

    private void editItem(final String i_id, String title)
    {
        //create objects
        LinearLayout l = new LinearLayout(MainActivity.getAppContext());
        l.setOrientation(LinearLayout.VERTICAL);

        final EditText item_title = new EditText(MainActivity.getAppContext());
        item_title.setText(title);
        item_title.setHint("item");
        item_title.setMaxLines(1);
        item_title.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

        l.addView(item_title);

        final AlertDialog d = new AlertDialog.Builder(MainActivity.getAppContext())
                .setTitle("Edit Item")
                .setView(l)
                .setPositiveButton("edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int id = helper.updateItem(i_id,item_title.getText().toString());
                        if(id <= 0) Toast.makeText(MainActivity.getAppContext(),"Update Failed",Toast.LENGTH_LONG).show();
                        else
                        {
                            ListsActivity.adapter.updateItems(helper.get_objects("item"));
                        }
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        d.show();

    }

    public void updateItemLists()
    {
        ArrayList<MainActivity.TEI_Object> list = helper.get_objects("item");
        list.addAll(MainActivity.getF_items());
        ListsActivity.adapter.updateItems(list);
    }

    //holds item view object attributes
    private class ItemViewHolder
    {
        TextView item_title;
        ImageButton item_done;
        ColorStateList c;

        ItemViewHolder(View view)
        {
            item_title = view.findViewById(R.id.grocery_item);
            item_done = view.findViewById(R.id.got_item);
            c = item_title.getTextColors();
        }
    }
}
