package am.dopomoga.aidtools.airtable.restclient;

import am.dopomoga.aidtools.airtable.AirtableReadOnlyConfiguration;
import am.dopomoga.aidtools.airtable.dto.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "airtableReadClient",
        url = "https://api.airtable.com/v0",
        configuration = AirtableReadOnlyConfiguration.class
)
public interface AirtableTablesReadClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{baseid}/Goods"
    )
    AirtableGoodsTableResponse getGoods(@PathVariable String baseid, @RequestParam String offset, @RequestParam String filterByFormula);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{baseid}/Minus"
    )
    AirtableMinusTableResponse getMinus(@PathVariable String baseid, @RequestParam String offset, @RequestParam String filterByFormula);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{baseid}/Support"
    )
    AirtableSupportTableResponse getSupport(@PathVariable String baseid, @RequestParam String offset, @RequestParam String filterByFormula);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{baseid}/Refugees"
    )
    AirtableRefugeesTableResponse getRefugees(@PathVariable String baseid, @RequestParam String offset, @RequestParam String filterByFormula);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{baseid}/Plus"
    )
    AirtablePlusTableResponse getPlus(@PathVariable String baseid, @RequestParam String offset, @RequestParam String filterByFormula);
}
