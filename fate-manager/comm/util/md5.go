package util

import (
	"crypto/md5"
	"encoding/hex"
	"os"
)

// EncodeMD5 md5 encryption
func EncodeMD5(value string) string {
	m := md5.New()
	m.Write([]byte(value))

	return hex.EncodeToString(m.Sum(nil))
}
func FileExists(path string) bool {
	_,err := os.Lstat(path)
	return !os.IsNotExist(err)
}