package ch.weetech.client;

import java.io.IOException;

import ch.weetech.client.config.HttpClientConfig;
import ch.weetech.core.Get;

public class Tester {

	public static void main(String[] args) throws IOException {
		JwClientFactory factory = new JwClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost").multiThreaded(true)
		//Per default this implementation will create no more than 2 concurrent connections per given route
		.defaultMaxTotalConnectionPerRoute(4)
		// and no more 20 connections in total
		.maxTotalConnection(20)
                 .build());
		
		JwClient client = factory.getObject();
		

		 Get get = new Get.Builder("twitter", "1").type("tweet").build();


		 JwResult result = client.execute(get);
		 
	
	}

}
