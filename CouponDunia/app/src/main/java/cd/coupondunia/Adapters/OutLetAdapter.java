package cd.coupondunia.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import cd.coupondunia.Objects.OutLet;
import cd.coupondunia.R;
import cd.coupondunia.Utils.StaticUtils;

/**
 * Created by anand on 07/02/16.
 */
public class OutLetAdapter extends RecyclerView.Adapter<OutLetAdapter.OutLetRowHolder> {
    private List<OutLet> feedItemList;
    private Context mContext;

    private final String walk_unicode="\uD83D\uDEB6";
    private final String run_unicode="\uD83C\uDFC3";
    private final String car_unicode="\uD83D\uDE95";


    public OutLetAdapter(Context context, List<OutLet> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public OutLetRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);

        OutLetRowHolder viewHolder = new OutLetRowHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final OutLetRowHolder customViewHolder, int i) {
        final OutLet outLet = feedItemList.get(i);

        //Download image using picasso library
        Picasso.with(mContext).load(outLet.logoUrl)
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(customViewHolder.thumbnail,new Callback() {

            @Override public void onSuccess() {

                outLet.dominantColor=getDominantColor(customViewHolder.thumbnail);
                customViewHolder.contentLayout.setBackgroundColor(outLet.dominantColor);
                customViewHolder.thumbnail.setBackgroundColor(outLet.dominantColor);

            }

            @Override public void onError() {
            }
        });



       customViewHolder.contentLayout.setBackgroundColor(outLet.dominantColor);
       customViewHolder.thumbnail.setBackgroundColor(outLet.dominantColor);

       String scategories="";

        for(int j=0;j<outLet.categories.size();j++)
        {
            scategories = scategories+"\u2022 "+outLet.categories.get(j).name+" ";
        }



        Picasso.with(mContext).load(outLet.coverUrl)
                .into(customViewHolder.coverImageView);



       // AutofitHelper.create(customViewHolder.titleTextView);
        customViewHolder.titleTextView.setText(outLet.name);
        customViewHolder.categoriesTextView.setText(scategories);

        String soffers=String.valueOf(outLet.numOfCoupons);
        if(outLet.numOfCoupons>1)
            soffers+=" offers";
        else
            soffers+=" offer";

        customViewHolder.countTextView.setText(soffers);

        customViewHolder.distanceTextView.setText(getUniCodeByDistance(outLet.distance)+" "+ StaticUtils.getDistanceInString(outLet.distance) + " "+outLet.neighbourhoodName);

        customViewHolder.likeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean is_liked=Prefs.getBoolean("is_liked_"+outLet.id,false);
                if(!is_liked)
                {
                    Prefs.putBoolean("is_liked_"+outLet.id,true);
                    customViewHolder.likeButton.setSelected(true);
                }
                else
                {
                    Prefs.putBoolean("is_liked_"+outLet.id,false);
                    customViewHolder.likeButton.setSelected(false);
                }


            }
        });

        if(Prefs.getBoolean("is_liked_"+outLet.id,false))
        {
            Prefs.putBoolean("is_liked_"+outLet.id,true);
            customViewHolder.likeButton.setSelected(true);
        }
        else
        {
            Prefs.putBoolean("is_liked_"+outLet.id,false);
            customViewHolder.likeButton.setSelected(false);
        }

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class OutLetRowHolder extends RecyclerView.ViewHolder {

        protected ImageView coverImageView;
        protected ImageView thumbnail;
        protected TextView titleTextView;
        protected RelativeLayout contentLayout;
        protected TextView categoriesTextView;
        protected TextView countTextView;
        protected TextView distanceTextView;
        protected ImageButton likeButton;

        public OutLetRowHolder(View view) {
            super(view);

            this.coverImageView=(ImageView) view.findViewById(R.id.cover_image);
            this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            this.titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            this.contentLayout=(RelativeLayout)view.findViewById(R.id.contentLayout);
            this.categoriesTextView =(TextView)view.findViewById(R.id.categoriesTextView);
            this.countTextView=(TextView)view.findViewById(R.id.countTextView);
            this.distanceTextView=(TextView)view.findViewById(R.id.distanceTextView);
            this.likeButton=(ImageButton)view.findViewById(R.id.likeImageButton);
        }

    }


    public int getDominantColor(ImageView imageView)
    {
        int color =((BitmapDrawable)imageView.getDrawable()).getBitmap().getPixel(0,0);

        if(color== Color.parseColor("#ffffff"))
            color=Color.parseColor("#f8f8f8");

        return color;
    }

    public String getUniCodeByDistance(Float distance)
    {
        if(distance<1000)
        {
            return walk_unicode;
        }

        if(distance<3000)
        {
            return run_unicode;
        }

        return  car_unicode;

    }
}
