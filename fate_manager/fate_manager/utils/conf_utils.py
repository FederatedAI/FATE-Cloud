import os

from ruamel import yaml

SERVICE_CONF = "service_conf.yaml"

PROJECT_BASE = None


def get_project_base_directory():
    global PROJECT_BASE
    if PROJECT_BASE is None:
        PROJECT_BASE = os.path.abspath(
            os.path.join(os.path.dirname(os.path.realpath(__file__)), os.pardir, os.pardir))
    return PROJECT_BASE


def conf_realpath(conf_name):
    conf_path = f"fate_manager/conf/{conf_name}"
    return os.path.join(get_project_base_directory(), conf_path)


def get_base_config(key, default=None, conf_name=SERVICE_CONF):
    base_config = load_yaml_conf(conf_path=conf_realpath(conf_name=conf_name)) or dict()
    return base_config.get(key, default)


def update_config(key, value, conf_name=SERVICE_CONF):
    config = load_yaml_conf(conf_path=conf_realpath(conf_name=conf_name)) or dict()
    config[key] = value
    rewrite_yaml_conf(conf_path=conf_realpath(conf_name=conf_name), config=config)


def load_yaml_conf(conf_path):
    if not os.path.isabs(conf_path):
        conf_path = os.path.join(get_project_base_directory(), conf_path)
    try:
        with open(conf_path) as f:
            return yaml.safe_load(f)
    except Exception as e:
        raise EnvironmentError("loading yaml file config from {} failed:".format(conf_path), e)


def rewrite_yaml_conf(conf_path, config):
    if not os.path.isabs(conf_path):
        conf_path = os.path.join(get_project_base_directory(), conf_path)
    try:
        with open(conf_path, "w") as f:
            yaml.dump(config, f, Dumper=yaml.RoundTripDumper)
    except Exception as e:
        raise EnvironmentError("rewrite yaml file config {} failed:".format(conf_path), e)