package com.cgi.odsc.drools.model;

import lombok.Builder;
import lombok.Data;

/**
 * Created by dingl on 02/06/16.
 */
@Data
@Builder
public class CustomerCCScore {

    private Integer score;
    private Double riskRate;
    private Double riskRatio;
    private boolean badCreditCustomer;

}
