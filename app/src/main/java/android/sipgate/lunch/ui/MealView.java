package android.sipgate.lunch.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.sipgate.lunch.sipgatelunch.R;
import android.sipgate.lunch.ormlite.Meal;

public class MealView extends LinearLayout {
    private ImageView mImageView;
    private TextView mMealTitle;
    private View mTypeView;
    private String mUrl;

    public MealView(Context context) {
        super(context);

        LayoutInflater.from(getContext()).inflate(
                R.layout.meal_view, this);

        mImageView = (ImageView) this.findViewById(R.id.imageView);
        mMealTitle = (TextView) this.findViewById(R.id.mealTitle);
        mTypeView = (View) this.findViewById(R.id.typeView);
    }

    public MealView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(
                R.layout.meal_view, this);

        mImageView = (ImageView) this.findViewById(R.id.imageView);
        mMealTitle = (TextView) this.findViewById(R.id.mealTitle);
        mTypeView = (View) this.findViewById(R.id.typeView);
    }

    public MealView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(getContext()).inflate(
                R.layout.meal_view, this);

        mImageView = (ImageView) this.findViewById(R.id.imageView);
        mMealTitle = (TextView) this.findViewById(R.id.mealTitle);
        mTypeView = (View) this.findViewById(R.id.typeView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setMealTitle(String title) {
        mMealTitle.setText(title);
    }

    public String getMealTitle() {
        return mMealTitle.getText().toString();
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setType(int type) {
        switch (type) {
            case Meal.TYPE_CARNE:
                mTypeView.setBackgroundColor(getResources().getColor(R.color.type_carne));
                break;
            case Meal.TYPE_VEGI:
                mTypeView.setBackgroundColor(getResources().getColor(R.color.type_vegi));
                break;
            case Meal.TYPE_FISH:
                mTypeView.setBackgroundColor(getResources().getColor(R.color.type_fish));
                break;
            case Meal.TYPE_BREAKFAST:
                mTypeView.setBackgroundColor(getResources().getColor(R.color.type_breakfast));
                break;
            case Meal.TYPE_DESSERT:
                mTypeView.setBackgroundColor(getResources().getColor(R.color.type_dessert));
                break;
            case Meal.TYPE_GENERAL:
            default:
                mTypeView.setBackgroundColor(getResources().getColor(R.color.type_general));
                break;
        }
    }
}

