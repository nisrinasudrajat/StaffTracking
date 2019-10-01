package com.pkl.stafftracking.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pkl.stafftracking.Model.Masuk;
import com.pkl.stafftracking.Model.Pulang;
import com.pkl.stafftracking.R;
import java.util.List;

public class MasukAdapter  extends RecyclerView.Adapter<MasukAdapter.MasukViewHolder> {

    private Context mCtx;
    private List<Masuk> masukList;

    public MasukAdapter(Context mCtx, List<Masuk> masukList) {
        this.mCtx = mCtx;
        this.masukList = masukList;
    }


    @Override
    public MasukAdapter.MasukViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_item_laporan_masuk, null);
        return new MasukAdapter.MasukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MasukAdapter.MasukViewHolder holder, int position) {
        Masuk masuk = masukList.get(position);

        holder.textHari.setText(masuk.getHari());
        holder.textTanggal.setText(masuk.getTanggal());
        holder.textJam.setText(masuk.getJam());
        holder.textNama.setText(masuk.getNama());
    }

    @Override
    public int getItemCount() {
        return masukList.size();
    }

    public class MasukViewHolder extends RecyclerView.ViewHolder {

        TextView textHari, textTanggal, textJam, textNama;

        public MasukViewHolder(View itemView) {
            super(itemView);

            textHari = itemView.findViewById(R.id.laporan_hari_masuk);
            textTanggal = itemView.findViewById(R.id.laporan_tanggal_masuk);
            textJam = itemView.findViewById(R.id.laporan_waktu_masuk);
            textNama = itemView.findViewById(R.id.laporan_nama);
        }
    }
}
