package com.cpm.phillipspc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cpm.phillipspc.R;

/**
 * Created by neerajg on 2/14/2018.
 */
//
//public class FeedBackQuestionAdapter extends RecyclerView.Adapter<FeedBackQuestionAdapter.ViewHolder>{
//
//    private Context context;
//
//    public FeedBackQuestionAdapter(FeedbackActivity feedbackActivity) {
//        this.context = feedbackActivity;
//    }
//
//    @Override
//    public FeedBackQuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item_list,parent,false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(FeedBackQuestionAdapter.ViewHolder holder, int position) {
//            holder.question.setText("sdfa");
//            holder.edt_answer.setText("sdjfjhasj");
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        TextView question;
//        Spinner sp_feedback_question;
//        EditText edt_answer;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            question = (TextView)itemView.findViewById(R.id.txt_question);
//            sp_feedback_question = (Spinner) itemView.findViewById(R.id.sp_auditAnswer);
//            edt_answer = (EditText) itemView.findViewById(R.id.sp_auditAnswer);
//        }
//    }
//}
