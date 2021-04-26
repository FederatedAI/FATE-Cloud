from db.db_models import FateSiteInfo, ApplySiteInfo
from entity.types import FateJobStatus, SiteStatusType
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


def get_date_info(request_data):
    date_info = {}
    if request_data.get("endDate") and request_data.get("endDate") == request_data.get("startDate"):
        date_info["job_create_day"] = request_data.get("startDate")
    if request_data.get("endDate") and request_data.get("startDate"):
        date_info["job_create_day"] = [request_data.get("startDate"), request_data.get("endDate")]
    return date_info


def group_by_status(site_job_list):
    site_job_dict = {"data": {}}
    for status in FateJobStatus.status_list():
        site_job_dict["data"][status] = 0
    for site_job in site_job_list:
        site_job_dict["data"][site_job.status] += 1
    site_job_dict["total"] = sum([site_job_dict["data"][status] for status in site_job_dict["data"].keys()])
    # percent
    for status in FateJobStatus.status_list():
        site_job_dict["data"][f"{status}_percent"] = site_job_dict["data"][status] / (site_job_dict["total"] if site_job_dict["total"] else 1)
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
