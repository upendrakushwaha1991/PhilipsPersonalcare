package com.cpm.pgattendance.dailyEntry;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpm.pgattendance.R;

public class CampaignListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Context context;
    CampaignListAdapter adapter;
    Intent campaignEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_list);
        declaration();
        prepareList();
    }

    void prepareList() {
        adapter = new CampaignListAdapter(context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    class CampaignListAdapter extends RecyclerView.Adapter<MyViewHolder> {

        LayoutInflater inflater;

        CampaignListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_add_store_list_item_view, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.textView.setText("campaign " + position);
            holder.storelist_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(campaignEntry);
                }
            });

        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout storelist_ll;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txt_storeName);
            storelist_ll = itemView.findViewById(R.id.storelist_ll);
        }
    }

    void declaration() {
        context = this;
        recyclerView = (RecyclerView) findViewById(R.id.campaignList);
        campaignEntry = new Intent(context, CampaignEntryActivity.class);
    }
}
