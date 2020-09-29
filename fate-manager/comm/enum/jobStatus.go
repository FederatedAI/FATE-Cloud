package enum

import "fate.manager/entity"

type JobStatus int32

const (
	JOB_STATUS_UNKNOWN JobStatus = -1
	JOB_STATUS_SUCCESS JobStatus = 0
	JOB_STATUS_RUNNING JobStatus = 1
	JOB_STATUS_FAILED  JobStatus = 2
)

func GetJobStatusString(p JobStatus) string {
	switch p {
	case JOB_STATUS_SUCCESS:
		return "success"
	case JOB_STATUS_RUNNING:
		return "running"
	case JOB_STATUS_FAILED:
		return "failed"
	default:
		return "unknown"
	}
}

func GetJobStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetJobStatusString(JobStatus(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
