package com.example.one17;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    String changedText = "";
    View views;
    Query query;
    private FirebaseRecyclerAdapter<foodItem, foodItemHolder> firebaseRecyclerAdapter;
    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.search_edit_text);

        firebaseDatabase = FirebaseDatabase.getInstance();

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        query = rootRef.orderByChild("storeID").equalTo("store894");




        FirebaseRecyclerOptions<foodItem> firebaseRecyclerOptions =
                new FirebaseRecyclerOptions.Builder<foodItem>()
                        .setQuery(query, new SnapshotParser<foodItem>() {
                            @NonNull
                            @Override
                            public foodItem parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new foodItem(snapshot.child("itemName").getValue().toString(),
                                        snapshot.child("itemPrice1").getValue().toString(),
                                        Integer.parseInt(snapshot.child("itemIsAvailable").getValue().toString()));
                            }
                        })
                        .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<foodItem, foodItemHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull foodItemHolder blogPostHolder, final int position, @NonNull final foodItem foodItem) {
                boolean status = blogPostHolder.setFoodItem(foodItem,changedText);
                if (!status) {
                    blogPostHolder.txtConfigurationName.setVisibility(View.GONE);
                    blogPostHolder.aSwitch.setVisibility(View.GONE);
                    blogPostHolder.txtConfigurationPrice.setVisibility(View.GONE);
                    blogPostHolder.blankView.setVisibility(View.GONE);
                }
                blogPostHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public foodItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                return new foodItemHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                recyclerView.removeAllViews();

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changedText = charSequence.toString().toLowerCase();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private class foodItemHolder extends RecyclerView.ViewHolder {

        private TextView txtConfigurationName, txtConfigurationPrice;
        private Switch aSwitch;
        private View blankView;

        foodItemHolder(View itemView) {
            super(itemView);
            views = itemView;
            txtConfigurationName = itemView.findViewById(R.id.txtConfigItemName);
            txtConfigurationPrice = itemView.findViewById(R.id.txtConfigPrice);
            aSwitch = itemView.findViewById(R.id.switchConfigitems);
            blankView = itemView.findViewById(R.id.blank_view);

        }

        boolean setFoodItem(foodItem foodItem, String string) {
            if(foodItem.getItemName().toLowerCase().contains(string)) {
                String itemName = foodItem.getItemName();
                txtConfigurationName.setText(itemName);
                String itemPrice = foodItem.getItemPrice();
                txtConfigurationPrice.setText(itemPrice);
                int available = foodItem.getAvailable();
                aSwitch.setChecked(available == 1);
                return true;
            }
            else return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter!= null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

}