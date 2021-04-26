from datetime import datetime

import pandas as pd
import numpy as np

from statistics import mean

from db.db_models import ApplySiteInfo
from entity.types import FateJobStatus, FateJobType
from operation import federated_db_operator
from operation.db_operator import DBOperator


def get_total(request_data):
    query_info = get_date_info(request_data)
    site_job_list = federated_db_operator.query_fate_site_job(**query_info)
    return group_by_status(site_job_list)


def get_institutions_total(request_data):
    query_info = get_date_info(request_data)
    site_job_list = federated_db_operator.query_fate_site_job(**query_info)
    return group_by_institutions(site_job_list)


def get_site_total(request_data):
    query_info = get_date_info(request_data)
    site_job_list = federated_db_operator.query_fate_site_job(**query_info)
    return group_by_site(site_job_list)


def get_detail_total(request_data):
    query_info = get_date_info(request_data)
    if request_data.get("party_id") and request_data.get("party_id", "").lower() != "all":
        query_info["party_id"] = request_data.get("party_id")
    job_list = federated_db_operator.query_fate_site_job(**query_info)
    day_list = get_day_list(request_data)
    return group_by_job_type(job_list, day_list)


def get_day_list(query_info):
    day_list = []
    start_time = datetime.strptime(query_info["startDate"], "%Y%m%d").date()
    end_time = datetime.strptime(query_info["endDate"], "%Y%m%d").date()
    for day in range(0, (end_time - start_time).days + 1):
        day_list.append((start_time + day*(end_time-start_time)/(end_time-start_time).days).strftime("%Y%m%d"))
    return day_list


def get_date_info(request_data):
    date_info = {}
    if request_data.get("endDate") and request_data.get("endDate") == request_data.get("startDate"):
        date_info["job_create_day"] = request_data.get("startDate")
    if request_data.get("endDate") and request_data.get("startDate"):
        date_info["job_create_day"] = [request_data.get("startDate"), request_data.get("endDate")]
    return date_info


def group_by_status(site_job_list, key="data", duration=False):
    site_job_dict = {key: {}}
    for status in FateJobStatus.status_list():
        site_job_dict[key][status] = 0
    duration_list = []
    if duration:
        for site_job in site_job_list:
            site_job_dict[key][site_job.status] += 1
            duration_list.append(site_job.job_elapsed)
    else:
        for site_job in site_job_list:
            site_job_dict[key][site_job.status] += 1
    site_job_dict[key]["total"] = sum([site_job_dict[key][status] for status in site_job_dict[key].keys()])
    # percent
    for status in FateJobStatus.status_list():
        site_job_dict[key][f"{status}_percent"] = site_job_dict[key][status] / (site_job_dict[key]["total"] if site_job_dict[key]["total"] else 1)
    if duration:
        return site_job_dict, duration_list
    return site_job_dict


def group_by_institutions(site_job_list):
    institutions_dict = {}
    ret = {}
    for site_job in site_job_list:
        if site_job.other_institutions:
            for institutions in site_job.other_institutions:
                if institutions_dict.get(institutions):
                    institutions_dict[institutions].append(site_job)
                else:
                    institutions_dict[institutions] = [site_job]
    for institutions, _site_job_list in institutions_dict.items():
        ret[institutions] = group_by_status(_site_job_list)
    return ret


def group_by_site(site_job_list):
    site_job_dict = {}
    other_institutions_dict = {}
    apply_site_list = DBOperator.query_entity(ApplySiteInfo)
    for site in apply_site_list:
        other_institutions_dict[site.party_id] = site.institutions
    for site_job in site_job_list:
        for other_party_id in site_job.other_party_id:
            if not site_job_dict.get(site_job.party_id):
                site_job_dict[site_job.party_id] = {other_institutions_dict[other_party_id]: {other_party_id: []}}
            if not site_job_dict[site_job.party_id].get(other_institutions_dict[other_party_id]):
                site_job_dict[site_job.party_id][other_institutions_dict[other_party_id]] = {other_party_id: []}
            if not site_job_dict[site_job.party_id][other_institutions_dict[other_party_id]].get(other_party_id):
                site_job_dict[site_job.party_id][other_institutions_dict[other_party_id]][other_party_id] = []
            site_job_dict[site_job.party_id][other_institutions_dict[other_party_id]][other_party_id].append(site_job)
    for party_id, party_id_item in site_job_dict.items():
        for institutions, institutions_item in party_id_item.items():
            for other_party_id, site_job_list in institutions_item.items():
                site_job_dict[party_id][institutions][other_party_id] = group_by_status(site_job_list)
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
    # total, duration
    for _type, job_type_list in type_job_dict.items():
        result[_type], duration_list = group_by_status(job_type_list, key="total", duration=True)
        result[_type]["duration"] = get_job_duration_distribution(duration_list)
    # date
    for _type, job_type_list in type_date_job_dict.items():
        for _day, job_day_list in job_type_list.items():
            date_total = group_by_status(job_day_list, key="total")
            _max, _min, _mean = get_job_metrics(job_day_list)
            result[_type][_day] = date_total
            result[_type][_day]['max'] = _max
            result[_type][_day]['min'] = _min
            result[_type][_day]['mean'] = _mean
    return result