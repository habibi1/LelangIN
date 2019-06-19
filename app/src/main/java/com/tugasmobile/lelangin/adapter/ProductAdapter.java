package com.tugasmobile.lelangin.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.tugasmobile.lelangin.CustomOnItemClickListener;
import com.tugasmobile.lelangin.DetailProductActivity;
import com.tugasmobile.lelangin.EditProductActivity;
import com.tugasmobile.lelangin.R;
import com.tugasmobile.lelangin.model.Product;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder>{

    Context context;
    ArrayList<Product> products;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void setProductModels(ArrayList<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public ProductAdapter() {

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        if (!myViewHolder.tes.equals(products.get(i).getIdOwner())){
            myViewHolder.edit.setVisibility(View.GONE);
        }

        myViewHolder.name.setText(products.get(i).getName());
        myViewHolder.owner.setText(products.get(i).getOwner());
        myViewHolder.price.setText(String.valueOf(products.get(i).getPrice()));
        Picasso.get().load(products.get(i).getImage()).placeholder(R.drawable.ic_image_black_24dp).into(myViewHolder.image);

        myViewHolder.cardView.setOnClickListener(new CustomOnItemClickListener(i, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {

                Intent intent = new Intent(context, DetailProductActivity.class);
                intent.putExtra(DetailProductActivity.EXTRA_ID, products.get(i).getId());
                intent.putExtra(DetailProductActivity.EXTRA_ID_OWNER, products.get(i).getIdOwner());
                intent.putExtra(DetailProductActivity.EXTRA_NAME, myViewHolder.name.getText().toString());
                intent.putExtra(DetailProductActivity.EXTRA_OWNER, myViewHolder.owner.getText().toString());
                intent.putExtra(DetailProductActivity.EXTRA_PRICE, myViewHolder.price.getText().toString());
                intent.putExtra(DetailProductActivity.EXTRA_DESCRIPTION, products.get(i).getDescription());
                intent.putExtra(DetailProductActivity.EXTRA_IMAGE, products.get(i).getImage());
                context.startActivity(intent);
            }
        }));

        myViewHolder.edit.setOnClickListener(new CustomOnItemClickListener(i, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra(DetailProductActivity.EXTRA_ID, products.get(i).getId());
                intent.putExtra(DetailProductActivity.EXTRA_ID_OWNER, products.get(i).getIdOwner());
                intent.putExtra(DetailProductActivity.EXTRA_NAME, myViewHolder.name.getText().toString());
                intent.putExtra(DetailProductActivity.EXTRA_OWNER, myViewHolder.owner.getText().toString());
                intent.putExtra(DetailProductActivity.EXTRA_PRICE, myViewHolder.price.getText().toString());
                intent.putExtra(DetailProductActivity.EXTRA_DESCRIPTION, products.get(i).getDescription());
                intent.putExtra(DetailProductActivity.EXTRA_IMAGE, products.get(i).getImage());
                context.startActivity(intent);
            }
        }));
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, owner, price;
        ImageView image, edit;
        CardView cardView;

        FirebaseUser key_user = FirebaseAuth.getInstance().getCurrentUser();
        String tes = key_user.getUid();

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            edit = itemView.findViewById(R.id.edit);
            name = itemView.findViewById(R.id.product_name);
            owner = itemView.findViewById(R.id.product_owner);
            price = itemView.findViewById(R.id.product_price);
            image = itemView.findViewById(R.id.product_image);
            cardView = itemView.findViewById(R.id.cv_item_product);
        }
    }


}
