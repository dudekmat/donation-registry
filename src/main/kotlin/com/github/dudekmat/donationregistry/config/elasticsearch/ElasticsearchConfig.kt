package com.github.dudekmat.donationregistry.config.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.client.RestClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ElasticsearchProperties::class)
class ElasticsearchConfig(private val elasticsearchProperties: ElasticsearchProperties) {

    @Bean
    fun elasticsearchClient(): ElasticsearchClient {
        val credentialsProvider = BasicCredentialsProvider()

        credentialsProvider.setCredentials(AuthScope.ANY,
                UsernamePasswordCredentials(elasticsearchProperties.username,
                        elasticsearchProperties.password))

        val restClient = RestClient.builder(HttpHost.create(elasticsearchProperties.host))
                .setHttpClientConfigCallback { httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                }
                .build()

        val transport = RestClientTransport(restClient, JacksonJsonpMapper())

        return ElasticsearchClient(transport)
    }

    companion object {
        const val DONATION_INDEX = "donation"
    }
}

@ConfigurationProperties(prefix = "elasticsearch")
data class ElasticsearchProperties(val host: String, val username: String, val password: String)