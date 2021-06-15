from datetime import datetime

import pandas as pd
import numpy as np

from statistics import mean

from fate_manager.db.db_models import ApplySiteInfo
from fate_manager.entity.types import FateJobStatus, FateJobType, FateJobEndStatus
from fate_manager.operation import federated_db_operator
from fate_manager.operation.db_operator import DBOperator
from fate_manager.settings import monitor_logger as logger


def get_total(request_data):
    query_info = get_date_info(request_data)
    logger.info(f"query job by:{query_info}")
    site_job_list = federated_db_operator.query_fate_site_job(**query_info)
    logger.info(f"query job success")
    apply_site_list = DBOperator.query_entity(ApplySiteInfo)
    site_total_dict = {}
    # all
    res = {"all": group_by_status(site_job_list)}
    for site in apply_site_list:
        site_total_dict[site.party_id] = {"data": [], "site_name": site.site_name}
    # site
    for site_job in site_job_list:
        site_total_dict[site_job.party_id]["data"].append(site_job)
    for party_id, total_dict in site_total_dict.items():
        if total_dict["data"]:
            res[str(party_id)] = group_by_status(total_dict["data"])
            res[str(party_id)]["site_name"] = total_dict["site_name"]
    logger.info(f"get total success: {res}")
    return res


def get_institutions_total(request_data):
    query_info = get_date_info(request_data)
    logger.info(f"query job by:{query_info}")
    site_job_list = federated_db_operator.query_fate_site_job(**query_info)
    logger.info(f"query job success")
    return group_by_institutions(site_job_list)


def get_site_total(request_data):
    query_info = get_date_info(request_data)
    logger.info(f"query job by:{query_info}")
    site_job_list = federated_db_operator.query_fate_site_job(**query_info)
    logger.info(f"query job success")
    return group_by_site(site_job_list)


def get_detail_total(request_data):
    query_info = get_date_info(request_data)
    if request_data.get("party_id") and str(request_data.get("party_id", "")).lower() != "all":
        query_info["party_id"] = request_data.get("party_id")
    logger.info(f"start query fate job by {query_info}")
    job_list = federated_db_operator.query_fate_site_job(**query_info)
    logger.info("start get day list")
    day_list = get_day_list(request_data)
    logger.info("start group by job type")
    return group_by_job_type(job_list, day_list)


def get_day_list(query_info):
    day_list = []
    start_time = datetime.strptime(query_info["startDate"], "%Y%m%d").date()
    end_time = datetime.strptime(query_info["endDate"], "%Y%m%d").date()
    if end_time == start_time:
        return [query_info["startDate"]]
    for day in range(0, (end_time - start_time).days + 1):
        day_list.append((start_time + day*(end_time-start_time)/(end_time-start_time).days).strftime("%Y%m%d"))
    return day_list


# def get_date_info(request_data):
#     date_info = {}
#     if request_data.get("endDate") and request_data.get("endDate") == request_data.get("startDate"):
#         date_info["job_create_day"] = request_data.get("startDate")
#     if request_data.get("endDate") and request_data.get("startDate"):
#         date_info["job_create_day"] = [request_data.get("startDate"), request_data.get("endDate")]
#     return date_info


def get_date_info(request_data):
    date_info = {}
    if request_data.get("endDate") and request_data.get("endDate") == request_data.get("startDate"):
        date_info["job_create_day_date"] = datetime.strptime(request_data.get("startDate"), '%Y%m%d')
    if request_data.get("endDate") and request_data.get("startDate"):
        date_info["job_create_day_date"] = [datetime.strptime(request_data.get("startDate"), '%Y%m%d'), datetime.strptime(request_data.get("endDate"), '%Y%m%d')]
    return date_info


def group_by_status(site_job_list, key="data", duration=False):
    site_job_dict = {key: {}}
    status_list = FateJobStatus.status_list()
    for status in status_list:
        site_job_dict[key][status] = 0
    for site_job in site_job_list:
        site_job_dict[key][site_job.status] += 1
    site_job_dict[key]["total"] = sum([site_job_dict[key][status] for status in site_job_dict[key].keys()])
    # percent
    for status in status_list:
        site_job_dict[key][f"{status}_percent"] = site_job_dict[key][status] / (site_job_dict[key]["total"] if site_job_dict[key]["total"] else 1)
    return site_job_dict


def group_by_end_status(site_job_list, key="data"):
    site_job_dict = {key: {}}
    end_status_list = FateJobEndStatus.status_list()
    for status in end_status_list:
        site_job_dict[key][FateJobEndStatus.end_status(status)] = 0
    duration_list = []
    for site_job in site_job_list:
        if site_job.status not in end_status_list:
            continue
        site_job_dict[key][FateJobEndStatus.end_status(site_job.status)] += 1
        duration_list.append(site_job.job_elapsed)
    site_job_dict[key]["total"] = sum([site_job_dict[key][status] for status in site_job_dict[key].keys()])
    # percent
    for status in [FateJobEndStatus.SUCCESS, FateJobEndStatus.FAILED]:
        site_job_dict[key][f"{status}_percent"] = site_job_dict[key][status] / (site_job_dict[key]["total"] if site_job_dict[key]["total"] else 1)
    return site_job_dict, duration_list


def group_by_institutions(site_job_list):
    institutions_dict = {}
    ret = {}
    for site_job in site_job_list:
        for institutions in site_job.other_institutions:
            if institutions_dict.get(institutions):
                institutions_dict[institutions].append(site_job)
            else:
                institutions_dict[institutions] = [site_job]
    for institutions, _site_job_list in institutions_dict.items():
        ret[institutions] = group_by_status(_site_job_list)
    logger.info(f"return:{ret}")
    return ret


