package com.ecsite.search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch設定クラス.
 *
 * <p>Elasticsearch 8.xクライアントの設定を行う。 接続情報、タイムアウト設定、認証情報を管理。
 *
 * @since 1.0.0
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

  @Value("${spring.elasticsearch.connection-timeout:5s}")
  private String connectionTimeout;

  @Value("${spring.elasticsearch.socket-timeout:30s}")
  private String socketTimeout;

  /**
   * Elasticsearchクライアント設定を構築.
   *
   * @return ClientConfiguration Elasticsearchクライアント設定
   */
  @Override
  public ClientConfiguration clientConfiguration() {
    ClientConfiguration.MaybeSecureClientConfigurationBuilder builder =
        ClientConfiguration.builder().connectedTo(elasticsearchUris.replace("http://", ""));

    if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
      builder.withBasicAuth(username, password);
    }

    return builder.build();
  }
}
