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
package entity

type FlowCommResp struct {
	Code int    `json:"retcode"`
	Msg  string `json:"retmsg"`
}

type CurrentSteps struct {
}
type Roles struct {
	Guest   []int `json:"guest"`
	Host    []int `json:"host"`
	Arbiter []int `json:"arbiter"`
}
type TrainRuntimeConf struct {
}
type FlowJobQuery struct {
	CreateTime       int64       `json:"f_create_time"`
	CurrentSteps     interface{} `json:"f_current_steps"`
	CurrentTasks     interface{} `json:"f_current_tasks"`
	Description      string      `json:"f_description"`
	Elapsed          interface{} `json:"f_elapsed"`
	EndTime          interface{} `json:"f_end_time"`
	InitiatorPartyId string      `json:"f_initiator_party_id"`
	IsInitiator      int         `json:"f_is_initiator"`
	JobId            string      `json:"f_job_id"`
	Name             string      `json:"f_name"`
	PartyId          string      `json:"f_party_id"`
	Progress         int         `json:"f_progress"`
	Role             string      `json:"f_role"`
	Roles            string      `json:"f_roles"`
	RunIp            string      `json:"f_run_ip"`
	RuntimeConf      string      `json:"f_runtime_conf"`
	StartTime        interface{} `json:"f_start_time"`
	Status           string      `json:"f_status"`
	Tage             string      `json:"f_tag"`
	TrainRuntimeConf string      `json:"f_train_runtime_conf"`
	UpdateTime       int64       `json:"f_update_time"`
	WorkMode         int         `json:"f_work_mode"`
}
type FlowJobQueryResp struct {
	Code int            `json:"retcode"`
	Msg  string         `json:"retmsg"`
	Data []FlowJobQuery `json:"data"`
}
type FlowVersionQuery struct {
	Version string `json:"FATE"`
}
type FlowVersionQueryResp struct {
	Code int                `json:"retcode"`
	Msg  string             `json:"retmsg"`
	Data []FlowVersionQuery `json:"data"`
}
