package entity

type FateFlowData struct {
	Eggroll string `json:"eggroll"`
	Fate    string `json:"fate"`
	Serving string `json:"serving"`
}

type FateFlowResp struct {
	Code int          `json:"retcode"`
	Msg  string       `json:"retmsg"`
	Data FateFlowData `json:"data"`
}
