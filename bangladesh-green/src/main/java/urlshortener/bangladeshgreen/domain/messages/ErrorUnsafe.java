package urlshortener.bangladeshgreen.domain.messages;

/**
 * Created by Bangladesh green on 08/01/2016.
 */
public class ErrorUnsafe extends JsonResponse{

    private String message;
    private String uri;
    public ErrorUnsafe(){
        super("unsafe");
    }

    public ErrorUnsafe(String message, String uri){
        super("unsafe");
        this.message = message;
        this.uri = uri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUri(){ return this.uri; }

    public void setUri(String uri){ this.uri = uri;  }
}
