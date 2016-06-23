package com.example.bbirincioglu.prisonersdilemma;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Custom Adapter class for storing and displaying Bluetooth Devices that are discovered or paired.
 */
public class DiscoveryListAdapter extends ArrayAdapter<BluetoothDevice> {
    private ArrayList<BluetoothDevice> devices;
    private Context context;

    public DiscoveryListAdapter(Context context, int rowResourceID, ArrayList<BluetoothDevice> devices) {
        super(context, rowResourceID, devices);
        this.devices = devices;
        this.context = context;
    }

    //Each row in the instance of DiscoveryListDialog is obtained via this method. We inflate into DiscoveryListDialog
    //and construct each row dynamically.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.discovery_list_row, parent, false);

        TextView rowTextView = (TextView) row.findViewById(R.id.discoveryListRowTextView);
        CheckBox checkBox = (CheckBox) row.findViewById(R.id.discoveryListRowCheckBox);
        checkBox.setOnClickListener(new CheckBoxListener());

        if (position == 0) {
            checkBox.setChecked(true);
        }

        checkBox.setTag(R.id.discoveryListRowCheckBox, getDevices().get(position));

        BluetoothDevice device = getDevices().get(position);
        String name = device.getName();
        int bondState = device.getBondState();
        String address = device.getAddress();

        rowTextView.setText(name + bondState + address);
        return row;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<BluetoothDevice> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<BluetoothDevice> devices) {
        this.devices = devices;
    }

    //CheckBox listener for selecting to which device we connect.
    private class CheckBoxListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            System.out.println("IN THE ONCLICK");
            CheckBox checkBox = (CheckBox) v;
            boolean isChecked = checkBox.isChecked();

            if (isChecked) {
                ListView listView = findListView(checkBox);
                int childCount = listView.getChildCount();

                for (int i = 0; i < childCount; i++) {
                    View row = listView.getChildAt(i);
                    ((CheckBox) row.findViewById(R.id.discoveryListRowCheckBox)).setChecked(false);
                }
            }

            checkBox.setChecked(true);
        }
    }

    private ListView findListView(View view) {
        ListView wanted = null;
        ViewParent parent = view.getParent();

        while (!(parent instanceof ListView)) {
            parent = parent.getParent();
        }

        wanted = (ListView) parent;
        return wanted;
    }
}
