package util

import (
	"crypto/hmac"
	"crypto/md5"
	"crypto/sha1"
	"encoding/base64"
	"encoding/hex"
	"fate.manager/comm/logging"
	"fmt"
	"io/ioutil"
	"os/exec"
	"strconv"
	"strings"
)

func Md5(inputStr string) string {
	h := md5.New()
	h.Write([]byte(inputStr))
	str := strings.ToUpper(hex.EncodeToString(h.Sum(nil)))
	return str
}

type Signature struct {
	AppKey        string
	AppSecret     string
	UserAppKey    string
	UserAppSecret string
	FateManagerId string
	PartyId       int
	Role          int
	Nonce         string
	Body          string
	Uri           string
	Time          int64
}
type UserSignature struct {
	UserAppSecret string
	FateManagerId string
	UserAppKey    string
	Time          int64
	Nonce         string
	Uri           string
	Body          string
}

func Base64EncodeWithString(signature Signature) string {
	partyId := strconv.Itoa(signature.PartyId)
	role := strconv.Itoa(signature.Role)
	time := strconv.FormatInt(signature.Time, 10)
	//str := signature.FateManagerId + "\n"+signature.UserAppKey + "\n" + partyId + "\n" + role + "\n" + signature.AppKey + "\n" + time + "\n" + signature.Nonce + "\n" + signature.Uri + "\n" + signature.Body
	str := fmt.Sprintf("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s", signature.FateManagerId, signature.UserAppKey, partyId, role, signature.AppKey, time, signature.Nonce, signature.Uri, signature.Body)
	logstr := fmt.Sprintf("FateManagerId:%s,UserAppKey:%s,partyId:%s,role:%s,AppKey:%s,time:%s,Nonce:%s,Uri:%s,Body:%s",
		signature.FateManagerId, signature.UserAppKey, partyId, role, signature.AppKey, time, signature.Nonce, signature.Uri, signature.Body)
	logging.Debug(logstr)
	key_for_sign := []byte(signature.UserAppSecret + signature.AppSecret)
	h := hmac.New(sha1.New, key_for_sign)
	h.Write([]byte(str))
	return base64.StdEncoding.EncodeToString(h.Sum(nil))
}

func Base64EncodeWithStringForUser(signature UserSignature) string {
	time := strconv.FormatInt(signature.Time, 10)
	//str := signature.FateManagerId + "\n" + signature.UserAppKey + "\n" + time + "\n" + signature.Nonce + "\n" + signature.Uri + "\n" + signature.Body
	str := fmt.Sprintf("%s\n%s\n%s\n%s\n%s\n%s", signature.FateManagerId, signature.UserAppKey, time, signature.Nonce, signature.Uri, signature.Body)
	logstr := fmt.Sprintf("FateManagerId：%s,UserAppKey：%s,time:%s,Nonce:%s,Uri:%s,Body:%s",
		signature.FateManagerId, signature.UserAppKey, time, signature.Nonce, signature.Uri, signature.Body)
	logging.Debug(logstr)
	key_for_sign := []byte(signature.UserAppSecret)
	h := hmac.New(sha1.New, key_for_sign)
	h.Write([]byte(str))
	return base64.StdEncoding.EncodeToString(h.Sum(nil))
}

func Base64DecodeWithString(input string) (string, error) {

	data, err := base64.StdEncoding.DecodeString(input)
	if err != nil {
		return "", err
	}
	return string(data), err
}

func ExecCommand(strCommand string) (string, error) {
	logging.Debug("command:" + strCommand)
	cmd := exec.Command("/bin/bash", "-c", strCommand)

	stdout, _ := cmd.StdoutPipe()
	if err := cmd.Start(); err != nil {
		logging.Debug("Execute failed when Start:" + err.Error())
		return "", err
	}

	out_bytes, _ := ioutil.ReadAll(stdout)
	stdout.Close()

	if err := cmd.Wait(); err != nil {
		logging.Debug("Execute failed when Wait:" + err.Error())
		return "", err
	}
	result := string(out_bytes)
	logging.Debug("command result:" + result)
	return result, nil
}
