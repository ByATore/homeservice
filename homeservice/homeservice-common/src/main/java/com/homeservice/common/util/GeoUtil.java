package com.homeservice.common.util;

public class GeoUtil {
    
    private static final double EARTH_RADIUS = 6371000.0;
    
    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
    
    public static String formatDistance(double distance) {
        if (distance < 1000) {
            return String.format("%.0fm", distance);
        } else {
            return String.format("%.1fkm", distance / 1000);
        }
    }
    
    public static boolean isWithinRange(double lat1, double lng1, double lat2, double lng2, double radiusMeters) {
        return calculateDistance(lat1, lng1, lat2, lng2) <= radiusMeters;
    }
    
    public static double[] getBoundingBox(double lat, double lng, double radiusMeters) {
        double latDelta = (radiusMeters / EARTH_RADIUS) * (180.0 / Math.PI);
        double lngDelta = (radiusMeters / (EARTH_RADIUS * Math.cos(Math.toRadians(lat)))) * (180.0 / Math.PI);
        
        return new double[]{
            lat - latDelta,
            lng - lngDelta,
            lat + latDelta,
            lng + lngDelta
        };
    }
}