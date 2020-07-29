package com.example.abhivriddi20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {


    private Context mCtx;
    private List<complaint_class> productList;

    public ProductsAdapter(Context mCtx, List<complaint_class> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    public  void updateList(List<complaint_class> list){
        productList = list;
        notifyDataSetChanged();
    }


    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_items, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        complaint_class product = productList.get(position);

        //loading the image
        Glide.with(mCtx)
                .load(product.getImage())
                .into(holder.imageView);

        holder.textViewCid.setText("cid : "+product.getName());
        holder.textViewProblem.setText("description: "+product.getproblem());
        holder.textViewuser.setText("by: "+String.valueOf(product.getByuname()));
        holder.textViewreg_date.setText("reg date:"+String.valueOf(product.getregDate()));
        holder.textViewupdate_date.setText("on "+String.valueOf(product.getupdatedDate()));
        holder.textViewRemarks.setText("remarks: "+String.valueOf(product.getremarks()));
        holder.textViewStatus.setText("status: "+String.valueOf(product.getstatus()));
        holder.textViewdept.setText("dept: "+String.valueOf(product.getDept()));
        holder.textViewAdress.setText(" "+String.valueOf(product.getAddress()));
        // holder.textViewPrice.setText(String.valueOf(product.getPrice()));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewCid, textViewProblem, textViewuser, textViewreg_date,textViewStatus,textViewupdate_date,textViewRemarks,textViewdept;
        ImageView imageView;
        TextView textViewAdress;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewCid = itemView.findViewById(R.id.cid_Textview);
            textViewProblem = itemView.findViewById(R.id.problem_Textview);
            textViewuser = itemView.findViewById(R.id.byusername_Textview);
            textViewreg_date = itemView.findViewById(R.id.reg_date_Textview);
            textViewupdate_date = itemView.findViewById(R.id.updateDate_Textview);
            textViewStatus = itemView.findViewById(R.id.status_Textview);
            textViewRemarks=itemView.findViewById(R.id.remark_Textview);
            textViewdept=itemView.findViewById(R.id.dept_Textview);
            textViewAdress=itemView.findViewById(R.id.address_TextView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}