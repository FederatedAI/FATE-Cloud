package enum

import "fate.manager/entity"

type JobType int32

const (
	JOB_TYPE_UNKNOWN JobType = -1
	JOB_TYPE_INSTALL JobType = 0
	JOB_TYPE_UPDATE  JobType = 1
	JOB_TYPE_DELETE  JobType = 2
)

func GetJobTypeString(p JobType) string {
	switch p {
	case JOB_TYPE_INSTALL:
		return "clusterInstall"
	case JOB_TYPE_UPDATE:
		return "clusterUpdate"
	case JOB_TYPE_DELETE:
		return "clusterDelete"
	default:
		return "unknown"
	}
}

func GetJobTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetJobTypeString(JobType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
