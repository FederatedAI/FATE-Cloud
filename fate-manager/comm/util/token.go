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
	if err != nil || result == nil{
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
