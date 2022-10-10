package com.example.grocery002;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ExpandAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final List<String> headerData;
    private final HashMap<String, List<String>> childData;
    public ListHolder[] listHolders;
    String pastColor = "#ff6817";
    String currentColor = "#54ff13";

    public ExpandAdapter(Context c, List<String> headerData, HashMap<String, List<String>> childData) {
        this.context = c;
        this.headerData = headerData;
        this.childData = childData;
        listHolders = new ListHolder[headerData.size()];
    }

    @Override //returns the given grocery item child view of the given grocery list
    public Object getChild(int groupPosition, int childPosition) {return this.childData.get(this.headerData.get(groupPosition)).get(childPosition);}

    @Override //returns the child position which maps the id one to one with there position (easier to use than arbitrary id scheme)
    public long getChildId(int groupPosition, int childPosition) {return childPosition;}

    @Override //returns the grocery item for the parent grocery list
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition); //gets the text component of the child
        if(convertView == null) { //if there is no existing child
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null); //set the view based on the list_item.xml
        }
        TextView childTxt = convertView.findViewById(R.id.ChildText); //get the TextView
        childTxt.setText(childText); //set the text of the child
        return convertView;
    }

    @Override //returns the count of grocery items to a given list
    public int getChildrenCount(int groupPosition) {return this.childData.get(this.headerData.get(groupPosition)).size();}

    @Override //returns the grocery list header item
    public Object getGroup(int groupPosition) {
        return this.headerData.get(groupPosition);
    }

    @Override // returns the count of grocery lists
    public int getGroupCount() {
        return this.headerData.size();
    }

    @Override //returns the position of the grocery list (id is mapped to position 1:1)
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override //gets the grocery list component
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition); //gets the text component
        ListHolder listHolder;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null); //sets the view based on the list_group.xml

            if(GetColor(headerTitle)) {convertView.setBackgroundColor(Color.parseColor(currentColor));} //set color green if after current date
            else {convertView.setBackgroundColor(Color.parseColor(pastColor));} //set red if data is before current data

            //set the listHolder variables
            listHolder = new ListHolder();
            listHolder.checkBox = convertView.findViewById(R.id.CheckBox);
            listHolder.textView = convertView.findViewById(R.id.textView); //use in listView instance
            listHolder.listId = headerTitle.substring(0, headerTitle.indexOf("|")-1); //uses the | character to find the id, -1 as there is a space
            listHolders[groupPosition] = listHolder; //set this listHolder in the array mapped to its position onscreen
        } else {listHolder = (ListHolder) convertView.getTag();}
        TextView headerTxt = (TextView) convertView.findViewById(R.id.textView); //use in convertView instance
        headerTxt.setTypeface(null, Typeface.BOLD); //have the title be bold
        headerTxt.setText(headerTitle); //set the text
        return convertView;
    }

    public boolean GetColor(String text) { //returns the color according the set date vs current date
        try {
            String date = text.substring(text.indexOf("|") + 1, text.lastIndexOf("|")); //the date is located between the first and last |
            date = date.replaceAll("\\s", ""); //remove any whitespace
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); //format as date (data validation this will work)
            Date listDate = format.parse(date); //format from string to date
            Date currentDate = new Date(); //default constructor uses system current date
            return !currentDate.after(listDate); //if the current date is after list date -> (true) then negate (false) -> false = red
        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "USER, YOU ARE NOT SEEING THIS", Toast.LENGTH_LONG).show(); //this won't ever run, date formatting has to be in a try catch structure
            return false;
        }
    }

    @Override //the data mapped to id changes based on changing grocery list and grocery items, so always return false
    public boolean hasStableIds() {return false;}

    @Override //all children are always selectable, always return true
    public boolean isChildSelectable(int groupPosition, int childPosition) {return true;}

    public static class ListHolder { //List holder object, easy to use passable data store
        String listId;
        CheckBox checkBox;
        TextView textView;
    }

    public ListHolder[] GetListHolders(){return listHolders;} //returns the list holder array
}
