package edu.bstiffiastate.firebase_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemListAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<MainActivity.TEI_Object> items;

    public ItemListAdapter(Context context, ArrayList<MainActivity.TEI_Object> objects)
    { mContext = context; items = objects; }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int i) { return items.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup vg) {
        MainActivity.TEI_Object cur = (MainActivity.TEI_Object) getItem(i);
        ItemViewHolder viewHolder;

        if(view == null)
        {
            view = LayoutInflater.from(mContext).
                    inflate(R.layout.grocery_list_item, vg, false);
            viewHolder = new ItemViewHolder(view);
            view.setTag(viewHolder);
        }
        else viewHolder = (ItemViewHolder)view.getTag();

        viewHolder.item_title.setText(cur.getTitle());

        //todo
        //set button listener here

        return view;
    }

    //updates listview upon object entry
    public void updateItems(ArrayList<MainActivity.TEI_Object> update)
    {
        items = update;
        notifyDataSetChanged();
    }

    //holds item view object attributes
    private class ItemViewHolder
    {
        TextView item_title;
        ImageButton item_done;

        public ItemViewHolder(View view)
        {
            item_title = (TextView)view.findViewById(R.id.grocery_item);
            item_done = (ImageButton)view.findViewById(R.id.got_item);
        }
    }
}
