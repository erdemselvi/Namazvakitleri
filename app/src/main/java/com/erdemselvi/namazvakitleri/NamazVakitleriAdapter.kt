package com.erdemselvi.namazvakitleri

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.erdemselvi.namazvakitleri.database.Vakitler
import com.squareup.picasso.Picasso

class NamazVakitleriAdapter(private val mContext: Context, private val vakitler: ArrayList<Vakitler>)
    :RecyclerView.Adapter<NamazVakitleriAdapter.VakitTutucu>() {
    inner class VakitTutucu(tasarim:View):RecyclerView.ViewHolder(tasarim) {
        var imsak:TextView
        var gunes:TextView
        var ogle:TextView
        var ikindi:TextView
        var aksam:TextView
        var yatsi:TextView
        var tarih:TextView
        var hicriTarih:TextView
        var ayGorunum: ImageView
        init {
            imsak=tasarim.findViewById(R.id.tvImsak)
            gunes=tasarim.findViewById(R.id.tvGunes)
            ogle=tasarim.findViewById(R.id.tvOgle)
            ikindi=tasarim.findViewById(R.id.tvIkindi)
            aksam=tasarim.findViewById(R.id.tvAksam)
            yatsi=tasarim.findViewById(R.id.tvYatsi)
            tarih=tasarim.findViewById(R.id.tvTarih)
            hicriTarih=tasarim.findViewById(R.id.tvHicriTarih)
            ayGorunum=tasarim.findViewById(R.id.ivAyGorunum)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VakitTutucu {
        val tasarim=LayoutInflater.from(mContext).inflate(R.layout.aylik_vakitler_tasarim,parent,false)
        return VakitTutucu(tasarim)
    }
    override fun getItemCount(): Int {
        return vakitler.size
    }

    override fun onBindViewHolder(holder: VakitTutucu, position: Int) {

        val vakit=vakitler.get(position)

        holder.tarih.text=vakit.miladiTarih
        holder.hicriTarih.text=vakit.hicriTarih
        holder.imsak.text=vakit.imsak
        holder.gunes.text=vakit.gunes
        holder.ogle.text=vakit.ogle
        holder.ikindi.text=vakit.ikindi
        holder.aksam.text=vakit.aksam
        holder.yatsi.text=vakit.yatsi
        Picasso.with(mContext)
            .load(vakit.ayUrl)

            .resize(150, 150)         //optional
            .centerCrop()                        //optional
            .into(holder.ayGorunum);

    }



}