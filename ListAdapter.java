package com.example.grocery002;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;


public class ListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;
    private final ViewHolder[] viewHolders;

    public ListAdapter(Context context, ArrayList<String> v) { //constructor
        super(context, R.layout.row_layout, v);
        this.values = v;
        this.context = context;
        viewHolders = new ViewHolder[v.size()];
    }


    @Override //gets the grocery items as list views
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;
        if(convertView == null) { //if a view currently doesn't exist
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_layout, null); //set the view based on the row_layout.xml
            viewHolder = new ViewHolder();
            //set viewHolder data
            viewHolder.image = convertView.findViewById(R.id.ImageView);
            viewHolder.text = convertView.findViewById(R.id.TextView);
            viewHolder.checkBox = convertView.findViewById(R.id.CheckBox);
            viewHolders[position] = viewHolder; //set the viewHolder to the viewHolder array
            convertView.setTag(viewHolder);
        } else {viewHolder = (ViewHolder) convertView.getTag();}
        try{
            String record = values.get(position).replaceAll("\\s", ""); //remove all whitespaces
            String[] recordElem = record.split(","); //split in elements separated by ,
            if(recordElem[3] != null) {viewHolder.image.setImageURI(Uri.parse(recordElem[3]));} //if there is some image link,
            else{viewHolder.image.setImageResource(R.drawable.no_image);}
        } catch(Exception e){
            viewHolder.image.setImageResource(R.drawable.no_image);
            e.printStackTrace();
        }
        String textStr = values.get(position).substring(0, values.get(position).lastIndexOf(","));
        viewHolder.text.setText(textStr);
        viewHolder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> viewHolders[position].isChecked = b);
        return convertView;
    }

    public static class ViewHolder {
        ImageView image;
        TextView text;
        CheckBox checkBox;
        boolean isChecked;
    }

    public ViewHolder[] GetViewHolders() {return viewHolders;}
    public void UncheckAll() {
        for (ViewHolder viewHolder : viewHolders) { //for each loop (all viewHolders)
            viewHolder.checkBox.setChecked(false); //sets the checkbox to false
        }
    }


}
