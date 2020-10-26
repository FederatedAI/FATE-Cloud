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
package util

import (
	"encoding/json"
	"errors"
	"fate.manager/comm/http"
	"fate.manager/comm/logging"
	"fmt"
)

type User struct {
	UserName string
	Password string
}

func GetToken(kubefateUrl string, user User) (string, error) {

	loginUrl := kubefateUrl + "/v1/user/login"

	login := map[string]string{
		"username": user.UserName,
		"password": user.Password,
	}

	result, err := http.POST(http.Url(loginUrl), login, nil)
	if err != nil || result == nil {
		return "", err
	}
	Result := map[string]interface{}{}

	err = json.Unmarshal([]byte(result.Body), &Result)
	if err != nil {
		return "", err
	}

	if result.StatusCode != 200 {
		return "", errors.New(fmt.Sprint(Result["message"]))
	}

	token := fmt.Sprint(Result["token"])

	logging.Debug("get token success,token:", token)
	return token, nil
}

type Request struct {
	Type string
	Path string
	Body []byte
}

type Response struct {
	Code int
	Body []byte
}
