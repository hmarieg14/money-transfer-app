package com.techelevator.tenmo.services;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class TransferService extends UserTokenService{


        public TransferService(String baseUrl) {
                this.baseUrl = baseUrl + "transfers/";

        }

        public Transfer createTransfer(TransferDto dto){
                Transfer transfer = null;
                try {
                        ResponseEntity<Transfer> response =
                                restTemplate.exchange(baseUrl, HttpMethod.POST, postAuthEntity(dto), Transfer.class);
                        transfer = response.getBody();
                }catch (RestClientResponseException | ResourceAccessException e){
                        BasicLogger.log(e.getMessage());
                }
                return transfer;
        }

        public Transfer retrieveTransferDetails(Long transferId) {
                Transfer transfer = null;
                try {
                        ResponseEntity<Transfer> response =
                                restTemplate.exchange(baseUrl + transferId, HttpMethod.GET,
                                        makeAuthEntity(), Transfer.class);
                        transfer = response.getBody();
                } catch (RestClientResponseException | ResourceAccessException e) {
                        BasicLogger.log(e.getMessage());
                }
                return transfer;
        }

        public Transfer[] retrieveAllTransfers() {
                Transfer[] transfers = null;
                try {
                        ResponseEntity<Transfer[]> response =
                                restTemplate.exchange(baseUrl, HttpMethod.GET,
                                        makeAuthEntity(), Transfer[].class);
                        transfers = response.getBody();
                } catch (RestClientResponseException | ResourceAccessException e) {
                        BasicLogger.log(e.getMessage());
                }
                return transfers;
        }

        public Transfer transferStatusById(Long id, String status){
                Transfer transfer = null;
                try{
                        ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + id, HttpMethod.PUT,
                                statusEntity(status), Transfer.class);
                        transfer = response.getBody();
                } catch (RestClientResponseException | ResourceAccessException e) {
                        BasicLogger.log(e.getMessage());
                }
                return transfer;
        }

        public HttpEntity<TransferDto> postAuthEntity(TransferDto transfer){
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(authToken);
                return new HttpEntity<>(transfer, headers);
        }

        public HttpEntity<StatusUpdateDto> statusEntity(String status){
                HttpHeaders header = new HttpHeaders();
                header.setContentType(MediaType.APPLICATION_JSON);
                header.setBearerAuth(authToken);
                return new HttpEntity<>(new StatusUpdateDto(status), header);
        }


}
