package com.example.librarylocator;

public class FindClosestLocation {

    //calculcates distance between 2 co-ordinates (lat longs)

    public static double distance(Double lat1, Double lon1, Double lat2, Double lon2, MainActivity mainAct) {
        Double theta = lon1 - lon2;
        Double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private static Double deg2rad(Double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static Double rad2deg(Double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
