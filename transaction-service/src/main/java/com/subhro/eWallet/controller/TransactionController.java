package com.subhro.eWallet.controller;

import com.subhro.eWallet.request.TransactionRequest;
import com.subhro.eWallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/trasaction")
    public String doTrasaction(@RequestBody TransactionRequest transactionRequest) throws Exception {
        if(!transactionRequest.validate()){
            throw new Exception("Invalid Request!!!");
        }

        return transactionService.createTransaction(transactionRequest);
    }
}
