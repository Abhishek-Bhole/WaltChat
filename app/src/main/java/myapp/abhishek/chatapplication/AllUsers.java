package myapp.abhishek.chatapplication;

public class AllUsers {

    public String name;
    public String image;
    public String status;
    public String thumbnail;

    public AllUsers()
    {

    }

    public AllUsers(String name, String image, String status,String thumbnail) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumbnail=thumbnail;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

}
