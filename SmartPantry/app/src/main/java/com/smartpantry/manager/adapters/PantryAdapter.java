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
    @Override public void onBindViewHolder(@NonNull Holder holder,int position) {  //7. START.............................................................
        PantryItem item=items.get(position); holder.name.setText(item.name);
        String meta=UnitConverter.format(item.quantity)+" "+item.unit;
        boolean expiresSoon=false;
        if(item.expiryDate!=null) {
            meta+="  •  Expires "+item.expiryDate;
            if(expiryIndicators) try {
                LocalDate date=LocalDate.parse(item.expiryDate), today=LocalDate.now();
                expiresSoon=!date.isBefore(today) && !date.isAfter(today.plusDays(3));
            } catch(Exception ignored) {}
        }
        holder.expiryBadge.setVisibility(expiresSoon?View.VISIBLE:View.GONE);
        holder.meta.setText(meta); holder.edit.setOnClickListener(v->actions.edit(item)); holder.delete.setOnClickListener(v->actions.delete(item));
        holder.itemView.setAlpha(1f);           //7. END.......................................................................................................
    }
    @Override public int getItemCount() { return items.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        final TextView name,meta,expiryBadge; final View edit,delete;
        Holder(View view) { super(view); name=view.findViewById(R.id.name); meta=view.findViewById(R.id.meta); expiryBadge=view.findViewById(R.id.expiry_badge); edit=view.findViewById(R.id.edit); delete=view.findViewById(R.id.delete); }
    }
}
