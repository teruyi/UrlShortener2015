package urlshortener.bangladeshgreen.domain.messages;

/**
 * Represents a JSON Success Response that follows the standard.
 */
public class SuccessResponse<DataType> extends JsonResponse {

    private DataType data;

    public SuccessResponse(){
        super("success");
    }


    public SuccessResponse(DataType data){
        super("success");
        this.data = data;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DataType getData() {
        return data;
    }

    public void setData(DataType data) {
        this.data = data;
    }
}
