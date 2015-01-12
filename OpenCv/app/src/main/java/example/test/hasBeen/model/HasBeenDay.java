package example.test.hasBeen.model;

/**
 * Created by zuby on 2015-01-09.
 */
public class HasBeenDay {
    int id;
    String title;
    String description;
    int photo_count;
    String date;
    String country;
    String city;
    int main_photo_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPhoto_count() {
        return photo_count;
    }

    public void setPhoto_count(int photo_count) {
        this.photo_count = photo_count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getMain_photo_id() {
        return main_photo_id;
    }

    public void setMain_photo_id(int main_photo_id) {
        this.main_photo_id = main_photo_id;
    }

    public HasBeenDay(int id, String title ,String description, int photo_count, String date, String country,String city,int main_photo_id){
        this.id = id;
        this.title = title;
        this.description = description;
        this.photo_count = photo_count;
        this.date = date;
        this.country = country;
        this.city = city;
        this.main_photo_id = main_photo_id;
    }
}
