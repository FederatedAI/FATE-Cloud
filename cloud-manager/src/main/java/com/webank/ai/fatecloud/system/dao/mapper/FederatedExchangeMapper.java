/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedExchangeDo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangePageForFateManagerQo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangePageQo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FederatedExchangeMapper extends BaseMapper<FederatedExchangeDo> {
    List<FederatedExchangeDo> findExchangePage(@Param("startIndex") long startIndex, @Param("exchangePageQo") ExchangePageQo exchangePageQo);

    int findExchangeCountForFateManager(ExchangePageForFateManagerQo exchangePageForFateManagerQo);

    List<FederatedExchangeDo> findExchangePageForFateManager(@Param("startIndex") long startIndex, @Param("exchangePageForFateManagerQo") ExchangePageForFateManagerQo exchangePageForFateManagerQo);

    Integer exchangeUsingCount(@Param("exchangeId") Long exchangeId);
}
