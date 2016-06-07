package com.cgi.odsc.drools;

import com.cgi.odsc.drools.model.Customer;
import com.cgi.odsc.drools.model.CustomerCCScore;
import lombok.extern.slf4j.Slf4j;
import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by dingl on 03/06/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = KnowledgeBaseExecutor.class)
@Slf4j
public class CreditCheckRuleTest {

    Customer customer1;
    Customer customer2;
    Customer customer3;
    Customer customer4;

    CustomerCCScore customerScore1;
    CustomerCCScore customerScore2;
    CustomerCCScore customerScore3;
    CustomerCCScore customerScore4;


    @Rule
    public TestName name = new TestName();

    @Autowired
    KnowledgeBase knowledgeBase;

    StatefulKnowledgeSession statefulKnowledgeSession;

    @Before
    public void setup() {

        customer1 = getRuleOneCustomerWithGoodCredit();
        customerScore1 = getRuleOneCustomerWithGoodCCScore();
        customer2 = getRuleOneCustomerWithGoodCredit();
        customerScore2 = getRuleOneCustomerWithBadCCScore();
//        customer3 = getRuleTwoCustomerWithGoodCredit();
//        customer4 = getRuleTwoCustomerWithBadCredit();
    }


    @Test
    public void testRuleOnePass() {

        printTestMethod();
        statefulKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        statefulKnowledgeSession.insert(customer1);
        statefulKnowledgeSession.insert(customerScore1);

        printCustomerEntityDetails(customer1,true);
        statefulKnowledgeSession.fireAllRules();
        printCustomerEntityDetails(customer1,false);
        assertThat(customer1.isEligibleForNewCard(), is(true));
        assertEquals(500, customer1.getBaseBalance(), 0);
        assertThat(customer1.getRuleName(),is("rule_001"));
    }

    @Test
    public void testRuleOneFail() {

        printTestMethod();
        statefulKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        statefulKnowledgeSession.insert(customer2);
        statefulKnowledgeSession.insert(customerScore2);
        printCustomerEntityDetails(customer2,true);
        statefulKnowledgeSession.fireAllRules();
        printCustomerEntityDetails(customer2,false);
        assertThat(customer2.isEligibleForNewCard(), is(false));
        assertEquals(0, customer2.getBaseBalance(), 0);
        assertThat(customer2.getRuleName(), nullValue());
    }

    private Customer getRuleOneCustomerWithGoodCredit() {

        return Customer.builder()
                .name("Allan")
                .age(20)
                .creditCardHolder(false)
                .hasJob(true)
                .build();
    }

    public CustomerCCScore getRuleOneCustomerWithGoodCCScore() {
        return CustomerCCScore.builder()
                .score(550)
                .badCreditCustomer(false)
                .build();
    }

    public CustomerCCScore getRuleOneCustomerWithBadCCScore() {
         return CustomerCCScore.builder()
                .score(550)
                .badCreditCustomer(true)
                .build();
    }

    private void printTestMethod(){
        log.info("Executing: {}", name.getMethodName());
    }

    private void printCustomerEntityDetails(Customer customer, boolean isBefore){

        String placeHolder = isBefore? "before" : "after";
        log.info("Customer details {} rules applied: {}", placeHolder, customer);

    }

  /*  public Customer getRuleOneCustomerWithBadCredit() {

        return Customer.builder()
                .name("David")
                .age(20)
                .creditCardHolder(false)
                .badCreditCustomer(true)
                .numberOfCreditCards(2)
                .build();
    }

    public Customer getRuleTwoCustomerWithGoodCredit() {
        return Customer.builder()
                .name("Joan")
                .age(25)
                .badCreditCustomer(true)
                .creditCardHolder(true)
                .creditLimit(700)
                .numberOfCreditCards(3)
                .build();
    }

    public Customer getRuleTwoCustomerWithBadCredit() {
        return Customer.builder()
                .name("Mike")
                .age(36)
                .badCreditCustomer(true)
                .creditCardHolder(true)
                .creditLimit(200)
                .numberOfCreditCards(3)
                .build();
    }*/

}
