package edu.bstiffiastate.caltask;

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
    private LocalDBAdapter helper;
    private Context mContext;
    private ArrayList<MainActivity.TEI_Object> items;

    public ItemListAdapter(Context context, ArrayList<MainActivity.TEI_Object> objects)
    { mContext = context; items = objects; helper = new LocalDBAdapter(context);}

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
        viewHolder.item_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View p = (View) view.getParent();
                TextView t = p.findViewById(R.id.grocery_item);
                helper.deleteItem(t.getText().toString());
                updateItems(helper.get_objects("item"));
            }
        });

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
            item_title = view.findViewById(R.id.grocery_item);
            item_done = view.findViewById(R.id.got_item);
        }
    }
}
