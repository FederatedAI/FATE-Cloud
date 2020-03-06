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
package com.webank.ai.fatecloud.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected String limitClause;

    public String getLimitClause() {
        return limitClause;
    }

    public void setLimitClause(String limitClause) {
        this.limitClause = limitClause;
    }

    public StationExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
        limitClause = null;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andPortalUrlIsNull() {
            addCriterion("portal_url is null");
            return (Criteria) this;
        }

        public Criteria andPortalUrlIsNotNull() {
            addCriterion("portal_url is not null");
            return (Criteria) this;
        }

        public Criteria andPortalUrlEqualTo(String value) {
            addCriterion("portal_url =", value, "portalUrl");
            return (Criteria) this;
        }

        public Criteria andPortalUrlNotEqualTo(String value) {
            addCriterion("portal_url <>", value, "portalUrl");
            return (Criteria) this;
        }

        public Criteria andPortalUrlGreaterThan(String value) {
            addCriterion("portal_url >", value, "portalUrl");
            return (Criteria) this;
        }

        public Criteria andPortalUrlGreaterThanOrEqualTo(String value) {
            addCriterion("portal_url >=", value, "portalUrl");
            return (Criteria) this;
        }

        public Criteria andPortalUrlLessThan(String value) {
            addCriterion("portal_url <", value, "portalUrl");
            return (Criteria) this;
        }

        public Criteria andPortalUrlLessThanOrEqualTo(String value) {
            addCriterion("portal_url <=", value, "portalUrl");
            return (Criteria) this;
        }

        public Criteria andPortalUrlLike(String value) {
            addCriterion("portal_url like", value, "portalUrl");
            return (Criteria) this;
        }

        public Criteria andPortalUrlNotLike(String value) {
            addCriterion("portal_url not like", value, "portalUrl");
            return (Criteria) this;
        }

        public Criteria andPortalUrlIn(List<String> values) {
            addCriterion("portal_url in", values, "portalUrl");
            return (Criteria) this;
        }

        public Criteria andPortalUrlNotIn(List<String> values) {
            addCriterion("portal_url not in", values, "portalUrl");
            return (Criteria) this;
        }

        public Criteria andPortalUrlBetween(String value1, String value2) {
            addCriterion("portal_url between", value1, value2, "portalUrl");
            return (Criteria) this;
        }

        public Criteria andPortalUrlNotBetween(String value1, String value2) {
            addCriterion("portal_url not between", value1, value2, "portalUrl");
            return (Criteria) this;
        }

        public Criteria andExportUrlIsNull() {
            addCriterion("export_url is null");
            return (Criteria) this;
        }

        public Criteria andExportUrlIsNotNull() {
            addCriterion("export_url is not null");
            return (Criteria) this;
        }

        public Criteria andExportUrlEqualTo(String value) {
            addCriterion("export_url =", value, "exportUrl");
            return (Criteria) this;
        }

        public Criteria andExportUrlNotEqualTo(String value) {
            addCriterion("export_url <>", value, "exportUrl");
            return (Criteria) this;
        }

        public Criteria andExportUrlGreaterThan(String value) {
            addCriterion("export_url >", value, "exportUrl");
            return (Criteria) this;
        }

        public Criteria andExportUrlGreaterThanOrEqualTo(String value) {
            addCriterion("export_url >=", value, "exportUrl");
            return (Criteria) this;
        }

        public Criteria andExportUrlLessThan(String value) {
            addCriterion("export_url <", value, "exportUrl");
            return (Criteria) this;
        }

        public Criteria andExportUrlLessThanOrEqualTo(String value) {
            addCriterion("export_url <=", value, "exportUrl");
            return (Criteria) this;
        }

        public Criteria andExportUrlLike(String value) {
            addCriterion("export_url like", value, "exportUrl");
            return (Criteria) this;
        }

        public Criteria andExportUrlNotLike(String value) {
            addCriterion("export_url not like", value, "exportUrl");
            return (Criteria) this;
        }

        public Criteria andExportUrlIn(List<String> values) {
            addCriterion("export_url in", values, "exportUrl");
            return (Criteria) this;
        }

        public Criteria andExportUrlNotIn(List<String> values) {
            addCriterion("export_url not in", values, "exportUrl");
            return (Criteria) this;
        }

        public Criteria andExportUrlBetween(String value1, String value2) {
            addCriterion("export_url between", value1, value2, "exportUrl");
            return (Criteria) this;
        }

        public Criteria andExportUrlNotBetween(String value1, String value2) {
            addCriterion("export_url not between", value1, value2, "exportUrl");
            return (Criteria) this;
        }

        public Criteria andSecretInfoIsNull() {
            addCriterion("secret_info is null");
            return (Criteria) this;
        }

        public Criteria andSecretInfoIsNotNull() {
            addCriterion("secret_info is not null");
            return (Criteria) this;
        }

        public Criteria andSecretInfoEqualTo(String value) {
            addCriterion("secret_info =", value, "secretInfo");
            return (Criteria) this;
        }

        public Criteria andSecretInfoNotEqualTo(String value) {
            addCriterion("secret_info <>", value, "secretInfo");
            return (Criteria) this;
        }

        public Criteria andSecretInfoGreaterThan(String value) {
            addCriterion("secret_info >", value, "secretInfo");
            return (Criteria) this;
        }

        public Criteria andSecretInfoGreaterThanOrEqualTo(String value) {
            addCriterion("secret_info >=", value, "secretInfo");
            return (Criteria) this;
        }

        public Criteria andSecretInfoLessThan(String value) {
            addCriterion("secret_info <", value, "secretInfo");
            return (Criteria) this;
        }

        public Criteria andSecretInfoLessThanOrEqualTo(String value) {
            addCriterion("secret_info <=", value, "secretInfo");
            return (Criteria) this;
        }

        public Criteria andSecretInfoLike(String value) {
            addCriterion("secret_info like", value, "secretInfo");
            return (Criteria) this;
        }

        public Criteria andSecretInfoNotLike(String value) {
            addCriterion("secret_info not like", value, "secretInfo");
            return (Criteria) this;
        }

        public Criteria andSecretInfoIn(List<String> values) {
            addCriterion("secret_info in", values, "secretInfo");
            return (Criteria) this;
        }

        public Criteria andSecretInfoNotIn(List<String> values) {
            addCriterion("secret_info not in", values, "secretInfo");
            return (Criteria) this;
        }

        public Criteria andSecretInfoBetween(String value1, String value2) {
            addCriterion("secret_info between", value1, value2, "secretInfo");
            return (Criteria) this;
        }

        public Criteria andSecretInfoNotBetween(String value1, String value2) {
            addCriterion("secret_info not between", value1, value2, "secretInfo");
            return (Criteria) this;
        }

        public Criteria andRoleTypeIsNull() {
            addCriterion("role_type is null");
            return (Criteria) this;
        }

        public Criteria andRoleTypeIsNotNull() {
            addCriterion("role_type is not null");
            return (Criteria) this;
        }

        public Criteria andRoleTypeEqualTo(Byte value) {
            addCriterion("role_type =", value, "roleType");
            return (Criteria) this;
        }

        public Criteria andRoleTypeNotEqualTo(Byte value) {
            addCriterion("role_type <>", value, "roleType");
            return (Criteria) this;
        }

        public Criteria andRoleTypeGreaterThan(Byte value) {
            addCriterion("role_type >", value, "roleType");
            return (Criteria) this;
        }

        public Criteria andRoleTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("role_type >=", value, "roleType");
            return (Criteria) this;
        }

        public Criteria andRoleTypeLessThan(Byte value) {
            addCriterion("role_type <", value, "roleType");
            return (Criteria) this;
        }

        public Criteria andRoleTypeLessThanOrEqualTo(Byte value) {
            addCriterion("role_type <=", value, "roleType");
            return (Criteria) this;
        }

        public Criteria andRoleTypeIn(List<Byte> values) {
            addCriterion("role_type in", values, "roleType");
            return (Criteria) this;
        }

        public Criteria andRoleTypeNotIn(List<Byte> values) {
            addCriterion("role_type not in", values, "roleType");
            return (Criteria) this;
        }

        public Criteria andRoleTypeBetween(Byte value1, Byte value2) {
            addCriterion("role_type between", value1, value2, "roleType");
            return (Criteria) this;
        }

        public Criteria andRoleTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("role_type not between", value1, value2, "roleType");
            return (Criteria) this;
        }

        public Criteria andNodeStatusIsNull() {
            addCriterion("node_status is null");
            return (Criteria) this;
        }

        public Criteria andNodeStatusIsNotNull() {
            addCriterion("node_status is not null");
            return (Criteria) this;
        }

        public Criteria andNodeStatusEqualTo(Byte value) {
            addCriterion("node_status =", value, "nodeStatus");
            return (Criteria) this;
        }

        public Criteria andNodeStatusNotEqualTo(Byte value) {
            addCriterion("node_status <>", value, "nodeStatus");
            return (Criteria) this;
        }

        public Criteria andNodeStatusGreaterThan(Byte value) {
            addCriterion("node_status >", value, "nodeStatus");
            return (Criteria) this;
        }

        public Criteria andNodeStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("node_status >=", value, "nodeStatus");
            return (Criteria) this;
        }

        public Criteria andNodeStatusLessThan(Byte value) {
            addCriterion("node_status <", value, "nodeStatus");
            return (Criteria) this;
        }

        public Criteria andNodeStatusLessThanOrEqualTo(Byte value) {
            addCriterion("node_status <=", value, "nodeStatus");
            return (Criteria) this;
        }

        public Criteria andNodeStatusIn(List<Byte> values) {
            addCriterion("node_status in", values, "nodeStatus");
            return (Criteria) this;
        }

        public Criteria andNodeStatusNotIn(List<Byte> values) {
            addCriterion("node_status not in", values, "nodeStatus");
            return (Criteria) this;
        }

        public Criteria andNodeStatusBetween(Byte value1, Byte value2) {
            addCriterion("node_status between", value1, value2, "nodeStatus");
            return (Criteria) this;
        }

        public Criteria andNodeStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("node_status not between", value1, value2, "nodeStatus");
            return (Criteria) this;
        }

        public Criteria andNodePublicIsNull() {
            addCriterion("node_public is null");
            return (Criteria) this;
        }

        public Criteria andNodePublicIsNotNull() {
            addCriterion("node_public is not null");
            return (Criteria) this;
        }

        public Criteria andNodePublicEqualTo(Byte value) {
            addCriterion("node_public =", value, "nodePublic");
            return (Criteria) this;
        }

        public Criteria andNodePublicNotEqualTo(Byte value) {
            addCriterion("node_public <>", value, "nodePublic");
            return (Criteria) this;
        }

        public Criteria andNodePublicGreaterThan(Byte value) {
            addCriterion("node_public >", value, "nodePublic");
            return (Criteria) this;
        }

        public Criteria andNodePublicGreaterThanOrEqualTo(Byte value) {
            addCriterion("node_public >=", value, "nodePublic");
            return (Criteria) this;
        }

        public Criteria andNodePublicLessThan(Byte value) {
            addCriterion("node_public <", value, "nodePublic");
            return (Criteria) this;
        }

        public Criteria andNodePublicLessThanOrEqualTo(Byte value) {
            addCriterion("node_public <=", value, "nodePublic");
            return (Criteria) this;
        }

        public Criteria andNodePublicIn(List<Byte> values) {
            addCriterion("node_public in", values, "nodePublic");
            return (Criteria) this;
        }

        public Criteria andNodePublicNotIn(List<Byte> values) {
            addCriterion("node_public not in", values, "nodePublic");
            return (Criteria) this;
        }

        public Criteria andNodePublicBetween(Byte value1, Byte value2) {
            addCriterion("node_public between", value1, value2, "nodePublic");
            return (Criteria) this;
        }

        public Criteria andNodePublicNotBetween(Byte value1, Byte value2) {
            addCriterion("node_public not between", value1, value2, "nodePublic");
            return (Criteria) this;
        }

        public Criteria andDetectiveStatusIsNull() {
            addCriterion("detective_status is null");
            return (Criteria) this;
        }

        public Criteria andDetectiveStatusIsNotNull() {
            addCriterion("detective_status is not null");
            return (Criteria) this;
        }

        public Criteria andDetectiveStatusEqualTo(Byte value) {
            addCriterion("detective_status =", value, "detectiveStatus");
            return (Criteria) this;
        }

        public Criteria andDetectiveStatusNotEqualTo(Byte value) {
            addCriterion("detective_status <>", value, "detectiveStatus");
            return (Criteria) this;
        }

        public Criteria andDetectiveStatusGreaterThan(Byte value) {
            addCriterion("detective_status >", value, "detectiveStatus");
            return (Criteria) this;
        }

        public Criteria andDetectiveStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("detective_status >=", value, "detectiveStatus");
            return (Criteria) this;
        }

        public Criteria andDetectiveStatusLessThan(Byte value) {
            addCriterion("detective_status <", value, "detectiveStatus");
            return (Criteria) this;
        }

        public Criteria andDetectiveStatusLessThanOrEqualTo(Byte value) {
            addCriterion("detective_status <=", value, "detectiveStatus");
            return (Criteria) this;
        }

        public Criteria andDetectiveStatusIn(List<Byte> values) {
            addCriterion("detective_status in", values, "detectiveStatus");
            return (Criteria) this;
        }

        public Criteria andDetectiveStatusNotIn(List<Byte> values) {
            addCriterion("detective_status not in", values, "detectiveStatus");
            return (Criteria) this;
        }

        public Criteria andDetectiveStatusBetween(Byte value1, Byte value2) {
            addCriterion("detective_status between", value1, value2, "detectiveStatus");
            return (Criteria) this;
        }

        public Criteria andDetectiveStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("detective_status not between", value1, value2, "detectiveStatus");
            return (Criteria) this;
        }

        public Criteria andLastDetectiveTimeIsNull() {
            addCriterion("last_detective_time is null");
            return (Criteria) this;
        }

        public Criteria andLastDetectiveTimeIsNotNull() {
            addCriterion("last_detective_time is not null");
            return (Criteria) this;
        }

        public Criteria andLastDetectiveTimeEqualTo(Date value) {
            addCriterion("last_detective_time =", value, "lastDetectiveTime");
            return (Criteria) this;
        }

        public Criteria andLastDetectiveTimeNotEqualTo(Date value) {
            addCriterion("last_detective_time <>", value, "lastDetectiveTime");
            return (Criteria) this;
        }

        public Criteria andLastDetectiveTimeGreaterThan(Date value) {
            addCriterion("last_detective_time >", value, "lastDetectiveTime");
            return (Criteria) this;
        }

        public Criteria andLastDetectiveTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("last_detective_time >=", value, "lastDetectiveTime");
            return (Criteria) this;
        }

        public Criteria andLastDetectiveTimeLessThan(Date value) {
            addCriterion("last_detective_time <", value, "lastDetectiveTime");
            return (Criteria) this;
        }

        public Criteria andLastDetectiveTimeLessThanOrEqualTo(Date value) {
            addCriterion("last_detective_time <=", value, "lastDetectiveTime");
            return (Criteria) this;
        }

        public Criteria andLastDetectiveTimeIn(List<Date> values) {
            addCriterion("last_detective_time in", values, "lastDetectiveTime");
            return (Criteria) this;
        }

        public Criteria andLastDetectiveTimeNotIn(List<Date> values) {
            addCriterion("last_detective_time not in", values, "lastDetectiveTime");
            return (Criteria) this;
        }

        public Criteria andLastDetectiveTimeBetween(Date value1, Date value2) {
            addCriterion("last_detective_time between", value1, value2, "lastDetectiveTime");
            return (Criteria) this;
        }

        public Criteria andLastDetectiveTimeNotBetween(Date value1, Date value2) {
            addCriterion("last_detective_time not between", value1, value2, "lastDetectiveTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andFDescriptionIsNull() {
            addCriterion("f_description is null");
            return (Criteria) this;
        }

        public Criteria andFDescriptionIsNotNull() {
            addCriterion("f_description is not null");
            return (Criteria) this;
        }

        public Criteria andFDescriptionEqualTo(String value) {
            addCriterion("f_description =", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionNotEqualTo(String value) {
            addCriterion("f_description <>", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionGreaterThan(String value) {
            addCriterion("f_description >", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("f_description >=", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionLessThan(String value) {
            addCriterion("f_description <", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionLessThanOrEqualTo(String value) {
            addCriterion("f_description <=", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionLike(String value) {
            addCriterion("f_description like", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionNotLike(String value) {
            addCriterion("f_description not like", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionIn(List<String> values) {
            addCriterion("f_description in", values, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionNotIn(List<String> values) {
            addCriterion("f_description not in", values, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionBetween(String value1, String value2) {
            addCriterion("f_description between", value1, value2, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionNotBetween(String value1, String value2) {
            addCriterion("f_description not between", value1, value2, "fDescription");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescIsNull() {
            addCriterion("organization_desc is null");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescIsNotNull() {
            addCriterion("organization_desc is not null");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescEqualTo(String value) {
            addCriterion("organization_desc =", value, "organizationDesc");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescNotEqualTo(String value) {
            addCriterion("organization_desc <>", value, "organizationDesc");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescGreaterThan(String value) {
            addCriterion("organization_desc >", value, "organizationDesc");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescGreaterThanOrEqualTo(String value) {
            addCriterion("organization_desc >=", value, "organizationDesc");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescLessThan(String value) {
            addCriterion("organization_desc <", value, "organizationDesc");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescLessThanOrEqualTo(String value) {
            addCriterion("organization_desc <=", value, "organizationDesc");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescLike(String value) {
            addCriterion("organization_desc like", value, "organizationDesc");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescNotLike(String value) {
            addCriterion("organization_desc not like", value, "organizationDesc");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescIn(List<String> values) {
            addCriterion("organization_desc in", values, "organizationDesc");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescNotIn(List<String> values) {
            addCriterion("organization_desc not in", values, "organizationDesc");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescBetween(String value1, String value2) {
            addCriterion("organization_desc between", value1, value2, "organizationDesc");
            return (Criteria) this;
        }

        public Criteria andOrganizationDescNotBetween(String value1, String value2) {
            addCriterion("organization_desc not between", value1, value2, "organizationDesc");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}