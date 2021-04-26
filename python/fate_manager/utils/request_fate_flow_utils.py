import requests

from entity.status_code import RequestFateFlowCode


def post_fate_flow(url, data):
    response = requests.post(url=url, json=data)
    if response.status_code == 200:
        if not response.json().get('retcode'):
            return response.json().get('data')
        else:
            raise Exception(RequestFateFlowCode.HttpRequestFailed,
                            f'request url {url} failed:code {response.json().get("retcode")}, msg {response.json().get("retmsg")}')
    else:
        raise Exception(RequestFateFlowCode.HttpRequestFailed,
                        f'request url {url} failed,http status code:{response.status_code}')

