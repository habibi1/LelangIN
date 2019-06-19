package com.tugasmobile.lelangin.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tugasmobile.lelangin.HomeActivity;
import com.tugasmobile.lelangin.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoneyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoneyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoneyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DatabaseReference mDatabase;

    Dialog top_up_dialog;

    TextView saldo, saldo2;
    Button top_up_button;

    private OnFragmentInteractionListener mListener;

    public MoneyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoneyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoneyFragment newInstance(String param1, String param2) {
        MoneyFragment fragment = new MoneyFragment();
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

        View view = inflater.inflate(R.layout.fragment_money, container, false);

        saldo = view.findViewById(R.id.money_saldo);
        top_up_button = view.findViewById(R.id.money_top_up);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        final HomeActivity homeActivity = (HomeActivity)getActivity();

        top_up_dialog = new Dialog(getActivity());

        saldo.setText("Rp."+homeActivity.saldoOwner);

        top_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopUp(Integer.parseInt(homeActivity.saldoOwner));
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void ShowPopUp(final int saldoOwner){
        TextView textClose;
        final EditText amount_top_up;
        Button btnConfirm;
        top_up_dialog.setContentView(R.layout.top_up_pop_up);
        amount_top_up = top_up_dialog.findViewById(R.id.jumlah_top_up);
        textClose = top_up_dialog.findViewById(R.id.txtclose);
        saldo2 = top_up_dialog.findViewById(R.id.money_saldo2);
        btnConfirm = top_up_dialog.findViewById(R.id.btnConfirm);

        saldo2.setText(saldo.getText().toString());

        FirebaseUser key_user = FirebaseAuth.getInstance().getCurrentUser();
        final String tes = key_user.getUid();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                amount_top_up.setError(null);

                boolean cancel = false;
                View focusView = null;

                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(amount_top_up.getText().toString())){
                    amount_top_up.setError(getString(R.string.error_field_required));
                    focusView = amount_top_up;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    mDatabase.child("users").child(tes).child("saldo").setValue(Integer.toString(saldoOwner + Integer.parseInt(amount_top_up.getText().toString()))).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            saldo.setText("Rp."+((saldoOwner + Integer.parseInt(amount_top_up.getText().toString()))));
                            top_up_dialog.dismiss();
                        }
                    });
                }
            }
        });

        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                top_up_dialog.dismiss();
            }
        });
        top_up_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        top_up_dialog.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
