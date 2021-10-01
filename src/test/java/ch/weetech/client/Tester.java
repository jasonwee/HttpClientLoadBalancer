/*
 * Copyright [2021] [Jason Wee]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.weetech.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import ch.weetech.client.config.HttpClientConfig;
import ch.weetech.core.Get;

public class Tester {

    public static void main(String[] args) throws IOException {
        JwClientFactory factory = new JwClientFactory();
        Collection<String> servers = new ArrayList<String>();
        servers.add("http://localhost1/");
        servers.add("http://localhost2/");
        factory.setHttpClientConfig(new HttpClientConfig.Builder(servers).multiThreaded(true)
        //Per default this implementation will create no more than 2 concurrent connections per given route
        .defaultMaxTotalConnectionPerRoute(4)
        .discoveryEnabled(true)
        //.multiThreaded(true)
        .requestCompressionEnabled(false)
        .discoveryFrequency(5, TimeUnit.SECONDS)
        // and no more 20 connections in total
        .maxTotalConnection(20)
                 .build());

        JwClient client = factory.getObject();

         Get get = new Get.Builder("twitter", "1").type("tweet").build();
         get = new Get.Builder("test/test.json").build();


         JwResult result = client.execute(get);
         System.out.println(result.jsonString);
         client.close();

    }

}
