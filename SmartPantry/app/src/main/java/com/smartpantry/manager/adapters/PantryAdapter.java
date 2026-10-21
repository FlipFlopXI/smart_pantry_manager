package com.smartpantry.manager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.smartpantry.manager.R;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.utils.UnitConverter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.Holder> {
    public interface Actions { void edit(PantryItem item); void delete(PantryItem item); }
    private final List<PantryItem> items=new ArrayList<>();
    private final Actions actions;
    private boolean expiryIndicators;
    public PantryAdapter(Actions actions) { this.actions=actions; setHasStableIds(true); }
    public void setItems(List<PantryItem> values,boolean indicators) { items.clear(); items.addAll(values); expiryIndicators=indicators; notifyDataSetChanged(); }
    @Override public long getItemId(int position) { return items.get(position).id; }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent,int type) { return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pantry,parent,false)); }
    @Override public void onBindViewHolder(@NonNull Holder holder,int position) {
        PantryItem item=items.get(position); holder.name.setText(item.name);
        String meta=UnitConverter.format(item.quantity)+" "+item.unit;
        if(item.expiryDate!=null) {
            meta+="  •  Expires "+item.expiryDate;
            if(expiryIndicators) try { LocalDate date=LocalDate.parse(item.expiryDate), today=LocalDate.now(); if(!date.isBefore(today) && !date.isAfter(today.plusDays(3))) meta+="  ⚠ Soon"; } catch(Exception ignored) {}
        }
        holder.meta.setText(meta); holder.edit.setOnClickListener(v->actions.edit(item)); holder.delete.setOnClickListener(v->actions.delete(item));
        holder.itemView.setAlpha(1f);
    }
    @Override public int getItemCount() { return items.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        final TextView name,meta; final View edit,delete;
        Holder(View view) { super(view); name=view.findViewById(R.id.name); meta=view.findViewById(R.id.meta); edit=view.findViewById(R.id.edit); delete=view.findViewById(R.id.delete); }
    }
}
