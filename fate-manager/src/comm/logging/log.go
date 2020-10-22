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
package logging

import (
	"fate.manager/comm/setting"
	"fmt"
	"github.com/EDDYCJY/go-gin-example/pkg/file"
	"github.com/gin-gonic/gin"
	"log"
	"os"
	"path/filepath"
	"runtime"
	"strings"
	"time"
)

type Level int

var (
	F                  *os.File
	DefaultPrefix      = ""
	DefaultCallerDepth = 2
	loggerInfo         *log.Logger
	loggerWarn         *log.Logger
	loggerDebug        *log.Logger
	loggerError        *log.Logger
	loggerFatal        *log.Logger
	logPrefix          = ""
	levelFlags         = []string{"DEBUG", "INFO", "WARN", "ERROR", "FATAL"}
	testFlags          = []string{"single", "toy", "fast", "normal", "all"}
)

const (
	DEBUG Level = iota
	INFO
	WARNING
	ERROR
	FATAL
)

func getLogFileName(level string) string {
	return fmt.Sprintf("fate-manager.%s.log", strings.ToLower(level))
}
func Setup() {
	LogSetup()
	AutoTestSetUp()
}
func AutoTestSetUp() {
	dir, err := os.Getwd()
	if err != nil {
		fmt.Errorf("os.Getwd err: %v", err)
	}
	for i := 0; i < len(testFlags); i++ {
		src := dir + "/testLog/" + testFlags[i]
		perm := file.CheckPermission(src)
		if perm == true {
			fmt.Errorf("file.CheckPermission Permission denied src: %s", src)
		}

		err = file.IsNotExistMkDir(src)
		if err != nil {
			fmt.Errorf("file.IsNotExistMkDir src: %s, err: %v", src, err)
		}
	}

}
func LogSetup() {
	var err error
	filePath := fmt.Sprintf("%s/%s/", setting.ServerSetting.LogSavePath, time.Now().Format(setting.ServerSetting.TimeFormat))
	perm := file.CheckPermission(filePath)
	if perm == true {
		fmt.Errorf("file.CheckPermission Permission denied src: %s", filePath)
	}

	err = file.IsNotExistMkDir(filePath)
	if err != nil {
		fmt.Errorf("file.IsNotExistMkDir src: %s, err: %v", filePath, err)
	}
	if err != nil {
		log.Fatalf("logging.Setup err: %v", err)
	}
	for i := 0; i < len(levelFlags); i++ {
		fileName := getLogFileName(levelFlags[i])
		fmt.Println(filePath, fileName)
		f, err := file.Open(filePath+fileName, os.O_APPEND|os.O_CREATE|os.O_RDWR, 0644)
		if err != nil {
			fmt.Errorf("Fail to OpenFile :%v", err)
		}
		if levelFlags[i] == "INFO" {
			loggerInfo = log.New(f, DefaultPrefix, log.LstdFlags)
		} else if levelFlags[i] == "DEBUG" {
			loggerDebug = log.New(f, DefaultPrefix, log.LstdFlags)
		} else if levelFlags[i] == "WARN" {
			loggerWarn = log.New(f, DefaultPrefix, log.LstdFlags)
		} else if levelFlags[i] == "ERROR" {
			loggerError = log.New(f, DefaultPrefix, log.LstdFlags)
		} else if levelFlags[i] == "FATAL" {
			loggerFatal = log.New(f, DefaultPrefix, log.LstdFlags)
		}
	}
}

func Debug(v ...interface{}) {
	if LogPrint(DEBUG) {
		setPrefix(DEBUG)
		loggerDebug.Println(v)
	}
}

func Info(v ...interface{}) {
	if LogPrint(INFO) {
		setPrefix(INFO)
		loggerInfo.Println(v)
	}
}

func Warn(v ...interface{}) {
	if LogPrint(WARNING) {
		setPrefix(WARNING)
		loggerWarn.Println(v)
	}
}

func Error(v ...interface{}) {
	if LogPrint(ERROR) {
		setPrefix(ERROR)
		loggerError.Println(v)
	}
}

func Fatal(v ...interface{}) {
	if LogPrint(FATAL) {
		setPrefix(FATAL)
		loggerFatal.Fatalln(v)
	}
}
func LogPrint(level Level) bool {
	runMode := gin.Mode()
	if (runMode == "debug" || runMode == "test") && (level == WARNING || level == DEBUG || level == ERROR || level == FATAL) {
		return true
	}
	if runMode == "release" && (level == ERROR || level == FATAL) {
		return true
	}
	return false
}
func setPrefix(level Level) {
	filePath := fmt.Sprintf("%s/%s/", setting.ServerSetting.LogSavePath, time.Now().Format(setting.ServerSetting.TimeFormat))
	if notExist := file.CheckNotExist(filePath); notExist == true {
		LogSetup()
	}
	_, file, line, ok := runtime.Caller(DefaultCallerDepth)
	if ok {
		logPrefix = fmt.Sprintf("[%s][%s:%d]", levelFlags[level], filepath.Base(file), line)
	} else {
		logPrefix = fmt.Sprintf("[%s]", levelFlags[level])
	}

	if level == INFO {
		loggerInfo.SetPrefix(logPrefix)
	} else if level == DEBUG {
		loggerDebug.SetPrefix(logPrefix)
	} else if level == WARNING {
		loggerWarn.SetPrefix(logPrefix)
	} else if level == ERROR {
		loggerError.SetPrefix(logPrefix)
	} else if level == FATAL {
		loggerFatal.SetPrefix(logPrefix)
	}
}
