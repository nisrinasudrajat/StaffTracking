package com.pkl.stafftracking.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pkl.stafftracking.Model.Pulang;
import com.pkl.stafftracking.R;
import java.util.List;

public class PulangAdapter extends RecyclerView.Adapter<PulangAdapter.PulangViewHolder> {

    private Context mCtx;
    private List<Pulang> pulangList;

    public PulangAdapter(Context mCtx, List<Pulang> pulangList) {
        this.mCtx = mCtx;
        this.pulangList = pulangList;
    }


    @Override
    public PulangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_item_laporan, null);
        return new PulangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PulangViewHolder holder, int position) {
        Pulang pulang = pulangList.get(position);

        holder.textHari.setText(pulang.getHari());
        holder.textTanggal.setText(pulang.getTanggal());
        holder.textJam.setText(pulang.getJam());
        holder.textNama.setText(pulang.getNama());
    }

    @Override
    public int getItemCount() {
        return pulangList.size();
    }

    public class PulangViewHolder extends RecyclerView.ViewHolder {

        TextView textHari, textTanggal, textJam, textNama;

        public PulangViewHolder(View itemView) {
            super(itemView);

                    textHari = itemView.findViewById(R.id.laporan_hari_masuk);
                    textTanggal = itemView.findViewById(R.id.laporan_tanggal_masuk);
                    textJam = itemView.findViewById(R.id.laporan_waktu_masuk);
                    textNama = itemView.findViewById(R.id.laporan_nama);
        }
    }
}
