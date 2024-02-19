package com.techelevator.tenmo.services;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class UserTokenService {

    protected String baseUrl;
    protected final RestTemplate restTemplate = new RestTemplate();

    protected String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public HttpEntity<Void> makeAuthEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }


}
