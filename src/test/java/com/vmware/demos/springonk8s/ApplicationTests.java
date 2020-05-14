/*
 * Copyright (c) 2020 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.demos.springonk8s;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {
    @Autowired
    private WebTestClient webClient;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testGreeting() {
        webClient.get().uri("/").exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello world!");
    }

    @Test
    public void testPrometheus() {
        webClient.get().uri("/actuator/prometheus").exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testHealth() {
        webClient.get().uri("/actuator/health").exchange()
                .expectStatus().isOk();
    }
}
