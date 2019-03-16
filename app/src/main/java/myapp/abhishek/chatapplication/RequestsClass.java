package myapp.abhishek.chatapplication;

public class RequestsClass {



    private String Request_Type;

    public RequestsClass()
    {

    }

    public RequestsClass(String request_Type) {
        Request_Type = request_Type;
    }

    public String getRequest_Type() {
        return Request_Type;
    }

    public void setRequest_Type(String request_Type) {
        Request_Type = request_Type;
    }


}
