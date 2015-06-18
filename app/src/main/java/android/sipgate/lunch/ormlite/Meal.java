package android.sipgate.lunch.ormlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Meal entity
 * @author schafm
 */
@DatabaseTable(tableName = Meal.TABLE_NAME)
public class Meal implements Serializable, Comparable {
    public static final String TABLE_NAME = "meal";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_TYPE = "type";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_CAL_WEEK = "calweek";
    public static final String COLUMN_NAME_CAL_YEAR = "year";

    public static final int TYPE_GENERAL = 1;
    public static final int TYPE_CARNE = 2;
    public static final int TYPE_VEGI = 3;
    public static final int TYPE_FISH = 4;
    public static final int TYPE_BREAKFAST = 5;
    public static final int TYPE_DESSERT = 6;

    @DatabaseField(columnName = COLUMN_NAME_ID, id = true, canBeNull = false)
    private int id;

    @DatabaseField(columnName = COLUMN_NAME_TYPE, canBeNull = false)
    private int type;

    @DatabaseField(columnName = COLUMN_NAME_NAME, canBeNull = false)
    private String name;

    @DatabaseField(columnName = COLUMN_NAME_DATE, canBeNull = false)
    private Date date;

    @DatabaseField(columnName = COLUMN_NAME_CAL_WEEK, canBeNull = false)
    private int calendarWeek;

    @DatabaseField(columnName = COLUMN_NAME_CAL_YEAR, canBeNull = false)
    private int year;

    public Meal() {
    }

    public Meal(int id, int type, String name, Date date, int calendarWeek, int year) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.date = date;
        this.calendarWeek = calendarWeek;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCalendarWeek() {
        return calendarWeek;
    }

    public void setCalendarWeek(int calendarWeek) {
        this.calendarWeek = calendarWeek;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", calendarWeek=" + calendarWeek +
                ", year=" + year +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !(o instanceof Meal)) {
            return -1;
        } else {
            Meal m = (Meal) o;
            if (m.getDate() == null) {
                return -1;
            } else if (getDate() == null) {
                return 1;
            } else if (m.getDate().after(getDate())) {
                return -1;
            } else if (m.getDate().before(getDate())) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
