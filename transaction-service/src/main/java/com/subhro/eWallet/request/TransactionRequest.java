package com.subhro.eWallet.request;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {

    private String purpose;
    private Double amount;
    private String sender;
    private String receiver;

    public boolean validate(){
        if(StringUtils.isEmpty(sender)||
                StringUtils.isEmpty(receiver)||
                this.amount==0||this.amount==null){
            return false;
        }

        return true;
    }
}
