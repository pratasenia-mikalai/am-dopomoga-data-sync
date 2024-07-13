package am.dopomoga.aidtools.airtable.restclient;

import am.dopomoga.aidtools.airtable.AirtableWriteConfiguration;
import am.dopomoga.aidtools.airtable.dto.GoodDto;
import am.dopomoga.aidtools.airtable.dto.RefugeeDto;
import am.dopomoga.aidtools.airtable.dto.RefugeeFamilyDto;
import am.dopomoga.aidtools.airtable.dto.request.AirtableTableSaveRequest;
import am.dopomoga.aidtools.airtable.dto.response.AirtableGoodsTableResponse;
import am.dopomoga.aidtools.airtable.dto.response.AirtableRefugeesTableResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
        name = "airtableWriteClient",
        url = "https://api.airtable.com/v0",
        configuration = AirtableWriteConfiguration.class
)
public interface AirtableTablesWriteClient {

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/{baseid}/Goods"
    )
    AirtableGoodsTableResponse saveGoods(@PathVariable String baseid, @RequestBody AirtableTableSaveRequest<GoodDto> body);


    @RequestMapping(
            method = RequestMethod.POST,
            value = "/{baseid}/Refugees"
    )
    AirtableRefugeesTableResponse saveRefugees(@PathVariable String baseid, @RequestBody AirtableTableSaveRequest<RefugeeDto> body);

    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/{baseid}/Refugees"
    )
    AirtableRefugeesTableResponse patchRefugeeFamilies(@PathVariable String baseid, @RequestBody AirtableTableSaveRequest<RefugeeFamilyDto> body);

}
