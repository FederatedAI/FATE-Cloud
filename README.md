# Overview #
FATE Cloud is an Infrastructure for Building and Managing Federated Data Collaboration Network.
 
FATE Cloud enables FATE to be managed in multi-cloud, forming a secure federated data network, designed to provide secure and compliant data cooperation solutions across or within organizations, and build an industrial-grade federated learning cloud service.

<div style="text-align:center", align=center>
<img src="./images/FATECloud.png" />
</div>
 
FATE Cloud provides standard federated infrastructure implementation capabilities, technical support capabilities, a unified technical framework for building federated data networks, and addresses distributed data processing and data authentication issues. FATE-Cloud includes two types of roles: a neutral Federated Cloud and local Federated Sites.

**Federated Cloud（Cloud Manager）**

The Federated Cloud is the management center of the federated network, building the entire federated network and performs site-wide operation and management, and providing site registration, site authentication, site cooperation management, etc.

**Federated Site（FATE Manager）**

A Federated Site is an enterprise, institution, or organization that participates in data cooperation with other members of the federated network. The Federated Site includes FATE and FATE manager. FATE serves as the infrastructure of federated learning and provides federated modeling capabilities for the sites; FATE Manager provides the site with services such as joining federated organizations, site configuration, site management and monitoring.

# Deploy #
Currently，Cloud Manager can be deployed as a separate service and does not depend on FATE. It is only required that the machine where it is located supports the jdk and MySQL environments.

If you start deploying FATE Manager, it is important to ensure that the FATE system has been deployed. The FATE Manager service is integrated into the FATE Board service of the FATE cluster as a plugin. If you have already deployed the FATE Board, you need to  update the FATE Board to the new version with FATE Manager. For more detailed deployment information, please refer to *[FATE Cloud Deployment](https://github.com/FederatedAI/FATE-Cloud/blob/master/cluster-deploy/scripts/deploy/deploy.sh)*.
 
# Usage #
## Adding a new site  ##
1. Cloud Manager adds a new site for the organization applying to join the federated network, and assigns site identity information(PartyID, role) and key information (SecretKey).
2. FATE Manager starts the service and configures the local site network, and submit the application.
3. Cloud Manager verified the information submitted by the site. After the verification passes, the site successfully joins the federated organization.
4. After that, federated modeling can be started between sites, you can launch FATE Board to view the model training process.


FATE Cloud can be used in Google Chrome, Firefox and Microsoft Edge, but IE browser is not currently supported. Some browsers might work, but there may be bugs or performance issues.

# FAQ #

**My FATE Manager can not register, and it shows "Authorization Failure!" .**

- There are some key information to fill in FATE Manager interface, such as Party ID and SecretKey. You should get them from Cloud Manager and make sure they are correct.
- The Party ID assigned to each site from Cloud Manager must be the same as the party ID assigned to the site when FATE was deployed. If they are not match, you can update the Party ID in MySQL connected to the Cloud Manager.

**FATEBoard shows ''System Error!" in the web pages.**

- FATEBoard which integrated FATE Manager is still one part of FATE. So it need the related configurations to properly start and run. Please input its file: application.properties.
- FATEBoard gets data from MySQL and FATE-Flow, so keep them work well.

**Cloud Manager failed to start.**

- Cloud Manager is a separate service. It depends upon the MySQL and Java environment. If you have a suit of FATE system, you could choose one machine to deploy the Cloud Manager to your current environment. And, you can also deploy it in a new machine using MySQL and java.
- Provide sufficient memory space for the FATEBoard service.

**FATEBoard failed to start.**

- FATEBoard services exists in each FATE of site. When you update FATEBoard to the new version with FATE Manager, you should kill the old one or you can change the port to avoid conflict.
- Check the script (service.sh) to start the service to make sure you have the right Java path.
- Provide sufficient memory space for the FATEBoard service.
