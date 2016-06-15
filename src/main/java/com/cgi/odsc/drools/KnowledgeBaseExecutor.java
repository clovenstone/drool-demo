package com.cgi.odsc.drools;

import lombok.extern.slf4j.Slf4j;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.*;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.UrlResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by dingl on 02/06/16.
 */
@Configuration
@Slf4j
public class KnowledgeBaseExecutor {

    @Bean
    public KnowledgeBase knowledgeBase() throws Exception {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        //kbuilder.add(ResourceFactory.newClassPathResource("creditCheckRules.drl"), ResourceType.DRL);
        addRulesFromRemote(kbuilder);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();

        if (errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                log.error(""+error.getMessage());
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        return knowledgeBase;
    }

    private void addRulesFromRemote(KnowledgeBuilder kbuilder){

        String url = "http://localhost:8080/drools-guvnor-5.5.0/rest/packages/creditCheckRules/source";
        UrlResource resource = (UrlResource) ResourceFactory.newUrlResource(url);
        resource.setBasicAuthentication("enabled");
        resource.setUsername("admin");
        resource.setPassword("admin");
        kbuilder.add(resource, ResourceType.DRL);

    }
}
