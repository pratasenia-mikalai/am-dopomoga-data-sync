package am.dopomoga.aidtools.airtable.restclient;

import am.dopomoga.aidtools.airtable.AirtableReadOnlyConfiguration;
import am.dopomoga.aidtools.airtable.dto.response.AirtableBasesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
        name = "airtableBasesClient",
        url = "https://api.airtable.com/v0/meta/bases",
        configuration = AirtableReadOnlyConfiguration.class
)
public interface AirtableBasesClient {

    @RequestMapping(method = RequestMethod.GET)
    AirtableBasesResponse getBasesList();

}
