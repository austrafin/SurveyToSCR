package surveytoscr;

/**
 * @author Matti Syrjanen
 */
public class SurveyPoint {

    private final String id, x, y, z, description;
    
    public SurveyPoint(String id, String x, String y, String z, String description) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getZ() {
        return z;
    }

    public String getDescription() {
        return description;
    }
}
