package com.example.collectqr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.location.Location;

import com.example.collectqr.data.MapViewController;
import com.mapbox.geojson.Point;

import org.junit.Test;

import java.util.List;

public class MapViewControllerTest {

    private static final double lat_true = 53.260;
    private static final double lon_true = -113.525;

    // Inverted location to where we don't have any data to display (likely)
    private static final double lat_false = -53.260;
    private static final double lon_false = 113.525;

    private static final double point_lat = 52.526191;
    private static final double point_lon = -113.525829;

    @Test
    public void testNearbyQrQuery() {
        // Creating a test location with an empty provider
        Location testLocation = new Location("");
        testLocation.setLatitude(lat_true);
        testLocation.setLongitude(lon_true);

        Point testPoint = Point.fromLngLat(point_lon, point_lat);

        List<Point> POIList = new MapViewController().getNearbyQRCodes(testLocation);

        assertTrue(POIList.contains(testPoint));

        // TODO Use with instrumentation tests to have a context
        // TODO add test case that is in DB but outside of search radius
    }
}
