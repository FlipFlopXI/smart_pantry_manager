package com.smartpantry.manager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.smartpantry.manager.R;
import com.smartpantry.manager.models.Recipe;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.Holder> {
    private final List<Recipe> items=new ArrayList<>();
    private final Consumer<Recipe> onOpen;
    public RecipeAdapter(Consumer<Recipe> onOpen) { this.onOpen=onOpen; setHasStableIds(true); }
    public void setItems(List<Recipe> recipes) { items.clear(); items.addAll(recipes); notifyDataSetChanged(); }
    @Override public long getItemId(int position) { return items.get(position).id; }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent,int type) { return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recipe,parent,false)); }
    @Override public void onBindViewHolder(@NonNull Holder holder,int position) {
        Recipe recipe=items.get(position); holder.name.setText(recipe.name); holder.meta.setText(recipe.description+"  •  "+recipe.preparationTime+" min"); holder.view.setOnClickListener(v->onOpen.accept(recipe)); holder.itemView.setOnClickListener(v->onOpen.accept(recipe));
        holder.itemView.setAlpha(1f);
    }
    @Override public int getItemCount() { return items.size(); }
    static class Holder extends RecyclerView.ViewHolder { final TextView name,meta; final View view; Holder(View root) { super(root); name=root.findViewById(R.id.name); meta=root.findViewById(R.id.meta); view=root.findViewById(R.id.view); } }
}
