package am.dopomoga.aidtools.airtable;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AirtableWriteConfiguration extends AbstractAirtableClientConfiguration {

    @Value("${airtable.token.write}")
    private String airtableToken;

}
