package am.dopomoga.aidtools.airtable;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public abstract class AbstractAirtableClientConfiguration {

    abstract String getAirtableToken();

    @Bean
    public RequestInterceptor basicAuthRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(
                    "Authorization",
                    "Bearer %s".formatted(getAirtableToken())
            );
        };
    }

}
