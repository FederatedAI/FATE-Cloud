from fate_manager.entity import item
from fate_manager.settings import stat_logger
from fate_manager.utils.request_cloud_utils import request_cloud_manager

DEFAULT_FATE_SERVING_VERSION = "1.3.0"


def update_version_to_cloud_manager(site):
    site_version_item = item.SiteVersionItem()
    site_version_item.fateVersion = site.fate_version
    site_version_item.fateServingVersion = get_fate_serving_version()
    site_version_item.componentVersion = site.component_version
    site_signature_req = item.SiteSignatureItem(**site.to_json()).to_dict()
    stat_logger.info(f"start request cloud FederationUri:{site_signature_req}\n{site_version_item.to_dict()}")
    resp = request_cloud_manager(uri_key="FederationUri", data=site_signature_req, body=site_version_item.to_dict(),
                                 url=site.federated_url)
    stat_logger.info(f"request cloud success")


def get_fate_serving_version():
    return DEFAULT_FATE_SERVING_VERSION

