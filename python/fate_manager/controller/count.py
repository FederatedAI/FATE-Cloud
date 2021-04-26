import json
import time

import requests

from db.db_models import DeployComponent, FateSiteInfo, FateSiteCount, FateSiteJobInfo, ApplySiteInfo
from entity.types import SiteStatusType
from operation.db_operator import DBOperator
from settings import FATE_FLOW_SETTINGS, request_flow_logger
from utils.request_fate_flow_utils import post_fate_flow


class CountJob:
    @staticmethod
    def count_fate_flow_job():
        request_flow_logger.info("start count fate flow job")
        site_list = DBOperator.query_entity(FateSiteInfo, status=SiteStatusType.JOINED)
        component_name = 'FATEFLOW'
        for site in site_list:
            deploy_fate_flow = DBOperator.query_entity(DeployComponent, party_id=site.party_id,
                                                       component_name=component_name)
            if deploy_fate_flow:
                query_job_url = "http://{}{}".format(deploy_fate_flow[0].address, FATE_FLOW_SETTINGS["QueryJob"])
                fate_site_count = DBOperator.query_entity(FateSiteCount, reverse=True, order_by="version")
                now_time = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
                if fate_site_count:
                    if site.party_id in fate_site_count[0].party_id_list:
                        party_id_list = fate_site_count[0].party_id_list
                        time_list = [fate_site_count[0].strftime, now_time]
                    else:
                        party_id_list = fate_site_count[0].party_id_list
                        party_id_list.append(site.party_id)
                        time_list = [0, now_time]
                else:
                    time_list = [0, now_time]
                    party_id_list = [site.party_id]
                request_flow_logger.info(time_list)
                job_list = post_fate_flow(query_job_url, data={"end_time": time_list})
                CountJob.log_job_info(job_list, party_id=site.party_id)
                request_flow_logger.info(f"start create fate site count: now_time{now_time}")
                DBOperator.create_entity(FateSiteCount, {"strftime": now_time, "party_id_list": party_id_list})


    @staticmethod
    def log_job_info(job_list, party_id):
        request_flow_logger.info(job_list)
        apply_site_list = DBOperator.query_entity(ApplySiteInfo)
        other_institutions = {}
        for site in apply_site_list:
            other_institutions[str(site.party_id)] = site.institutions
        for job in job_list:
            try:
                CountJob.save_site_job_item(job, party_id, other_institutions)
            except Exception as e:
                request_flow_logger.exception(e)

    @staticmethod
    def save_site_job_item(job, party_id, other_institutions):
        site_job = FateSiteJobInfo()
        site_job.job_id = job.get("f_job_id")
        site_job.party_id = party_id
        site_job.job_create_time = int(time.mktime(time.strptime(job.get("f_job_id")[:20], "%Y%m%d%H%M%S%f"))*1000)
        site_job.job_elapsed = job.get("f_elapsed")
        site_job.job_start_time = job.get("f_start_time")
        site_job.job_end_time = job.get("f_end_time")
        site_job.roles = job.get("f_roles")
        site_job.job_type = CountJob.get_job_type(job.get("f_dsl"))
        site_job.status = job.get("f_status")
        site_job.job_create_day = job.get("f_job_id")[:8]

        site_job.job_info = job
        other_party_id = set()
        for role, party_id_list in job["f_roles"].items():
            for _party_id in party_id_list:
                if party_id != _party_id:
                    other_party_id.add(_party_id)
            if role == "arbiter":
                continue
            if party_id in party_id_list:
                site_job.role = role
                break
        else:
            site_job.role = "local"
            site_job.other_party_id = []
        if site_job.role != "local":
            site_job.other_party_id = list(other_party_id)
        # set other institutions by other party id
        institutions_list = []
        for _party_id in site_job.other_party_id:
            if str(_party_id) in other_institutions.keys():
                institutions_list.append(other_institutions[str(_party_id)])
        site_job.other_institutions = list(set(institutions_list))
        site_job.save(force_insert=True)

    @staticmethod
    def get_job_type(dsl):
        job_type = ''
        if isinstance(dsl, str):
            dsl = json.loads(dsl)
        cpn = dsl['components'].keys()
        cpn = list(cpn)[0]
        if 'upload' in cpn:
            job_type = 'upload'
        elif 'download' in cpn:
            job_type = 'download'
        elif 'intersect' in cpn:
            for j in dsl['components'].keys():
                if 'intersect' not in j:
                    job_type = 'modeling'
                    break
            else:
                job_type = 'intersect'
        else:
            job_type = 'modeling'
        return job_type