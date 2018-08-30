package com.cpm.phillipspc.dailyEntry;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cpm.phillipspc.R;
import com.cpm.phillipspc.constant.CommonString;
import com.cpm.phillipspc.database.PhilipsAttendanceDB;
import com.cpm.phillipspc.getterSetter.JCPMasterGetterSetter;
import com.cpm.phillipspc.getterSetter.MyPerformanceMer;
import com.cpm.phillipspc.getterSetter.MyPerformanceRoutewiseMer;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyPerfromanceActivity extends AppCompatActivity {
    Toolbar toolbar;
    private RecyclerView lv_performance, lv_route;
    private Button btnexit;
    private TextView tvname;
    JCPMasterGetterSetter jcpGetset;
    String _UserId, visit_date, store_id, username;
    PhilipsAttendanceDB db;
    ArrayList<MyPerformanceMer> performance_list = new ArrayList<MyPerformanceMer>();
    ArrayList<MyPerformanceRoutewiseMer> route_list = new ArrayList<MyPerformanceRoutewiseMer>();
    ValueAdapter adapter;
    RouteAdapter routeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_perfromance);
        declaration();
        performance_list = db.getMyPerformanceData();
        route_list = db.getRouteData();
        if (performance_list.size() > 0) {

            adapter = new ValueAdapter(getApplicationContext(), performance_list);
            lv_performance.setAdapter(adapter);
            lv_performance.setLayoutManager(new LinearLayoutManager(this));

        }
        if (route_list.size() > 0) {

            routeAdapter = new RouteAdapter(getApplicationContext(), route_list);
            lv_route.setAdapter(routeAdapter);
            lv_route.setLayoutManager(new LinearLayoutManager(this));

        }
    }

    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<MyPerformanceMer> data = Collections.emptyList();

        public ValueAdapter(Context context, List<MyPerformanceMer> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.performance_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

            final MyPerformanceMer current = data.get(position);

            getPerformanceColor(String.valueOf(current.getAttendance()), viewHolder.tvattendencee);
            getPerformanceColor(String.valueOf(current.getMerchandise()), viewHolder.tvmerchandise);
            getPerformanceColor(String.valueOf(current.getPSS()), viewHolder.tvpss);

            if (current.getPeriod().equalsIgnoreCase("Target")) {
                viewHolder.tvperiod.setText(current.getPeriod());
                viewHolder.tvperiod.setBackgroundColor(Color.BLUE);
                viewHolder.tvattendencee.setBackgroundColor(Color.BLUE);
                viewHolder.tvmerchandise.setBackgroundColor(Color.BLUE);
                viewHolder.tvpss.setBackgroundColor(Color.BLUE);

                viewHolder.tvattendencee.setText(String.valueOf(current.getAttendance()));
                viewHolder.tvmerchandise.setText(String.valueOf(current.getMerchandise()));
                viewHolder.tvpss.setText(String.valueOf(current.getPSS()));
            }
            viewHolder.tvperiod.setText(current.getPeriod());
            viewHolder.tvattendencee.setText(String.valueOf(current.getAttendance()));
            viewHolder.tvmerchandise.setText(String.valueOf(current.getMerchandise()));
            viewHolder.tvpss.setText(String.valueOf(current.getPSS()));

        }


        @Override
        public int getItemCount() {
            return data.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvperiod, tvattendencee, tvpss, tvmerchandise;

            public MyViewHolder(View itemView) {
                super(itemView);
                tvperiod = (TextView) itemView.findViewById(R.id.tvperiod);
                tvattendencee = (TextView) itemView.findViewById(R.id.tvattendencee);
                tvmerchandise = (TextView) itemView.findViewById(R.id.tvmerchandise);
                tvpss = (TextView) itemView.findViewById(R.id.tvpss);

            }
        }
    }


    public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<MyPerformanceRoutewiseMer> data = Collections.emptyList();

        public RouteAdapter(Context context, List<MyPerformanceRoutewiseMer> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.route_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

            final MyPerformanceRoutewiseMer current = data.get(position);

            getPerformanceColor(String.valueOf(current.getPSS()), viewHolder.tvpss);
            getPerformanceColor(String.valueOf(current.getMerchandise()), viewHolder.tvmerchandise);

            viewHolder.tvroute.setText(current.getRoute());
            viewHolder.tvpss.setText(String.valueOf(current.getPSS()));
            viewHolder.tvmerchandise.setText(String.valueOf(current.getMerchandise()));

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvroute, tvpss, tvmerchandise;

            public MyViewHolder(View itemView) {
                super(itemView);
                tvroute = (TextView) itemView.findViewById(R.id.tvroute);
                tvpss = (TextView) itemView.findViewById(R.id.tvpss);
                tvmerchandise = (TextView) itemView.findViewById(R.id.tvmerchandise);
            }
        }
    }


    public void getPerformanceColor(String performance, TextView tv) {

        try {
            int calc = Integer.parseInt(performance);

            if (calc >= 90) {
                //return "Green";
                tv.setTextColor(getResources().getColor(R.color.green));

            } else if (calc < 90 && calc >= 75) {
                //return "Amber";
                tv.setTextColor(getResources().getColor(R.color.amber));
            } else if (calc < 75) {
                //return "Red";
                tv.setTextColor(getResources().getColor(R.color.red));
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            System.out.println("Exception ---> " + e.toString());

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == android.R.id.home) {

            // NavUtils.navigateUpFromSameTask(this);
            finish();


            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

        }

        return super.onOptionsItemSelected(item);
    }

    void declaration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Performance");
        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null) {
            jcpGetset = (JCPMasterGetterSetter) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
        }
        lv_performance = (RecyclerView) findViewById(R.id.lv_performance);
        lv_route = (RecyclerView) findViewById(R.id.lv_routewise);
        btnexit = (Button) findViewById(R.id.btnexit);
        tvname = (TextView) findViewById(R.id.tv_mer_name);
        db = new PhilipsAttendanceDB(MyPerfromanceActivity.this);
        db.open();
    }
}
