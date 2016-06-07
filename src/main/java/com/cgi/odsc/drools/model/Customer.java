package com.cgi.odsc.drools.model;

import lombok.Builder;
import lombok.Data;

/**
 * Created by dingl on 01/06/16.
 */
@Data
@Builder
public class Customer {

    private String name;
    private Integer age;
    private boolean creditCardHolder;

    private double currentCreditLimit;
    private boolean hasJob;

    private boolean eligibleForNewCard;
    private double baseBalance;
    private String ruleName;

}
