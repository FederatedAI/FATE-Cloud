class BaseItem(object):
    def to_dict(self, need_none=False):
        d = {}
        for k, v in self.__dict__.items():
            if v is None and not need_none:
                continue
            d[k] = v
        return d


class IdPair(BaseItem):
    def __init__(self, **kwargs):
        self.code = None
        self.desc = None
        for k, v in kwargs.items():
            if hasattr(self, k):
                setattr(self, k, v)


class AuditPair(BaseItem):
    def __init__(self, **kwargs):
        self.code = None
        self.desc = None
        self.readCode = None
        for k, v in kwargs.items():
            if hasattr(self, k):
                setattr(self, k, v)


class Role(BaseItem):
    def __init__(self, **kwargs):
        self.roleId = None
        self.roleName = None
        for k, v in kwargs.items():
            if hasattr(self, k):
                setattr(self, k, v)


class SitePair(BaseItem):
    def __init__(self, **kwargs):
        self.partyId = None
        self.siteName = None
        self.institution = None
        for k, v in kwargs.items():
            if hasattr(self, k):
                setattr(self, k, v)


class FederatedItem(BaseItem):
    def __init__(self, **kwargs):
        self.federatedId = None
        self.federatedOrganization = None
        self.institutions = None
        self.fateManagerInstitutions = None
        self.size = None
        self.siteList = []
        self.createTime = None
        for k, v in kwargs.items():
            if hasattr(self, k):
                setattr(self, k, v)


class SiteItem(BaseItem):
    def __init__(self, **kwargs):
        self.siteId = None
        self.partyId = None
        self.siteName = None
        self.role = None
        self.status = None
        self.serviceStatus = None
        self.activationTime = None
        for k, v in kwargs.items():
            if hasattr(self, k):
                setattr(self, k, v)


class SiteVersionItem(BaseItem):
    def __init__(self, **kwargs):
        self.componentVersion = None
        self.fateServingVersion = None
        self.fateVersion = None
        for k, v in kwargs.items():
            if hasattr(self, k):
                setattr(self, k, v)


class SiteSignatureItem(BaseItem):
    def __init__(self, **kwargs):
        self.partyId = None
        self.role = None
        self.appKey = None
        self.appSecret = None
        for k, v in kwargs.items():
            if hasattr(self, k):
                setattr(self, k, v)


class InstitutionSignatureItem(BaseItem):
    def __init__(self, **kwargs):
        self.fateManagerId = None
        self.appKey = None
        self.appSecret = None
        for k, v in kwargs.items():
            if hasattr(self, k):
                setattr(self, k, v)


class OldSignatureItem(BaseItem):
    def __init__(self, **kwargs):
        self.partyId = None
        self.role = None
        self.appKey = None
        self.appSecret = None
        for k, v in kwargs.items():
            if hasattr(self, k):
                setattr(self, k, v)
