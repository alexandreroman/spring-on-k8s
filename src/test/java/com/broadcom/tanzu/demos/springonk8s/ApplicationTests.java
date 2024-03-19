/*
 * Copyright (c) 2024 Broadcom, Inc. or its affiliates
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

package com.broadcom.tanzu.demos.springonk8s;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "app.message=Hello tests!")
@AutoConfigureObservability(tracing = false)
public class ApplicationTests {
    @Autowired
    private TestRestTemplate webClient;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testGreeting() {
        assertThat(webClient.getForObject("/", String.class)).isEqualTo("Hello tests!");
    }

    @Test
    public void testPrometheus() {
        assertThat(webClient.getForEntity("/actuator/prometheus", String.class).getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    public void testHealth() {
        assertThat(webClient.getForEntity("/actuator/health", String.class).getStatusCode().is2xxSuccessful()).isTrue();
    }
}
