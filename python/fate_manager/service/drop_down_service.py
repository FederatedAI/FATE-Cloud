from db.db_models import FederatedInfo, FateSiteInfo, DeploySite, FateVersion, ComponentVersion, ApplySiteInfo
from entity import enum
from entity.types import SiteStatusType, ProductType, IsValidType
from operation.db_operator import DBOperator


def get_federation_drop_down_list():
    federated_info_list = DBOperator.query_entity(FederatedInfo, status=1)
    data = []
    for federated_info in federated_info_list:
        site_info_list = DBOperator.query_entity(FateSiteInfo, **{"federated_id": federated_info.federated_id,
                                                                  "status": SiteStatusType.JOINED})
        if site_info_list:
            data.append({"federatedOrganization": federated_info.federated_organization,
                         "federatedId": federated_info.federated_id})
    return data


def get_site_drop_down_list(federated_id):
    site_info_list = DBOperator.query_entity(FateSiteInfo, **{"federated_id": federated_id})
    data = []
    for site in site_info_list:
        if site.status == SiteStatusType.REMOVED:
            continue
        deploy_site_list = DBOperator.query_entity(DeploySite, **{"federated_id": federated_id,
                                                                  "party_id": site.party_id,
                                                                  "product_type": ProductType.FATE,
                                                                  "is_valid": IsValidType.YES
                                                                  })
        deploy_tag = False
        if deploy_site_list:
            if deploy_site_list[0].deploy_status == 1:  # success
                deploy_tag = True
        data.append({
            "partyId": site.party_id,
            "siteName": site.site_name,
            "deployStatus": deploy_tag
        })
    return data


def get_fate_version_drop_down_list(product_type):
    fate_version_list = DBOperator.query_entity(FateVersion, **{"product_type": product_type})
    return [fate_version.to_json() for fate_version in fate_version_list]


def get_enum_name_drop_down_list():
    return enum.EnumItem.to_list()


def get_enum_name_info(enum_name):
    return []


def get_component_version_list(name, by_fateversion=False):
    if not by_fateversion:
        component_version_info =  {"component_name": name}
    else:
        component_version_info= {
            "fate_version": name,
            "product_type": ProductType.FATE,
        }
    component_version_list = DBOperator.query_entity(ComponentVersion, **component_version_info)
    return [component_version.to_dict() for component_version in component_version_list]


def get_party_id():
    site_list = DBOperator.query_entity(ApplySiteInfo)
    party_id_dict = {}
    for site in site_list:
        party_id_dict[site.site_name] = site.party_id
    return party_id_dict