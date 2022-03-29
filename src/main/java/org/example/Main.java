package org.example;

import org.slf4j.Logger;
import com.cloudera.cdp.environments.api.EnvironmentsClient;
import com.cloudera.cdp.environments.api.EnvironmentsClientBuilder;
import com.cloudera.cdp.environments.model.ListEnvironmentsRequest;
import org.slf4j.LoggerFactory;

public class Main {

    final static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main (String... args){

        CdpProfileCredentialsProvider cdpprofilecredprovider = new CdpProfileCredentialsProvider("default");

        EnvironmentsClient envClient = EnvironmentsClientBuilder.defaultBuilder()
                .withCredentials(cdpprofilecredprovider).build();

        logger.info("LIST ENVIRONMENTS:" + envClient.listEnvironments(new ListEnvironmentsRequest()));
    }
}
