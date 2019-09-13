package com.line.vertical.marqueelayout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.line.marqueelayout.VerticalMarqueeAdapter;
import com.line.marqueelayout.VerticalMarqueeLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MyAdapter marqueeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VerticalMarqueeLayout marqueeLayout = findViewById(R.id.marquee_layout);
        marqueeAdapter = new MyAdapter(this);
        marqueeLayout.setAdapter(marqueeAdapter);
        marqueeLayout.start();

    }

    public static class MyViewHolder extends VerticalMarqueeLayout.ViewHolder {
        ImageView imageView;
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.action_image);
            textView = itemView.findViewById(R.id.tv_desc);
        }
    }

    public static class MyAdapter extends VerticalMarqueeAdapter<MyViewHolder> {

        private Context context;
        List<String> data;
        List<String> urls;

        public MyAdapter(Context context) {
            this.context = context;
            List<String> dataTemp = Arrays.asList("1", "2", "3", "4");
            data = new ArrayList<>(dataTemp);

            List<String> urlTemp = Arrays.asList("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3894282581,1292381293&fm=26&gp=0.jpg",
                    "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1823007526,3538577712&fm=26&gp=0.jpg",
                    "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2657596156,4172056089&fm=26&gp=0.jpg",
                    "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3309486999,346953630&fm=26&gp=0.jpg"
            );
            urls = new ArrayList<>(urlTemp);
        }


        public void remove() {
            data.remove(0);
            urls.remove(0);
        }

        public void add() {
            data.add("新增" + System.currentTimeMillis());
            urls.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3894282581,1292381293&fm=26&gp=0.jpg");
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_marquee_ad, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.textView.setText(data.get(position));
            Picasso.get().load(urls.get(position)).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }


    public void updateData(View view) {
        if (System.currentTimeMillis() % 2 == 0) {
            marqueeAdapter.add();
        } else {
            marqueeAdapter.remove();
        }
    }
}
