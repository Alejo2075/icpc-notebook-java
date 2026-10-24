package notebook;

/** Three-dimensional vector operations and spherical distance. */
public final class Geometry3D {
    private Geometry3D() {}
    public record Vector(double x, double y, double z) {
        public Vector add(Vector other) { return new Vector(x+other.x,y+other.y,z+other.z); }
        public Vector subtract(Vector other) { return new Vector(x-other.x,y-other.y,z-other.z); }
        public Vector scale(double factor) { return new Vector(x*factor,y*factor,z*factor); }
        public double dot(Vector other) { return x*other.x+y*other.y+z*other.z; }
        public Vector cross(Vector other) {
            return new Vector(y*other.z-z*other.y,z*other.x-x*other.z,x*other.y-y*other.x);
        }
        public double norm() { return Math.sqrt(dot(this)); }
        public double distance(Vector other) { return subtract(other).norm(); }
    }

    /** Signed volume of tetrahedron (origin,a,b,c). */
    public static double signedTetrahedronVolume(Vector a, Vector b, Vector c) {
        return a.dot(b.cross(c))/6.0;
    }

    /** Great-circle distance in kilometers using the haversine formula. */
    public static double greatCircleKilometers(double latitude1, double longitude1,
                                                double latitude2, double longitude2) {
        if (Math.abs(latitude1)>90 || Math.abs(latitude2)>90) throw new IllegalArgumentException("invalid latitude");
        double lat1=Math.toRadians(latitude1), lat2=Math.toRadians(latitude2);
        double dLat=lat2-lat1, dLon=Math.toRadians(longitude2-longitude1);
        double haversine=Math.sin(dLat/2)*Math.sin(dLat/2)
                +Math.cos(lat1)*Math.cos(lat2)*Math.sin(dLon/2)*Math.sin(dLon/2);
        return 6371.0088*2*Math.asin(Math.sqrt(Math.max(0,Math.min(1,haversine))));
    }
}
