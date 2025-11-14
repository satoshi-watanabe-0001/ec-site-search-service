package com.ecsite.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * CORS設定クラス.
 *
 * <p>クロスオリジンリソース共有（CORS）の設定を管理します。
 * 開発環境用に全オリジンを許可しています。</p>
 *
 * @since 1.0
 */
@Configuration
public class CorsConfig {

    /**
     * CORSフィルターを設定する.
     *
     * <p>全オリジンからのアクセスを許可するCORSフィルターを生成します。
     * 本番環境では適切なオリジンに制限する必要があります。</p>
     *
     * @return CORSフィルター
     * @since 1.0
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
