package com.webank.ai.fatecloud.system.controller;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.EnumConvert;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.system.pojo.qo.DropDownVersionQo;
import com.webank.ai.fatecloud.system.service.facade.DropDownServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/dropdown")
@Api(tags = "DropDownController", description = "About Enum Drop Down List")
@CrossOrigin
public class DropDownController {

    @Autowired
    EnumConvert enumConvert;

    @Autowired
    DropDownServiceFacade dropDownServiceFacade;


    @GetMapping(value = "/all")
    @ApiOperation(value = "Drop Down All List")
    public CommonResponse getDropDownAllList() throws ReflectiveOperationException {
        Map<String, Object> all = enumConvert.all();
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, all);

    }

    @PostMapping(value = "/version")
    @ApiOperation(value = "Drop Down Fate Version List")
    public CommonResponse<List<String>> getDropDownVersionList(@RequestBody DropDownVersionQo dropDownVersionQo) {

        return dropDownServiceFacade.getDropDownVersionList(dropDownVersionQo);
    }
}

