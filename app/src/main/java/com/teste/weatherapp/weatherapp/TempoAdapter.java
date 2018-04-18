package com.teste.weatherapp.weatherapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by pmaza on 17-Apr-18.
 */

public class TempoAdapter extends RecyclerView.Adapter<TempoAdapter.MyViewHolder> {

    private List<TempoModel> tempoLista;
    DateFormat tempoDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
    DateFormat displayDateFormat = new SimpleDateFormat("EEEE, dd '"+R.string.de+"' yyyy", Locale.US);

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView desc, minima, maxima;

        public MyViewHolder(View view) {
            super(view);
            desc = (TextView) view.findViewById(R.id.tempo_item_data);
            minima = (TextView) view.findViewById(R.id.tempo_item_minima);
            maxima = (TextView) view.findViewById(R.id.tempo_item_maxima);
        }
    }
    public TempoAdapter(List<TempoModel> tempoLista) {
        this.tempoLista = tempoLista;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tempo_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TempoModel tempo = tempoLista.get(position+1);
        try {
            Date data = tempoDateFormat.parse(tempo.getDate());
            holder.desc.setText(displayDateFormat.format(data));
            holder.maxima.setText(tempo.getHigh()+"°");
            holder.minima.setText(tempo.getLow()+"°");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return tempoLista.size()-1;
    }




}