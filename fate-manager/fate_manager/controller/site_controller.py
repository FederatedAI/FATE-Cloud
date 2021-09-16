import socket


def site_page(site_info, pageSize, pageNum):
    # if not site_info:
    #     return {}
    if site_info:
        total = len(site_info[0]['siteList'])
        take_int = total // pageSize
        remainder = total % pageSize
        if pageNum <= take_int:
            start_num = (pageNum - 1) * pageSize
            info = site_info[0]['siteList']
            data_split = info[start_num:(start_num + pageSize)]

        else:
            info = site_info[0]['siteList']
            data_split = info[-remainder:]
        site_info[0]['siteList'] = data_split
        return {'data': site_info, 'total': total}


def connect_test(ip, port, timeout=3):
    try:
        client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client.settimeout(timeout)
        client.connect((ip, port))
        return True
    except Exception as e:
        return False
