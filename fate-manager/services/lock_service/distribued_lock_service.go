package lock_service

//
//import "fate.manager/comm/logging"
//
//var (
//	DisLockTableName = "ai_metrics_dislock"
//	ClientMethodName = "ClientWeekReportTask"
//)
//
//type DistributedLock struct {
//	ID         int64  `gorm:"type:bigint(12);column:id;AUTO_INCREMENT;primary_key"`
//	MethodName string `gorm:"type:string;column:method_name;unique_key"`
//	HostIp     string `gorm:"type:string;column:host_ip"`
//	Desc       string
//}
//
//
//func TryLock(methodName string) (result bool, msg string) {
//	defer func() {
//		if error := recover(); error != nil {
//			logging.Error("TryLock", zap.String("DBOperation Error", "Error"))
//			return
//		}
//	}()
//	disLock := DistributedLock{
//		MethodName: methodName,
//		HostIp:     http.GetLocalIP(),
//	}
//	db := fdnDao.GetDB()
//	error := db.Table(DisLockTableName).Create(&disLock).Error
//
//	if error == nil {
//		return true, "succ"
//	} else {
//		return false, error.Error()
//	}
//}
//
//func Release(methodName string) (result bool, msg string) {
//	defer func() {
//		if error := recover(); error != nil {
//			logging.Error("Release", zap.String("DBOperation Error", "Error"))
//			return
//		}
//	}()
//	disLock := DistributedLock{
//		MethodName: methodName,
//		HostIp:     http.GetLocalIP(),
//	}
//	db := fdnDao.GetDB()
//	error := db.Table(DisLockTableName).Delete(&disLock).Error
//	if error == nil {
//		return true, "succ"
//	} else {
//		return false, error.Error()
//	}
//
//}
