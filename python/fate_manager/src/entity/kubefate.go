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

import "time"

type KubeData struct {
	JobId     string    `json:"uuid"`
	StartTime time.Time `json:"start_time"`
	EndTime   time.Time `json:"end_time"`
	Method    string    `json:"method"`
	Result    string    `json:"result"`
	ClusterId string    `json:"cluster_id"`
	Status    string    `json:"status"`
	Creator   string    `json:"creator"`
	SubJobs   []string  `json:"sub-jobs"`
	TimeLimit int64     `json:"time_limit"`
}

type ClusterInstallResp struct {
	Msg  string   `json:"msg"`
	Data KubeData `json:"data"`
}
type JobQueryResp struct {
	Data KubeData `json:"data"`
}

type ClusterInstallReq struct {
	Name         string `json:"name"`
	NameSpace    string `json:"namespace"`
	ChartName    string `json:"chart_name"`
	ChartVersion string `json:"chart_version"`
	Cover        bool   `json:"cover"`
	Data         []byte `json:"data"`
}
type ClusterUpdateReq struct {
	FateVersion string `json:"version"`
	Data        string `json:"data"`
}
type Metadata struct {
}
type ClusterData struct {
	ClusterId    string                 `json:"uuid"`
	Name         string                 `json:"name"`
	NameSpace    string                 `json:"namespaces"`
	ChartName    string                 `json:"chart_name"`
	ChartVersion string                 `json:"chart_version"`
	Status       string                 `json:"status"`
	Revision     int                    `json:"revision"`
	Values       string                 `json:"values"`
	ChartValue   map[string]interface{} `json:"chartValues"`
	Metadata     Metadata               `json:"metadata"`
	Config       map[string]interface{} `json:"Config"`
	Info         map[string]interface{} `json:"Info"`
}
type ClusterQueryResp struct {
	Data ClusterData `json:"data"`
}
type VersionResp struct {
	Msg  string `json:"msg"`
	Data string `json:"version"`
}

type Egg struct {
	Count int `json:"count"`
}
type DstParty struct {
	DstPartyId   int    `json:"partyId"`
	DstPartyIp   string `json:"partyIp"`
	DstPartyPort int    `json:"partyPort"`
}
type Proxy struct {
	Exchange  Exchange   `json:"exchange"`
	NodePort  int        `json:"nodePort"`
	PartyList []DstParty `json:"partyList"`
	Type      string     `json:"type"`
}

//1.3.0
type ClusterConfig struct {
	ChartVersion string   `json:"chartVersion"`
	Egg          Egg      `json:"egg"`
	Modules      []string `json:"modules"`
	Name         string   `json:"name"`
	NameSpace    string   `json:"namespace"`
	SrcPartyId   int      `json:"partyId"`
	Proxy        Proxy    `json:"proxy"`
}

//1.4.0+
type NodeSelector struct {
}
type Mysql struct {
	AccessMode    string       `json:"accessMode"`
	Database      string       `json:"database"`
	ExistingClaim string       `json:"existingClaim"`
	Ip            string       `json:"ip"`
	NodeSelector  NodeSelector `json:"nodeSelector"`
	Password      string       `json:"password"`
	Port          int          `json:"port"`
	Size          string       `json:"size"`
	StorageClass  string       `json:"storageClass"`
	SubPath       string       `json:"subPath"`
	User          string       `json:"user"`
}
type Node struct {
	AccessMode               string       `json:"accessMode"`
	ExistingClaim            string       `json:"existingClaim"`
	Name                     string       `json:"name"`
	NodeSelector             NodeSelector `json:"nodeSelector"`
	SessionProcessorsPerNode int          `json:"sessionProcessorsPerNode"`
	Size                     string       `json:"size"`
	StorageClass             string       `json:"storageClass"`
	SubPath                  string       `json:"subPath"`
}
type Python struct {
	FateFlowNodePort int          `json:"fateflowNodePort"`
	FateFlowType     string       `json:"fateflowType"`
	NodeSelector     NodeSelector `json:"nodeSelector"`
}
type NodeManager struct {
	Count                    int    `json:"count"`
	List                     []Node `json:"list"`
	SessionProcessorsPerNode int    `json:"sessionProcessorsPerNode"`
}
type PullPolicy struct {
}
type Exchange struct {
	ExchangeIp   string `json:"ip"`
	ExchangePort int    `json:"port"`
}
type Rollsite struct {
	Exchange Exchange `json:"exchange"`
	Proxy
	NodeSelector NodeSelector `json:"nodeSelector"`
}
type Istio struct {
	Enabled bool `json:"enabled"`
}
type ClusterConfig140 struct {
	ChartName    string      `json:"chartName"`
	ChartVersion string      `json:"chartVersion"`
	Istio        Istio       `json:"istio"`
	Modules      []string    `json:"modules"`
	Mysql        Mysql       `json:"mysql"`
	Name         string      `json:"name"`
	NameSpace    string      `json:"nameSpace"`
	NodeManager  NodeManager `json:"nodemanager"`
	SrcPartyId   int         `json:"partyId"`
	Persistence  bool        `json:"persistence"`
	PullPolicy   PullPolicy  `json:"pullPolicy"`
	Python       Python      `json:"python"`
	Registry     string      `json:"registry"`
	Rollsite     Rollsite    `json:"rollsite"`
}
