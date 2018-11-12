package com.example.android.carrobluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SimplesAdapter extends BaseAdapter {

    public ArrayList<BluetoothDevice> getLista() {
        return lista;
    }

    private ArrayList<BluetoothDevice> lista = new ArrayList<BluetoothDevice>();
    private Context context;

    public SimplesAdapter(Context context){
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String device = lista.get(position).getName() + " " + lista.get(position).getAddress();
        View view = LayoutInflater.from(context).inflate(R.layout.simple_adapter, parent, false);

        TextView t = (TextView) view.findViewById(R.id.textinho);
        t.setText(device);

        return view;
    }

    public void addDevice(BluetoothDevice device){
        if(!lista.contains(device)) {
            lista.add(device);
            notifyDataSetChanged();
        }
    }

    public void addDevice(List<BluetoothDevice> device){
        for(BluetoothDevice tmpDevice : device)
            lista.add(tmpDevice);

        notifyDataSetChanged();
    }

    public void cleanAdapter(){
        lista.clear();
        notifyDataSetChanged();
    }

    public void addItem(BluetoothDevice item){
        lista.add(item);
    }

    public Context getContext() {
        return context;
    }
}
