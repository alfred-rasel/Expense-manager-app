package com.example.expensemanagerapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanagerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExpenseFragment() {
        // Required empty public constructor
    }
 // firebase database
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
  private  RecyclerView recyclerView ;
    private  TextView expenseSumResult;
    //edt data item
    private EditText edtAmmount;
    private  EditText edtType;
    private  EditText edtNote;
    //button
    private Button btnUpdate;
    private  Button btnDelete;

    //Data variable
    private String type;
    private  String note;
    private  int ammount;
    private  String post_key;

    // test
    private FirebaseRecyclerAdapter expenseadapter;





    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
          View myview= inflater.inflate(R.layout.fragment_expense, container, false);

            mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
         expenseSumResult =myview.findViewById(R.id.expense_txt_result);
        recyclerView =myview.findViewById(R.id.recycler_id_expense);

        RecyclerView.LayoutManager layoutManager =new LinearLayoutManager(getActivity());
                ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
                ((LinearLayoutManager) layoutManager).setReverseLayout(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);
                mExpenseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int expenseSum =0;
                            for(DataSnapshot mysanapshot :snapshot.getChildren()){
                                Data data =mysanapshot.getValue(Data.class);
                                expenseSum+=data.getAmount();
                                String strExpensesum=String.valueOf(expenseSum);
                                expenseSumResult.setText(strExpensesum+=".00");
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

          return myview;
    }
    public  void onStart() {
         super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data.class)
                .build();

        expenseadapter = new FirebaseRecyclerAdapter<Data,MyViewHolder>(options) {




            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpenseFragment.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false));

            }

            protected void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Data model) {
                holder.setAmmount(model.getAmount());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //change
                       String  post_key= getRef(position).getKey();

                        type=model.getType();
                        note=model.getNote();
                        ammount=model.getAmount();

                        updateDataItem();
                    }
                });
            }
        };
            expenseadapter.startListening();
        recyclerView.setAdapter(expenseadapter);

    }
   public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public MyViewHolder(View itemView){
            super(itemView);
            mView=itemView;

        }
        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
        private  void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }
        private void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }
        private  void setAmmount(int ammount){
            TextView mAAmmount=mView.findViewById(R.id.ammount_txt_expense);
            String strammount =String.valueOf(ammount);
            mAAmmount.setText(strammount);

        }

    }
    private  void updateDataItem(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater =LayoutInflater.from(getActivity());
        View myview =inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myview);

        edtAmmount =myview.findViewById(R.id.amount_edt);
        edtType =myview.findViewById(R.id.type_edt);
        edtNote =myview.findViewById(R.id.note_edt);

        edtType.setText(type);
        edtType.setSelection(type.length());
        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());


        btnUpdate=myview.findViewById(R.id.btn_upd_Update);
        btnDelete=myview.findViewById(R.id.btnuPD_Delete);
       final  AlertDialog dialog=mydialog.create();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();

                String stammount =String.valueOf(ammount);
                stammount=edtAmmount.getText().toString().trim();


                int intamount =Integer.parseInt(stammount);
                String mDate= DateFormat.getDateInstance().format(new Date());

                    Data data =new Data(intamount,type,note,post_key,mDate);
                    mExpenseDatabase.child(post_key).setValue(data);
                    dialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpenseDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });
        dialog.show();


    }

}