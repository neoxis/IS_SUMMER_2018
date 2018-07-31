package edu.bstiffiastate.demo_3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListsItemListAdapter extends BaseAdapter
{
    FirebaseDatabase database;
    LocalDBAdapter dbAdapter;
    ArrayList<MainActivity.TEI_Object> items;
    ListsActivity la;
    Context context;

    ListsItemListAdapter(Context context, ListsActivity la, ArrayList<MainActivity.TEI_Object> items)
    {
        this.context = context;
        this.la = la;
        this.items = items;
        database = FirebaseDatabase.getInstance();
        dbAdapter = new LocalDBAdapter(context);
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int i) { return items.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    //todo move delete to own method
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemViewHolder viewHolder;
        if(view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.grocery_list_item,viewGroup,false);
            viewHolder = new ItemViewHolder(view);
            view.setTag(viewHolder);
        }
        else viewHolder = (ItemViewHolder) view.getTag();

        final MainActivity.TEI_Object cur = (MainActivity.TEI_Object) getItem(i);

        viewHolder.item_title.setText(cur.getTitle());

        if(cur.getId().startsWith("-")) //public
        {
            viewHolder.item_title.setTextColor(Color.parseColor("#FF4081"));
            viewHolder.item_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference ref = database.getReference("objects").child(dbAdapter.get_account_ID()+"-table").child(cur.getId());
                    ref.removeValue();
                    la.f_items.remove(cur);
                    la.update_lists_items();
                }
            });
        }
        else //private
        {
            viewHolder.item_title.setTextColor(viewHolder.c);
            viewHolder.item_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbAdapter.delete_object(cur.getId());
                    la.update_lists_items();
                }
            });
        }

        viewHolder.item_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_item(cur.getId(),cur.getTitle());
            }
        });
        return view;
    }

    public void updateItems(ArrayList<MainActivity.TEI_Object> update)
    {
        items = update;
        notifyDataSetChanged();
    }

    //todo delete object upon empty fields
    private void edit_item(final String item_id, String title)
    {
        final EditText edit_t_title = edit_title(title);

        AlertDialog d = new AlertDialog.Builder(context)
                .setTitle("edit item")
                .setView(edit_t_title)
                .setPositiveButton("edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(item_id.startsWith("-"))
                        {
                            DatabaseReference ref = database.getReference("objects").child(dbAdapter.get_account_ID()+"-table").child(item_id).child("title");
                            ref.setValue(edit_t_title.getText().toString());
                            la.update_lists_items();
                        }
                        else
                        {
                            int id = dbAdapter.updateItem(item_id,edit_t_title.getText().toString());
                            if(id <= 0) Toast.makeText(context,"Private: Update Failed", Toast.LENGTH_LONG).show();
                            else la.update_lists_items();
                        }
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        d.show();
    }

    private EditText edit_title(String text)
    {
        EditText temp = new EditText(context);
        temp.setHint("title");
        temp.setText(text);
        temp.setMaxLines(1);
        temp.setSelection(text.length());
        return temp;
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
