package am.dopomoga.aidtools.airtable;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AirtableReadOnlyConfiguration extends AbstractAirtableClientConfiguration {

    @Value("${airtable.token.read-only}")
    private String airtableToken;

}
