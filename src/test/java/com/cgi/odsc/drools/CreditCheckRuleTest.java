package com.cgi.odsc.drools;

import com.cgi.odsc.drools.model.Customer;
import com.cgi.odsc.drools.model.CustomerCCScore;
import lombok.extern.slf4j.Slf4j;
import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;
import org.hamcrest.core.IsNull;
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
   
    CustomerCCScore customerScore1;
    CustomerCCScore customerScore2;
    CustomerCCScore customerScore3;
    
    @Rule
    public TestName name = new TestName();

    @Autowired
    KnowledgeBase knowledgeBase;

    StatefulKnowledgeSession statefulKnowledgeSession;

    @Before
    public void setup() {

        customer3 = getRuleTwoCustomer();
        customerScore3 = getRuleTwoCustomerWithGoodCCScore();

    }


    @Test
    public void testRuleOnePass() {

        customer1 = getRuleOneCustomer();
        customerScore1 = getRuleOneCustomerWithGoodCCScore();

        printTestMethod();
        statefulKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        statefulKnowledgeSession.insert(customer1);
        statefulKnowledgeSession.insert(customerScore1);
        printCustomerEntityDetails(customer1,customerScore1,true);
        statefulKnowledgeSession.fireAllRules();
        printCustomerEntityDetails(customer1,customerScore1,false);
        assertThat(customer1.isEligibleForNewCard(), is(true));
        assertEquals(500, customer1.getBaseBalance(), 0);
        assertThat(customer1.getRuleName(),is("rule_001"));
    }

    @Test
    public void testRuleOneFail() {

        customer1 = getRuleOneCustomer();
        customerScore1 = getRuleOneCustomerWithBadCCScore();
        printTestMethod();
        statefulKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        statefulKnowledgeSession.insert(customer1);
        statefulKnowledgeSession.insert(customerScore1);
        printCustomerEntityDetails(customer1,customerScore1,true);
        statefulKnowledgeSession.fireAllRules();
        printCustomerEntityDetails(customer1,customerScore1,false);
        assertThat(customer1.isEligibleForNewCard(), is(false));
        assertEquals(0, customer1.getBaseBalance(), 0);
        assertThat(customer1.getRuleName(), nullValue());
    }

    @Test
    public void testRuleTwoPass(){
        customer2 = getRuleTwoCustomer();
        customerScore2 = getRuleTwoCustomerWithGoodCCScore();
        printTestMethod();
        statefulKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        statefulKnowledgeSession.insert(customer2);
        statefulKnowledgeSession.insert(customerScore2);
        printCustomerEntityDetails(customer2,customerScore2,true);
        statefulKnowledgeSession.fireAllRules();
        printCustomerEntityDetails(customer2,customerScore2,false);
        assertThat(customer2.isEligibleForNewCard(), is(true));
        assertEquals(500, customer2.getBaseBalance(), 0);
        assertThat(customer2.getRuleName(), is("rule_002"));

    }


    @Test
    public void testRuleTwoFail(){
        customer2 = getRuleTwoCustomer();
        customerScore2 = getRuleTwoCustomerWithBadCCScore();
        printTestMethod();
        statefulKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        statefulKnowledgeSession.insert(customer2);
        statefulKnowledgeSession.insert(customerScore2);
        printCustomerEntityDetails(customer2,customerScore2,true);
        statefulKnowledgeSession.fireAllRules();
        printCustomerEntityDetails(customer2,customerScore2,false);
        assertThat(customer2.isEligibleForNewCard(), is(false));
        assertEquals(0, customer2.getBaseBalance(), 0);
        assertThat(customer2.getRuleName(), nullValue());

    }


    @Test
    public void testRuleThreePass(){
        customer3 = getRuleThreeCustomer();
        customerScore3 = getRuleThreeCustomerWithGoodCCScore();
        printTestMethod();
        statefulKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        statefulKnowledgeSession.insert(customer3);
        statefulKnowledgeSession.insert(customerScore3);
        printCustomerEntityDetails(customer3,customerScore3,true);
        statefulKnowledgeSession.fireAllRules();
        printCustomerEntityDetails(customer3,customerScore3,false);
        assertThat(customer3.isEligibleForNewCard(), is(true));
        assertEquals(1000, customer3.getBaseBalance(), 0);
        assertThat(customer3.getRuleName(), is("rule_003"));

    }

    @Test
    public void testRuleThreeFail(){
        customer3 = getRuleTwoCustomer();
        customerScore3 = getRuleThreeCustomerWithBadCCScore();
        printTestMethod();
        statefulKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        statefulKnowledgeSession.insert(customer3);
        statefulKnowledgeSession.insert(customerScore3);
        printCustomerEntityDetails(customer3,customerScore3,true);
        statefulKnowledgeSession.fireAllRules();
        printCustomerEntityDetails(customer3,customerScore3,false);
        assertThat(customer3.isEligibleForNewCard(), is(false));
        assertEquals(0, customer3.getBaseBalance(), 0);
        assertThat(customer3.getRuleName(), nullValue());

    }


   

    private void printTestMethod(){
        log.info("Executing: {}", name.getMethodName());
    }

    private void printCustomerEntityDetails(Customer customer, CustomerCCScore customerCCScore, boolean isBefore){

        String placeHolder = isBefore? "before" : "after";
        log.info("Customer details {}, rules applied: {} and CCScore {}", placeHolder, customer,customerCCScore);

    }


    private Customer getRuleOneCustomer() {

        return Customer.builder()
                .name("Allan")
                .age(20)
                .creditCardHolder(false)
                .hasJob(true)
                .build();
    }

    private CustomerCCScore getRuleOneCustomerWithGoodCCScore() {
        return CustomerCCScore.builder()
                .score(550)
                .badCreditCustomer(false)
                .build();
    }

    private CustomerCCScore getRuleOneCustomerWithBadCCScore() {
        return CustomerCCScore.builder()
                .score(550)
                .badCreditCustomer(true)
                .build();
    }

    private CustomerCCScore getRuleTwoCustomerWithGoodCCScore() {
        return CustomerCCScore.builder()
                .score(920)
                .badCreditCustomer(true)
                .build();
    }

    private Customer getRuleTwoCustomer() {
        return Customer.builder()
                .name("Bob")
                .age(35)
                .creditCardHolder(false)
                .hasJob(false)
                .build();
    }

    private CustomerCCScore getRuleTwoCustomerWithBadCCScore() {

        return CustomerCCScore.builder()
                .score(800)
                .badCreditCustomer(true)
                .build();
    }


    public Customer getRuleThreeCustomer() {
        return Customer.builder()
                .name("Jason")
                .age(56)
                .creditCardHolder(true)
                .currentCreditLimit(5800.0)
                .hasJob(false)
                .build();
    }

    public CustomerCCScore getRuleThreeCustomerWithGoodCCScore() {
        return CustomerCCScore.builder()
                .score(990)
                .badCreditCustomer(false)
                .build();
    }

    public CustomerCCScore getRuleThreeCustomerWithBadCCScore() {
        return CustomerCCScore.builder()
                .score(500)
                .badCreditCustomer(false)
                .build();
    }
}
