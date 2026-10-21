package com.smartpantry.manager.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.smartpantry.manager.R;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.utils.ValidationUtils;
import java.time.LocalDate;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditIngredientActivity extends AppCompatActivity {
    private static final String[] UNITS={"Select unit","grams","kilograms","millilitres","litres","pieces","teaspoons","tablespoons","cups"};
    private final ExecutorService worker=Executors.newSingleThreadExecutor();
    private EditText name,quantity;
    private Spinner unit;
    private String expiry;
    private long itemId;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_form);
        itemId=getIntent().getLongExtra("item_id",-1);
        name=findViewById(R.id.name); quantity=findViewById(R.id.quantity); unit=findViewById(R.id.unit);
        unit.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,UNITS));
        ((TextView)findViewById(R.id.title)).setText(itemId<0?"Add ingredient":"Edit ingredient");
        findViewById(R.id.back).setOnClickListener(v->{ startActivity(new Intent(this,PantryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP)); finish(); });
        findViewById(R.id.cancel).setOnClickListener(v->finish());
        findViewById(R.id.expiry).setOnClickListener(v->{ LocalDate today=LocalDate.now(); new DatePickerDialog(this,(picker,year,month,day)->{ expiry=String.format(Locale.ROOT,"%04d-%02d-%02d",year,month+1,day); ((TextView)findViewById(R.id.expiry_value)).setText("Expiry: "+expiry); },today.getYear(),today.getMonthValue()-1,today.getDayOfMonth()).show(); });
        findViewById(R.id.clear_expiry).setOnClickListener(v->{ expiry=null; ((TextView)findViewById(R.id.expiry_value)).setText("No expiry date"); });
        findViewById(R.id.save).setOnClickListener(v->save());
        if(state!=null) { expiry=state.getString("expiry"); if(expiry!=null) ((TextView)findViewById(R.id.expiry_value)).setText("Expiry: "+expiry); }
        if(itemId>=0 && state==null) worker.execute(()->{ try(DatabaseHelper db=new DatabaseHelper(this)) { PantryItem item=db.getPantryItem(itemId); runOnUiThread(()->{ if(item==null) { Toast.makeText(this,"Ingredient no longer exists",Toast.LENGTH_SHORT).show(); finish(); return; } name.setText(item.name); quantity.setText(String.valueOf(item.quantity)); for(int i=1;i<UNITS.length;i++) if(UNITS[i].equals(item.unit)) unit.setSelection(i); expiry=item.expiryDate; if(expiry!=null) ((TextView)findViewById(R.id.expiry_value)).setText("Expiry: "+expiry); }); } });
    }
    private void save() {
        TextInputLayout nameLayout=findViewById(R.id.name_layout), quantityLayout=findViewById(R.id.quantity_layout);
        nameLayout.setError(null); quantityLayout.setError(null);
        findViewById(R.id.unit_error).setVisibility(View.GONE);
        String cleanName=name.getText().toString().trim(); double amount=ValidationUtils.positiveQuantity(quantity.getText().toString());
        if(cleanName.isEmpty()) { nameLayout.setError("Enter an ingredient name"); return; }
        if(Double.isNaN(amount)) { quantityLayout.setError("Enter a number greater than zero"); return; }
        if(unit.getSelectedItemPosition()==0) { findViewById(R.id.unit_error).setVisibility(View.VISIBLE); unit.requestFocus(); return; }
        View save=findViewById(R.id.save); save.setEnabled(false);
        String selectedUnit=UNITS[unit.getSelectedItemPosition()], date=expiry;
        worker.execute(()->{ boolean success; try(DatabaseHelper db=new DatabaseHelper(this)) { success=itemId<0?db.addPantryItem(cleanName,amount,selectedUnit,date)>0:db.updatePantryItem(itemId,cleanName,amount,selectedUnit,date); } catch(Exception error) { success=false; }
            final boolean saved=success;
            runOnUiThread(()->{ if(saved) { Toast.makeText(this,"Ingredient saved!",Toast.LENGTH_SHORT).show(); finish(); } else { save.setEnabled(true); Toast.makeText(this,"Could not save ingredient",Toast.LENGTH_LONG).show(); } });
        });
    }
    @Override protected void onSaveInstanceState(Bundle state) { state.putString("expiry",expiry); super.onSaveInstanceState(state); }
    @Override protected void onDestroy() { worker.shutdown(); super.onDestroy(); }
}
