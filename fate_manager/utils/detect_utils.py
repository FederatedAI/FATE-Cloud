import typing

from fate_manager.entity.status_code import ParameterStatusCode
from fate_manager.settings import stat_logger


def check_config(config: typing.Dict, required_arguments: typing.List):
    no_arguments = []
    error_arguments = []
    for require_argument in required_arguments:
        if isinstance(require_argument, tuple):
            config_value = config.get(require_argument[0], None)
            if isinstance(require_argument[1], (tuple, list)):
                if config_value not in require_argument[1]:
                    error_arguments.append(require_argument)
            elif config_value != require_argument[1]:
                error_arguments.append(require_argument)
        elif require_argument not in config:
            no_arguments.append(require_argument)
    if no_arguments or error_arguments:
        error_string = ""
        if no_arguments:
            error_string += "required parameters are missing: {};".format(",".join(no_arguments))
        if error_arguments:
            error_string += "required parameter values: {}".format(",".join(["{}={}".format(a[0], a[1]) for a in error_arguments]))
        stat_logger.exception(error_string)
        raise Exception(ParameterStatusCode.MissingRequestParameters, error_string)
