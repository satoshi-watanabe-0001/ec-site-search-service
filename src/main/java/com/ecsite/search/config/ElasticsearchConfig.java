package com.ecsite.search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch設定クラス.
 *
 * <p>Elasticsearchクライアントの接続設定を管理します。
 * ホスト、ポート、認証情報などを設定します。</p>
 *
 * @since 1.0
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.ecsite.search.repository")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUris;

    @Value("${spring.elasticsearch.username:}")
    private String username;

    @Value("${spring.elasticsearch.password:}")
    private String password;

    /**
     * Elasticsearchクライアント設定を構築する.
     *
     * <p>application.ymlの設定値を使用して、
     * Elasticsearchクライアントの接続設定を生成します。
     * Basic認証が設定されている場合は認証情報を含めます。</p>
     *
     * @return クライアント設定
     * @since 1.0
     */
    @Override
    public ClientConfiguration clientConfiguration() {
        ClientConfiguration.MaybeSecureClientConfigurationBuilder builder =
                ClientConfiguration.builder()
                        .connectedTo(elasticsearchUris.replace("http://", "")
                                .replace("https://", ""));

        if (username != null && !username.isEmpty()) {
            builder.withBasicAuth(username, password);
        }

        return builder.build();
    }
}
