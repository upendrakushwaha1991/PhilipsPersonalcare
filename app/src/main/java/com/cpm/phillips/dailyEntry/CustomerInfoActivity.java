package com.cpm.phillips.dailyEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cpm.phillips.R;
import com.cpm.phillips.constant.AlertandMessages;
import com.cpm.phillips.constant.CommonString;
import com.cpm.phillips.database.PhilipsAttendanceDB;
import com.cpm.phillips.getterSetter.CustomerInfoGetterSetter;
import com.cpm.phillips.getterSetter.QuestionnaireGetterSetter;
import com.cpm.phillips.gsonGetterSetter.FeedbackQuestionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerInfoActivity extends AppCompatActivity {

    Context context;
    FloatingActionButton fab;
    PhilipsAttendanceDB db;
    SharedPreferences preferences;
    String visit_date, username, msg,User_Name,Email,Number,store_cd;
    private EditText txtUserName,txtUserEmail,txtNumber;
    AnswerAdapter adapter;
    CustomerDetailsAdapter customerDetailsAdapter;
    RecyclerView recyclerView,customerInfoView;
    ArrayList<CustomerInfoGetterSetter> cusInfoList;
    ArrayList<QuestionnaireGetterSetter> headerListData;
    ArrayList<QuestionnaireGetterSetter> listDataHeader;
    ArrayList<CustomerInfoGetterSetter> customerList;
    HashMap<QuestionnaireGetterSetter, ArrayList<QuestionnaireGetterSetter>> listDataChild;
    ArrayList<QuestionnaireGetterSetter> childListData;
    ArrayList<FeedbackQuestionData> AnswerList;
    List<Integer> checkHeaderArray = new ArrayList<>();
    boolean checkflag = true;
    int saveflag = 0;
    Button btn_submit,btnUpdate;
    TextView txtCustomer,txtPosition;
    CustomerInfoGetterSetter data;
  /*  FeedbackQuestionData data1;*/
    LinearLayout  ll_View;
    ArrayList<FeedbackQuestionData> FeedbackList =  new ArrayList<>();

    boolean edit_flag = true;
    boolean delete_flag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info);
        declaration();
        prepareList();

        customerList = db.getCustomerInfoDetails(visit_date,store_cd);
        if(customerList.size() > 0) {
            ll_View.setVisibility(View.VISIBLE);
            saveflag = 1;
            CustomerDetailsList(customerList);
        }else{
            ll_View.setVisibility(View.GONE);
            saveflag = 0;
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.clearFocus();
                if (validateData(listDataHeader)) {

                    // adding user details in to list
                    for(int i=0;i<listDataHeader.size();i++){
                        FeedbackQuestionData  data1 = new FeedbackQuestionData();
                        data1.setStore_Id(store_cd);
                        data1.setQuestion_CD(listDataHeader.get(i).getQUESTION_ID().get(0));
                        data1.setAnswer_CD(listDataHeader.get(i).getSp_AnswerId());
                        FeedbackList.add(data1);
                    }

                    data = new CustomerInfoGetterSetter();
                    data.setUSER_MOBILE(Number);
                    data.setUSER_EMAIL(Email);
                    data.setUSER_NAME(User_Name);
                    data.setList(FeedbackList);
                    customerList.add(data);
                    loadCustomerData(customerList);
                } else {
                    AlertandMessages.showSnackbarMsg(context, msg);
                }
            }
        });


        // update user details list
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.clearFocus();
                if (validateData(listDataHeader)) {
                    String pos = txtPosition.getText().toString();
                    ArrayList<FeedbackQuestionData> updatedFeedBackList =  new ArrayList<>();
                   // adding user details into list
                    for(int i=0;i<listDataHeader.size();i++){
                        FeedbackQuestionData data1 = new FeedbackQuestionData();
                        data1.setStore_Id(store_cd);
                        data1.setQuestion_CD(listDataHeader.get(i).getQUESTION_ID().get(0));
                        data1.setAnswer_CD(listDataHeader.get(i).getSp_AnswerId());
                        updatedFeedBackList.add(data1);
                    }

                    data = new CustomerInfoGetterSetter();
                    data.setUSER_MOBILE(Number);
                    data.setUSER_EMAIL(Email);
                    data.setUSER_NAME(User_Name);
                    data.setList(updatedFeedBackList);

                    customerList.set(Integer.parseInt(pos),data);
                    updateCustomerData(customerList);
                } else {
                    AlertandMessages.showSnackbarMsg(context, msg);
                }
            }

            private void updateCustomerData(final ArrayList<CustomerInfoGetterSetter> customerList) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(CustomerInfoActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("Do you want to update customer details ?").setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Load customer details
                                CustomerDetailsList(customerList);
                                txtUserName.setText("");
                                txtUserEmail.setText("");
                                txtNumber.setText("");
                                ll_View.setVisibility(View.VISIBLE);
                                btnUpdate.setVisibility(View.GONE);
                                btn_submit.setVisibility(View.VISIBLE);

                                prepareList();
                                adapter.notifyDataSetChanged();
                                AlertandMessages.showSnackbarMsg(txtUserName,"Existing Customer Info Updated Successfully");
                                edit_flag = true;
                                delete_flag = true;

                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveflag == 0){
                    msg = "Please add complete details first";
                    AlertandMessages.showSnackbarMsg(context, msg);
                }else{
                    new AlertandMessages((Activity) context, null, null, null).saveCustomerInfo(CustomerInfoActivity.this,customerList);
                }
            }
        });

    }

    private void loadCustomerData(final ArrayList<CustomerInfoGetterSetter> customerList) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to add customer details ?").setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Load customer details
                        CustomerDetailsList(customerList);
                        txtUserName.setText("");
                        txtUserEmail.setText("");
                        txtNumber.setText("");
                        ll_View.setVisibility(View.VISIBLE);
                        prepareList();
                        adapter.notifyDataSetChanged();
                        AlertandMessages.showSnackbarMsg(txtUserName,"Customer Info added Successfully");
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // setting user List
    public  void CustomerDetailsList(ArrayList<CustomerInfoGetterSetter> customerList)
    {
        customerDetailsAdapter = new CustomerDetailsAdapter(CustomerInfoActivity.this, customerList);
        customerInfoView.setLayoutManager(new LinearLayoutManager(CustomerInfoActivity.this));
        customerInfoView.setItemAnimator(new DefaultItemAnimator());
        customerInfoView.addItemDecoration(new MyDividerItemDecoration(CustomerInfoActivity.this, LinearLayoutManager.VERTICAL,10));
        customerInfoView.setAdapter(customerDetailsAdapter);
        customerDetailsAdapter.notifyDataSetChanged();
    }

    //Preparing the list data
    private void prepareList() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        db.open();
        headerListData = db.getQuestionnaireData("2");
        if (headerListData.size() > 0) {
            // Adding child data
            for (int i = 0; i < headerListData.size(); i++) {
                listDataHeader.add(headerListData.get(i));
                db.open();
                childListData = db.getQuestionnaireAnswerData(headerListData.get(i));
                listDataChild.put(listDataHeader.get(i), childListData); // Header, Child data
            }
            adapter = new AnswerAdapter(listDataHeader, listDataChild);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.addItemDecoration(new MyDividerItemDecoration(CustomerInfoActivity.this, LinearLayoutManager.VERTICAL,10));
            recyclerView.setAdapter(adapter);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (headerListData.size() > 0) {
                new AlertandMessages((Activity) context, null, null, null).backpressedAlert();
            } else {
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (headerListData.size() > 0) {
            new AlertandMessages((Activity) context, null, null, null).backpressedAlert();
        } else {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }

    }

    class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {
        ArrayList<QuestionnaireGetterSetter> questionList;
        HashMap<QuestionnaireGetterSetter, ArrayList<QuestionnaireGetterSetter>> answerHashMap;
        ArrayList<QuestionnaireGetterSetter> answerList;
        // ArrayList<Audit_QuestionDataGetterSetter> ans_list;

        public AnswerAdapter(ArrayList<QuestionnaireGetterSetter> questionList,
                             HashMap<QuestionnaireGetterSetter, ArrayList<QuestionnaireGetterSetter>> answerHashMap) {
            this.questionList = questionList;
            this.answerHashMap = answerHashMap;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item_list, parent, false);
            return new ViewHolder(view);
        }

        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.data = questionList.get(position);

            holder.txt_question.setText(holder.data.getQUESTION().get(0));

            ArrayList<QuestionnaireGetterSetter> ans_list = answerHashMap.get(holder.data);

            if (holder.data.getQUESTION_TYPE().get(0).equalsIgnoreCase("Dropdown")) {
                holder.sp_auditAnswer.setVisibility(View.VISIBLE);
                holder.edt_answer.setVisibility(View.GONE);
            } else {
                holder.edt_answer.setVisibility(View.VISIBLE);
                holder.sp_auditAnswer.setVisibility(View.GONE);
            }

            holder.sp_auditAnswer.setAdapter(new AnswerSpinnerAdapter((Activity) context, R.layout.spinner_text_view, ans_list));

            final ArrayList<QuestionnaireGetterSetter> finalAns_list = ans_list;
            holder.sp_auditAnswer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    QuestionnaireGetterSetter ans = finalAns_list.get(position);
                    holder.data.setSp_AnswerId(ans.getANSWER_ID().get(0));
                    holder.data.setSp_Answer(ans.getANSWER().get(0));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });



            holder.edt_answer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        holder.data.setSp_AnswerId("0");
                        holder.data.setSp_Answer(((EditText) v).getText().toString());
                    }
                }
            });

            for (int i = 0; i < ans_list.size(); i++) {
                if (holder.data.getQUESTION_TYPE().get(0).equalsIgnoreCase("Dropdown") && ans_list.get(i).getANSWER_ID().get(0).equals(holder.data.getSp_AnswerId())) {
                    holder.sp_auditAnswer.setSelection(i);
                    break;
                } else {
                    holder.edt_answer.setText(ans_list.get(i).getANSWER().get(0));
                }
            }
        }

        @Override
        public int getItemCount() {
            return questionList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView txt_question;
            public final Spinner sp_auditAnswer;
            public final EditText edt_answer;
            CardView card_view;
            QuestionnaireGetterSetter data;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                txt_question = view.findViewById(R.id.txt_question);
                sp_auditAnswer = view.findViewById(R.id.sp_auditAnswer);
                edt_answer =  view.findViewById(R.id.edt_answer);
                card_view =  view.findViewById(R.id.card_view);
            }
        }
    }

    public class AnswerSpinnerAdapter extends ArrayAdapter<QuestionnaireGetterSetter> {
        List<QuestionnaireGetterSetter> list;
        Context context;
        int resourceId;

        public AnswerSpinnerAdapter(Context context, int resourceId, ArrayList<QuestionnaireGetterSetter> list) {
            super(context, resourceId, list);
            this.context = context;
            this.list = list;
            this.resourceId = resourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(resourceId, parent, false);
            QuestionnaireGetterSetter cm = list.get(position);
            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text);
            txt_spinner.setText(list.get(position).getANSWER().get(0));

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(resourceId, parent, false);

            QuestionnaireGetterSetter cm = list.get(position);

            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text);
            txt_spinner.setText(cm.getANSWER().get(0));

            return view;
        }

    }

    boolean validateData(ArrayList<QuestionnaireGetterSetter> data) {
        //boolean flag = true;
       checkHeaderArray.clear();
        User_Name = txtUserName.getText().toString();
        Email  = txtUserEmail.getText().toString();
        Number = txtNumber.getText().toString();

       if(TextUtils.isEmpty(User_Name)){
           msg = "Please enter username";
           checkflag = false;
       }else if(!isValidEmailAddress(Email)){
           msg = "Please valid enter email";
           checkflag = false;
       }else if(TextUtils.isEmpty(Number)){
           msg = "Please enter number";
           checkflag = false;
       }else{
           for (int i = 0; i < data.size(); i++) {
               if (data.get(i).getQUESTION_TYPE().get(0).equalsIgnoreCase("Dropdown") && data.get(i).getSp_AnswerId().equalsIgnoreCase("0")) {
                   if (!checkHeaderArray.contains(i)) {
                       checkHeaderArray.add(i);
                   }
                   msg = "Please Select Answer from Dropdown";
                   checkflag = false;
                   break;
               } else if (data.get(i).getQUESTION_TYPE().get(0).equalsIgnoreCase("Freetext") && (data.get(i).getSp_Answer() == null || data.get(i).getSp_Answer().equalsIgnoreCase(""))) {
                   if (!checkHeaderArray.contains(i)) {
                       checkHeaderArray.add(i);
                   }
                   msg = "Please Fill Answer";
                   checkflag = false;
                   break;
               } else {
                   msg = "";
                   checkflag = true;
                   saveflag = 1;
               }

               if (checkflag == false) {
                   break;
               }
           }
       }
        return checkflag;
    }

    void declaration() {
        context = this;
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User FeedBack");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, "");
        username = preferences.getString(CommonString.KEY_USERNAME, "");
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        recyclerView = findViewById(R.id.list);
        customerInfoView = findViewById(R.id.customerInfo_View);
        txtUserName = findViewById(R.id.user_name);
        txtUserEmail = findViewById(R.id.user_email);
        txtPosition = findViewById(R.id.txtPos);
        txtNumber = findViewById(R.id.user_mobile);
        btn_submit = findViewById(R.id.btn_add);
        btnUpdate = findViewById(R.id.btn_update);
        txtCustomer = findViewById(R.id.customerTxt);
        ll_View = findViewById(R.id.ll_view);


        fab =  findViewById(R.id.fab);

        db = new PhilipsAttendanceDB(context);
        db.open();
    }


    // check email validation
    public static boolean isValidEmailAddress(String emailAddress) {
        String emailRegEx;
        Pattern pattern;
        // Regex for a valid email address
        emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
        // Compare the regex with the email address
        pattern = Pattern.compile(emailRegEx);
        Matcher matcher = pattern.matcher(emailAddress);
        if (!matcher.find()) {
            return false;
        }
        return true;
    }

    public class CustomerDetailsAdapter extends RecyclerView.Adapter<CustomerDetailsAdapter.ViewHolder> {

        ArrayList<CustomerInfoGetterSetter> list = new ArrayList<>();
        Context context;

        public CustomerDetailsAdapter(CustomerInfoActivity customerInfoActivity, ArrayList<CustomerInfoGetterSetter> customerList) {
            this.context = customerInfoActivity;
            this.list = customerList;
        }


        @Override
        public CustomerDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.name.setText(list.get(position).getUSER_NAME());
            holder.email.setText(list.get(position).getUSER_EMAIL());
            holder.number.setText(list.get(position).getUSER_MOBILE());


            holder.imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edit_flag == true) {
                        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        builder.setTitle("Alert");
                        builder.setMessage("Do you want to edit this customer details ?").setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        btnUpdate.setVisibility(View.VISIBLE);
                                        btn_submit.setVisibility(View.GONE);
                                        holder.imgEdit.setVisibility(View.GONE);
                                        delete_flag = false;

                                        if (customerList.size() > 0) {
                                            User_Name = customerList.get(position).getUSER_NAME();
                                            Number = customerList.get(position).getUSER_MOBILE();
                                            Email = customerList.get(position).getUSER_EMAIL();
                                            AnswerList = customerList.get(position).getList();

                                            txtUserName.setText("");
                                            txtUserEmail.setText("");
                                            txtNumber.setText("");
                                            txtPosition.setText("");

                                            txtUserName.setText(User_Name);
                                            txtUserEmail.setText(Email);
                                            txtNumber.setText(Number);
                                            txtPosition.setText(position + "");

                                            if(AnswerList.size() > 0) {
                                                for (int i = 0; i < listDataHeader.size(); i++) {
                                                    if (listDataHeader.get(i).getQUESTION_ID().get(0).equals(AnswerList.get(i).getQuestion_CD())) {
                                                        listDataHeader.get(i).setSp_AnswerId(AnswerList.get(i).getAnswer_CD());
                                                    }
                                                }
                                                adapter = new AnswerAdapter(listDataHeader, listDataChild);
                                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                                recyclerView.addItemDecoration(new MyDividerItemDecoration(CustomerInfoActivity.this, LinearLayoutManager.VERTICAL,10));
                                                recyclerView.setAdapter(adapter);
                                            }
                                            edit_flag = false;
                                        }
                                    }
                                });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        android.support.v7.app.AlertDialog alert = builder.create();
                        alert.show();
                    }else{
                        AlertandMessages.showSnackbarMsg(CustomerInfoActivity.this,"Please update the existing data first");
                    }
                }
            });

            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (delete_flag == true) {
                        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        builder.setTitle("Alert");
                        builder.setMessage("Do you want to delete this customer details ?").setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if(list.size() > 1) {
                                            list.remove(position);
                                        }else{
                                            list.remove(position);
                                            ll_View.setVisibility(View.GONE);
                                        }
                                        CustomerDetailsList(list);
                                    }
                                });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        android.support.v7.app.AlertDialog alert = builder.create();
                        alert.show();

                    }else{
                        AlertandMessages.showSnackbarMsg(CustomerInfoActivity.this,"Please update the existing data first");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, email, number;
            ImageView imgDelete,imgEdit;

            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.user_name);
                email = itemView.findViewById(R.id.user_email);
                number = itemView.findViewById(R.id.user_mobile);
                imgDelete = itemView.findViewById(R.id.item_delete);
                imgEdit = itemView.findViewById(R.id.item_edit);
            }
        }
    }
}
