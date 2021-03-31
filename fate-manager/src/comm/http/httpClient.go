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
package http

import (
	"bytes"
	"compress/gzip"
	"crypto/tls"
	"encoding/json"
	"fate.manager/comm/logging"
	"fate.manager/comm/setting"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"net/url"
	"time"
)

type request interface {
	GET() (ret *result, err error)
	POST() (ret *result, err error)
	PUT() (ret *result, err error)
	DELETE() (ret *result, err error)
}

type Url string

type Param interface{}

type Header map[string]interface{}

var DEBUG bool

type req struct {
	Url
	Param
	Header
}

type result struct {
	Url
	Param
	Method     string
	Params     interface{}
	Body       string
	Header     http.Header
	Response   *http.Response
	Proto      string
	Host       string
	URL        *url.URL
	Status     string
	StatusCode int
}

func (r req) GET() (ret *result, err error) {
	return r.do("GET", r.Url, r.Param, r.Header)
}

func (r req) POST() (ret *result, err error) {
	return r.do("POST", r.Url, r.Param, r.Header)
}

func (r req) PUT() (ret *result, err error) {
	return r.do("PUT", r.Url, r.Param, r.Header)
}

func (r req) DELETE() (ret *result, err error) {
	return r.do("DELETE", r.Url, r.Param, r.Header)
}

func GetProxy() *http.Client {
	proxy := func(_ *http.Request) (*url.URL, error) {
		return url.Parse(setting.ServerSetting.ProxyUrl)
	}

	transport := &http.Transport{Proxy: proxy}

	client := &http.Client{Timeout: 5 * time.Second, Transport: transport}
	return client
}

func (r req) do(method string, url Url, param Param, header Header) (ret *result, err error) {
	logging.Debug("Post:", url, param)
	jsonStr, _ := json.Marshal(param)
	reqs, err := http.NewRequest(method, string(url), bytes.NewBuffer(jsonStr))
	if err != nil {
		return nil, err
	}
	r.setHeader(reqs, header)
	defer reqs.Body.Close()
	tr := &http.Transport{
		TLSClientConfig: &tls.Config{InsecureSkipVerify: setting.ServerSetting.IfSSL},
	}
	client := &http.Client{Timeout: 20 * time.Second, Transport: tr}
	if setting.ServerSetting.IfProxy {
		client = GetProxy()
	}
	res, err := client.Do(reqs)
	if err != nil {
		return nil, err
	}
	defer res.Body.Close()
	ret = &result{
		Url:        r.Url,
		Param:      r.Param,
		Method:     method,
		Body:       r.getBody(res),
		Header:     reqs.Header,
		Response:   res,
		Proto:      reqs.Proto,
		Host:       reqs.Host,
		URL:        reqs.URL,
		Status:     res.Status,
		StatusCode: res.StatusCode,
	}
	r.debug(ret)
	return
}

func (r req) getBody(res *http.Response) (body string) {
	if res.StatusCode == 200 {
		switch res.Header.Get("Content-Encoding") {
		case "gzip":
			reader, _ := gzip.NewReader(res.Body)
			for {
				buf := make([]byte, 1024)
				n, err := reader.Read(buf)
				if err != nil && err != io.EOF {
					panic(err)
				}
				if n == 0 {
					break
				}
				body += string(buf)
			}
		default:
			bodyByte, _ := ioutil.ReadAll(res.Body)
			body = string(bodyByte)
		}
	} else {
		bodyByte, _ := ioutil.ReadAll(res.Body)
		body = string(bodyByte)
	}
	return
}

func (r req) setHeader(h *http.Request, header Header) {
	h.Header.Add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
	h.Header.Add("Accept-Encoding", "gzip, deflate")
	h.Header.Add("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
	h.Header.Add("Connection", "keep-alive")
	h.Header.Add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:12.0) Gecko/20100101 Firefox/12.0")
	h.Header.Add("content-type", "application/json")
	for k, v := range header {
		h.Header.Add(k, fmt.Sprint(v))
	}
}

func (r req) debug(ret *result) {
	logging.Info("debug log start ----------")
	logging.Debug(ret.Method, ret.Proto)
	logging.Debug("Host", ":", ret.Host)
	logging.Debug("URL", ":", ret.URL)
	logging.Debug("RawQuery", ":", ret.URL.RawQuery)
	for key, val := range ret.Header {
		logging.Debug(key, ":", val[0])
	}
	logging.Debug("----------------------------------------------------")
	logging.Debug("Status", ":", ret.Status)
	logging.Debug("StatusCode", ":", ret.StatusCode)
	for key, val := range ret.Response.Header {
		logging.Debug(key, ":", val[0])
	}
	logging.Debug("Body", ":", ret.Body)
	logging.Debug("debug log end ----------")
}

func GET(url Url, param Param, header Header) (ret *result, err error) {
	return req.GET(req{
		Url:    url,
		Param:  param,
		Header: header,
	})
}

func POST(url Url, param Param, header Header) (ret *result, err error) {
	return req.POST(req{
		Url:    url,
		Param:  param,
		Header: header,
	})
}

func PUT(url Url, param Param, header Header) (ret *result, err error) {
	return req.PUT(req{
		Url:    url,
		Param:  param,
		Header: header,
	})
}

func DELETE(url Url, param Param, header Header) (ret *result, err error) {
	return req.DELETE(req{
		Url:    url,
		Param:  param,
		Header: header,
	})
}
