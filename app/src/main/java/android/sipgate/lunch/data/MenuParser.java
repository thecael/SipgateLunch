package android.sipgate.lunch.data;

import android.sipgate.lunch.exception.MenuParsingException;
import android.sipgate.lunch.ormlite.Meal;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Simple html parser to get meals infos from the sipgate altepost website.
 *
 * @author schafm
 */
public class MenuParser {
    private String mUrl = "http://altepost.sipgate.net/";
    private int mCalendarWeek;
    private int mYear;
    private OkHttpClient mClient;

    public MenuParser() {
        mClient = new OkHttpClient();
    }

    public MenuParser(int calendarWeek, int year) {
        mClient = new OkHttpClient();
        this.mCalendarWeek = calendarWeek;
        this.mYear = year;
        this.mUrl += "/?kw=" + mCalendarWeek + "/" + mYear;
    }

    public List<Meal> parseMenuSite() throws IOException, ParseException, MenuParsingException {
        List<Meal> mealsList = new ArrayList<>();
        Request request = new Request.Builder().url(mUrl).build();
        Response response = mClient.newCall(request).execute();
        Document doc = Jsoup.parse(response.body().string());

        if (doc.select("span.kw").isEmpty()) {
            return mealsList;
        }

        String kw = doc.select("span.kw").get(0).text();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date startDate = sdf.parse(kw.substring(kw.indexOf("(") + 1, kw.indexOf("(") + 11));
        Calendar calendar = new GregorianCalendar();

        for (int i = 1; i <= 5; i++) {
            calendar.setTime(startDate);
            calendar.add(Calendar.DATE, i-1);
            Elements mealsDay = doc.select("div#meals-day" + i);
            mealsList.addAll(parseMealsForDate(mealsDay, calendar.getTime()));
        }

        return mealsList;
    }

    private List<Meal> parseMealsForDate(Elements mealsForDay, Date date) throws MenuParsingException {
        List<Meal> mealsList = new ArrayList<>();
        Element mealElement = mealsForDay.get(0);
        Elements foodBoxes = mealElement.select("div.foodBox");
        for (int i = 0; i < foodBoxes.size(); i++) {
            Meal meal = parseMeal(date, foodBoxes.get(i));
            mealsList.add(meal);
        }

        return mealsList;
    }

    private Meal parseMeal(Date date, Element foodBox) throws MenuParsingException {
        Meal meal = new Meal();
        String[] divClasses = foodBox.attr("class").split(" ");
        String name = foodBox.select("div.text").select("span").get(0).text();

        for (String divClass : divClasses) {
            if (divClass.toLowerCase().equals("vegi")) {
                meal.setType(Meal.TYPE_VEGI);
            } else if (divClass.toLowerCase().equals("carne")) {
                meal.setType(Meal.TYPE_CARNE);
            } else if (divClass.toLowerCase().equals("general")) {
                meal.setType(Meal.TYPE_GENERAL);
            } else if (divClass.toLowerCase().equals("fisch")) {
                meal.setType(Meal.TYPE_FISH);
            } else if (divClass.toLowerCase().equals("breakfast")) {
                meal.setType(Meal.TYPE_BREAKFAST);
            } else if (divClass.toLowerCase().equals("dessert")) {
                meal.setType(Meal.TYPE_DESSERT);
            } else if (divClass.toLowerCase().startsWith("meal")) {
                try {
                    String mealNo = divClass.substring(4, divClass.length());
                    meal.setId(Integer.parseInt(mealNo));
                } catch (Exception e) {
                    throw new MenuParsingException("error trying to parse meal-number!");
                }
            }
        }
        meal.setDate(date);
        meal.setName(name);
        meal.setCalendarWeek(mCalendarWeek);
        meal.setYear(mYear);
        return meal;
    }
}
