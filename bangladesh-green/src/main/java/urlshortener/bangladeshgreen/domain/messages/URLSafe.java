package urlshortener.bangladeshgreen.domain.messages;

/**
 * Created by guytili on 08/01/2016.
 */
public class URLSafe extends JsonResponse{

    private String message;
    private String uri;

    public URLSafe(String message, String uri){
        super("safe");
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
