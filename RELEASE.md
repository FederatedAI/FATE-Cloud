# **Release 1.2.0** #

# **Major Features** #

## Cloud Manager ##

**Site manage**
- Supports for specifying the proxy network access of  site before generating registration link.
----------------------------------------------------------------------------------------------------
**IP manage**
- Supports for setting the true/false value for "is polling" field of each router .
----------------------------------------------------------------------------------------------------
**Certificate**
- Add certificate function,supports for Cloud manager certificates publishment and revocation.
----------------------------------------------------------------------------------------------------
**System function switch**
- Supports for setting the federated senario for Cloud manager.After setting the senario,FATE manager can only apply to view the specified sites of other FATE managers as follows:
  - hetero federation only：appliers can  only view the host sites of other FATE managers.
  - homo federation only：appliers can  only view the guest sites of other FATE managers.
  - hetero federation and homo federation：appliers can view both the guest sites and host sites of other FATE managers.
----------------------------------------------------------------------------------------------------
**Admin access**
- Supports for specifying the proxy network access of FATE manager before generating invitation link.
----------------------------------------------------------------------------------------------------

# **Improvements** #
**Site monitor**
- Improvements for the arrangement of institutions and sites in each module.

**Others**
- Supports for switching  to the Chinese language.

# **Release 1.1.0** #

# **Major Features and Improvements** #

## Cloud Manager ##

**Site manage**
- Supports for viewing service status of sites .
- Add more site details, supports for viewing  IP information of each deployed component.
- Supports for generation options of regitration link, include generating as a HTTPS or HTTP link, whether the link is encrypted.

**IP manage**
- Add exchange router management.  

**Service manage**
- Supports for viewing service status of each deployed component.

**Site monitor**
- Add site monitor function,supports for viewing active data of institutions. Include stastics of  institutions themselves and statistics of cooperation between institutions.

**Admin access**
- Supports for generation options of invitation link, include generating as a HTTPS or HTTP link.

**Authentication**
- Supports authentication for exchange(rollsite) polling mode.


## FATE Manager ##

**Site Manage**
- Supports for viewing service status of sites.
- Add more site details, supports for viewing IP information of each deployed component.
- Add exchange info function, supports for viewing VIP of exchanges relevant with my institution.


**Auto-deploy**
- Add automatic deployment function based on Ansible.
- Supports for viewing service status of each deployed component.

**Site cooperation**
- Add site cooperation function, supports for viewing active data of sites. Include stastics of sites themselves and statistics of cooperation between sites.


# **Release 1.0** #

# **Major Features and Improvements** #

## Cloud Manager ##

**Site manage**

- Add network access entrances/exits management of sites, supports the approval of IP modification, and maintain historical approval records of IP.
- Add service manage of sites，supports management and maintenance of site service version.
- Add more site details.
- Add site information authorization to support authorization and viewing of sites under federated organization.

**Account login**

- Add account login for users.

**Administrator access**

- Add administrator setting of Cloud Manager to support the addition and maintenance of admin.
- Add admin invitation of FATE Manager.

**System function switch**

- Add the switch control of system function module.


## FATE Manager ##

**Site manage**

- Supports for the modification of network access entrances/exits.
- Add more site details, include user list of site and service version records of site.
- Add service version management of site.
- Add the application function of site information authorization, supports for the application to view other FATE Manager’s sites under the federated organization.

**Auto-deploy**

- Add automatic deployment function based on KubeFATE
- Supports for automatic upgrade of service version.

**Account login**

- Add account login for users

**User access**

- Add activation and binding of FATE Manager administrator.
- Addiction and maintenance of FATE Manager administrator.
- Addiction and maintenance of site users.

## Improvements ##

- Site registration process optimization.
- Product UI interface revision.


# **Release alpha** #

**Major Features**
 
**Cloud Manager: the management center of the Federated Data Collaboration Network**

- Site registration: add site, assign an Party ID, allocate role and distribute a Secret key to the new site.
- Site authentication: verify identity for site and update the registration information to the federated network.
- Site management: support site information query, site deletion, etc.

**FATE Manager: local site management platform**

- Site configuration：network configuration and submit an application to join the federated network.
- Support site registration information query and modification

