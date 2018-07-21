package surveytoscr;

/**
 * @author Matti Syrjanen
 */
public class Description {

    private final String descriptionName;
    private String layer, colour;

    public Description(String descriptionName, String layer, String colour) {
        this.layer = layer;
        this.colour = colour;
        this.descriptionName = descriptionName;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getLayer() {
        return layer;
    }

    public String getColour() {
        return colour;
    }
    
    @Override
    public String toString() {
        return descriptionName;
    }
}
