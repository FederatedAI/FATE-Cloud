import datetime
import json
import time

from fate_manager.db.db_models import DeployComponent, FateSiteInfo, FateSiteCount, FateSiteJobInfo, ApplySiteInfo
from fate_manager.entity import item
from fate_manager.entity.types import SiteStatusType, FateJobEndStatus
from fate_manager.operation.db_operator import DBOperator
from fate_manager.settings import FATE_FLOW_SETTINGS, request_flow_logger, request_cloud_logger
from fate_manager.utils.request_cloud_utils import request_cloud_manager
from fate_manager.utils.request_fate_flow_utils import post_fate_flow


class CountJob:
    @staticmethod
    def count_fate_flow_job(account):
        request_flow_logger.info("start count fate flow job")
        site_list = DBOperator.query_entity(FateSiteInfo, status=SiteStatusType.JOINED)
        component_name = 'FATEFLOW'
        party_id_flow_url = {}
        for site in site_list:
            deploy_fate_flow = DBOperator.query_entity(DeployComponent, party_id=site.party_id,
                                                       component_name=component_name)
            if deploy_fate_flow:
                query_job_url = "http://{}{}".format(deploy_fate_flow[0].address, FATE_FLOW_SETTINGS["QueryJob"])
                party_id_flow_url[site.party_id] = query_job_url
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
                CountJob.log_job_info(account, job_list, party_id=site.party_id, site_name=site.site_name)
                request_flow_logger.info(f"start create fate site count: now_time{now_time}")
                DBOperator.create_entity(FateSiteCount, {"strftime": now_time, "party_id_list": party_id_list})
        return party_id_flow_url

    @staticmethod
    def detector_no_end_job(account, party_id_flow_url):
        job_list = DBOperator.query_entity(FateSiteJobInfo, is_end=0)
        synchronization_job_list = []
        for job in job_list:
            update_status = FateJobEndStatus.FAILED
            if job.party_id in party_id_flow_url:
                job_list = post_fate_flow(party_id_flow_url[job.party_id], data={"job_id": job.job_id})
                if job_list:
                    if job_list[0]["f_status"] not in FateJobEndStatus.status_list():
                        update_status = None
            if update_status:
                DBOperator.update_entity(FateSiteJobInfo, {"job_id": job.job_id, "status":update_status, "is_end": 1})
                job.status = update_status
                job = CountJob.job_adapter(job)
                if job:
                    synchronization_job_list.append(job.to_json())
        CountJob.job_synchronization(account, synchronization_job_list)

    @staticmethod
    def detector_no_report_job(account):
        job_list = DBOperator.query_entity(FateSiteJobInfo, is_report=0)
        synchronization_job_list = []
        for job in job_list:
            job = CountJob.job_adapter(job)
            if job:
                synchronization_job_list.append(job.to_json())
        CountJob.job_synchronization(account, synchronization_job_list, is_report=1)


    @staticmethod
    def log_job_info(account, job_list, party_id, site_name):
        request_flow_logger.info(job_list)
        apply_site_list = DBOperator.query_entity(ApplySiteInfo)
        all_institutions = {}
        for site in apply_site_list:
            all_institutions[str(site.party_id)] = site.institutions
        synchronization_job_list = []
        for job in job_list:
            try:
                if not CountJob.check_roles(job.get("f_roles")):
                    continue
                site_job = CountJob.save_site_job_item(job, party_id, all_institutions, site_name, account)
                site_job = CountJob.job_adapter(site_job)
                if site_job:
                    synchronization_job_list.append(site_job.to_json())
            except Exception as e:
                request_flow_logger.exception(e)
        CountJob.job_synchronization(account, synchronization_job_list)

    @staticmethod
    def check_roles(roles):
        return True


    @staticmethod
    def save_site_job_item(job, party_id, all_institutions, site_name, account):
        site_job = FateSiteJobInfo()
        site_job.job_id = job.get("f_job_id")
        site_job.institutions = account.institutions
        site_job.party_id = party_id
        site_job.site_name = site_name
        site_job.job_create_time = int(time.mktime(time.strptime(job.get("f_job_id")[:20], "%Y%m%d%H%M%S%f"))*1000)
        site_job.job_elapsed = job.get("f_elapsed")
        site_job.job_start_time = job.get("f_start_time")
        site_job.job_end_time = job.get("f_end_time")
        site_job.roles = job.get("f_roles")
        site_job.job_type = CountJob.get_job_type(job.get("f_dsl"))
        site_job.status = FateJobEndStatus.end_status(job.get("f_status"))
        site_job.is_end = 1 if site_job.status in FateJobEndStatus.status_list() else 0
        site_job.job_create_day = job.get("f_job_id")[:8]
        site_job.job_create_day_date = datetime.datetime.strptime(site_job.job_create_day, "%Y%m%d")

        site_job.job_info = job
        site_job.need_report = 1
        other_party_id = set()
        site_job.role = job.get("f_role")
        institutions_party_id_list = []
        if site_job.role == "local":
            site_job.other_party_id = [party_id]
            institutions_party_id_list = [party_id]
        else:
            for role, party_id_list in job["f_roles"].items():
                for _party_id in party_id_list:
                    other_party_id.add(_party_id)
                    if str(_party_id) in all_institutions and all_institutions[str(_party_id)] == all_institutions[str(party_id)]:
                        institutions_party_id_list.append(_party_id)
                    if str(_party_id) not in all_institutions:
                        site_job.need_report = 0
            site_job.other_party_id = list(set(other_party_id))
            if len(site_job.other_party_id) > 1 and party_id in site_job.other_party_id:
                site_job.other_party_id.remove(site_job.party_id)
        # set other institutions by other party id
        site_job.institutions_party_id = list(set(institutions_party_id_list))
        institutions_list = []
        for _party_id in site_job.other_party_id:
            if str(_party_id) in all_institutions.keys():
                institutions_list.append(all_institutions[str(_party_id)])
        site_job.other_institutions = list(set(institutions_list))
        if len(site_job.other_institutions) > 1 and site_job.institutions in site_job.other_institutions:
            site_job.other_institutions.remove(site_job.institutions)
        site_job.save(force_insert=True)
        return site_job

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

    @staticmethod
    def job_adapter(site_job):
        # for cloud job
        if not site_job.need_report:
            return None
        site_job.job_info = None
        site_job.create_date = None
        site_job.update_date = None
        site_job.create_time = None
        site_job.job_create_day_date = datetime.datetime.strptime(site_job.job_create_day, "%Y%m%d")
        site_job.job_create_day_date = int(datetime.datetime.timestamp(site_job.job_create_day_date)) * 1000
        site_job.roles = json.dumps(site_job.roles, separators=(',', ':'))
        site_job.other_party_id = json.dumps(site_job.other_party_id, separators=(',', ':'))
        site_job.other_institutions = json.dumps(site_job.other_institutions, separators=(',', ':'))
        return site_job

    @staticmethod
    def job_synchronization(account, synchronization_job_list, is_report=0):
        piece = 0
        count_of_piece = 500
        try:
            while len(synchronization_job_list) > piece*count_of_piece:
                start = piece*count_of_piece
                end = piece*count_of_piece + count_of_piece
                institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                                           appKey=account.app_key,
                                                                           appSecret=account.app_secret).to_dict()
                resp = request_cloud_manager(uri_key="MonitorPushUri", data=institution_signature_item,
                                             body=synchronization_job_list[start:end],
                                             url=None)
                piece += 1
        except Exception as e:
            request_cloud_logger.exception(e)
        if piece*count_of_piece >= len(synchronization_job_list):
            if is_report:
                for job in synchronization_job_list[:piece*count_of_piece]:
                    DBOperator.update_entity(FateSiteJobInfo, {"job_id": job.get("job_id"), "is_report": is_report})
            else:
                for job in synchronization_job_list[piece*count_of_piece:]:
                    DBOperator.update_entity(FateSiteJobInfo, {"job_id": job.get("job_id"), "is_report":is_report})