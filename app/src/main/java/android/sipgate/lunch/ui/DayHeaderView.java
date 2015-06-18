package android.sipgate.lunch.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.sipgate.lunch.sipgatelunch.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DayHeaderView extends LinearLayout {
    private Date mDate;

    public DayHeaderView(Context context, Date date) {
        super(context);
        this.mDate = date;
        init();
    }

    public DayHeaderView(Context context, Date date, AttributeSet attrs) {
        super(context, attrs);
        this.mDate = date;
        init();
    }

    public DayHeaderView(Context context, Date date, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDate = date;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.calendar_day, this);
        TextView dayText = (TextView) this.findViewById(R.id.dayText);
        TextView dayNumber = (TextView) this.findViewById(R.id.daynr);
        String dayName = new SimpleDateFormat("EEEE", Locale.GERMAN).format(mDate);
        String monthName = new SimpleDateFormat("MMMM", Locale.GERMAN).format(mDate);
        dayText.setText(dayName);
        dayNumber.setText(mDate.getDate() + ". " + monthName);
    }

    public void setDate(Date date) {
        this.mDate = date;
        init();
    }
}