def group_by_site(site_job_list):
    site_job_dict = {}
    other_institutions_dict = {}
    other_site_name_dict = {}
    apply_site_list = DBOperator.query_entity(ApplySiteInfo)
    for site in apply_site_list:
        other_institutions_dict[site.party_id] = site.institutions
        other_site_name_dict[site.party_id] = site.site_name
    for site_job in site_job_list:
        for other_party_id in site_job.other_party_id:
            if not site_job_dict.get(site_job.party_id):
                site_job_dict[site_job.party_id] = {"institutions": {other_institutions_dict[other_party_id]: {other_party_id: []}}}
            if not site_job_dict[site_job.party_id]["institutions"].get(other_institutions_dict[other_party_id]):
                site_job_dict[site_job.party_id]["institutions"][other_institutions_dict[other_party_id]] = {other_party_id: []}
            if not site_job_dict[site_job.party_id]["institutions"][other_institutions_dict[other_party_id]].get(other_party_id):
                site_job_dict[site_job.party_id]["institutions"][other_institutions_dict[other_party_id]][other_party_id] = []
            site_job_dict[site_job.party_id]["institutions"][other_institutions_dict[other_party_id]][other_party_id].append(site_job)
    total_job_num = 0
    for party_id, party_id_item in site_job_dict.items():
        for institutions, institutions_item in party_id_item["institutions"].items():
            for other_party_id, site_job_list in institutions_item.items():
                site_job_dict[party_id]["institutions"][institutions][other_party_id] = group_by_status(site_job_list)
                site_job_dict[party_id]["institutions"][institutions][other_party_id]["site_name"] = other_site_name_dict[other_party_id]
                site_job_dict[party_id]["site_name"] = other_site_name_dict[party_id]
                total_job_num += site_job_dict[party_id]["institutions"][institutions][other_party_id]["data"]["total"]
    logger.info(f"return:{site_job_dict}")
    return site_job_dict


def get_job_metrics(job_day_list):
    # max, min, mean
    job_elapsed_list = [job_day.job_elapsed for job_day in job_day_list]
    if job_elapsed_list:
        return max(job_elapsed_list), min(job_elapsed_list), mean(job_elapsed_list)
    else:
        return 0, 0, 0


def get_job_duration_distribution(duration_list, key="duration"):
    bins = [0, 60 * 1000, 30 * 60 * 1000, 2 * 60 * 60 * 1000, 6 * 60 * 60 * 1000, 12 * 60 * 60 * 1000,
            24 * 60 * 60 * 1000, np.inf]
    duration_distribution = pd.value_counts(pd.cut(duration_list, bins), sort=False)
    duration_distribution.index = [0, 1, 2, 3, 4, 5, 6]
    duration_distribution_dict = dict(duration_distribution)
    for k, v in duration_distribution_dict.items():
        duration_distribution_dict[k] = int(v)
    return duration_distribution_dict


def group_by_job_type(job_list, day_list):
    type_job_dict = {}
    type_date_job_dict = {}
    result = {}
    for job_type in FateJobType.status_list():
        type_job_dict[job_type] = []
        type_date_job_dict[job_type] = {}
        result[job_type] = {}
    for _type in type_date_job_dict:
        for day in day_list:
            type_date_job_dict[_type][day] = []
    for job in job_list:
        type_job_dict[job.job_type].append(job)
        type_date_job_dict[job.job_type][job.job_create_day].append(job)
    # type total and type duration
    for _type, job_type_list in type_job_dict.items():
        result[_type], duration_list = group_by_end_status(job_type_list, key="total")
        result[_type]["duration"] = get_job_duration_distribution(duration_list)
    # all type total
    all_total = {"total": 0, FateJobEndStatus.FAILED: 0, FateJobEndStatus.SUCCESS: 0}
    for _type, _type_total in result.items():
        all_total["total"] += _type_total["total"]["total"]
        for _status, _status_total in _type_total["total"].items():
            if _status in [FateJobEndStatus.FAILED, FateJobEndStatus.SUCCESS]:
                all_total[_status] += _status_total
    all_total[f"{FateJobEndStatus.SUCCESS}_percent"] = all_total[FateJobEndStatus.SUCCESS] / (all_total["total"] if all_total["total"] else 1)
    all_total[f"{FateJobEndStatus.FAILED}_percent"] = all_total[FateJobEndStatus.FAILED] / (all_total["total"] if all_total["total"] else 1)
    result["total"] = all_total
    # date
    for _type, job_type_list in type_date_job_dict.items():
        type_max = 0
        type_min = 0
        type_time_consuming = 0
        result[_type]["day"] = {}
        for _day, job_day_list in job_type_list.items():
            date_total, _ = group_by_end_status(job_day_list, key="total")
            _max, _min, _mean = get_job_metrics(job_day_list)
            result[_type]["day"][_day] = date_total
            result[_type]["day"][_day]['max'] = _max
            result[_type]["day"][_day]['min'] = _min
            result[_type]["day"][_day]['mean'] = _mean
            if type_max < _max:
                type_max = _max
            if type_min > _min:
                type_min = _min
            type_time_consuming += _mean * date_total["total"]["total"]
        result[_type]["total"]["max"] = type_max
        result[_type]["total"]["min"] = type_min
        result[_type]["total"]["mean"] = type_time_consuming / result[_type]["total"]['total'] if result[_type]["total"]['total'] else 1
    logger.info(f"return {result}")
    return result