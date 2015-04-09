package co.hasBeen.model.network;

import java.util.List;

/**
 * Created by 주현 on 2015-04-08.
 */
public class PlaceAutocomplete {
    String status;
    List<Predictions> predictions;

    public String getStatus() {
        return status;
    }

    public List<Predictions> getPredictions() {
        return predictions;
    }

    public class Predictions {
        String description;
        String id;
        String place_id;

        public String getDescription() {
            return description;
        }

        public String getId() {
            return id;
        }

        public String getPlace_id() {
            return place_id;
        }
    }
}
