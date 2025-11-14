package com.ecsite.search;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
    properties = {
      "spring.elasticsearch.uris=http://localhost:9200",
      "spring.elasticsearch.username=",
      "spring.elasticsearch.password="
    })
class SearchApplicationTest {

  @Test
  void contextLoads() {
    // Spring Boot context loads successfully
  }
}
