from fate_manager.operation.db_operator import DBOperator
from fate_manager.db.db_models import *

table_list = [AccountInfo, AccountSiteInfo,ApplyInstitutionsInfo,ApplySiteInfo,AutoTest,ChangeLog,ComponentVersion,
              DeployComponent,DeployJob,DeployPrepare,DeploySite,FateVersion, FederatedInfo,KubenetesConf,
              FateSiteCount,FateSiteJobInfo,FateReportInstitution,FateReportSite,FateSiteInfo,TokenInfo]


def clear_table_data():
    for tab_name in table_list:
        DBOperator.delete_entity_data(tab_name)