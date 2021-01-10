package rocks.magical.camunda.helper;

public class Base64Image {
    private String imageString;
    private String filetype;

    public Base64Image() {
    }

    public Base64Image(String imageString, String filetype) {
        this.imageString = imageString;
        this.filetype = filetype;
    }

    public String getImageString() {
        return imageString;
    }

    public String getFiletype() {
        return filetype;
    }
}
