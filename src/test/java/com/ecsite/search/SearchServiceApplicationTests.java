package com.ecsite.search;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Search Service Application Tests.
 * 検索サービスアプリケーションテスト
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.elasticsearch.uris=http://localhost:9200"
})
class SearchServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
