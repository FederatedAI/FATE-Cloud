
import time
import json
import random
import pymysql


#status = "waiting" #running / waiting / failed / success
ji = "20210604090635152"+str(random.randint(111,999))
je = random.randint(11111,99999)
ud = time.strftime("%Y-%m-%d %H:%M:%S",time.localtime())
# ud = "2021-06-06 17:50:24"

def insert(roles,ip,ins1,party_id1,site_name1,role1,other_institutions,other_party_id):
    db = pymysql.connect(host=ip, user="root", passwd="fate_dev", port=3308, db="fate_manager")
    #db = pymysql.connect(host="172.16.153.186", user="fate_manager", passwd="fate_manager", port=3306, db="CM130_58_test")
    cursor = db.cursor()
    N = "(NULL)"
    update_date = ud
    print(update_date)
    update_t = time.strptime(update_date,"%Y-%m-%d %H:%M:%S")
    update_time = int(time.mktime(update_t)*1000)
    print(update_time)
    institutions = ins1
    party_id = party_id1
    site_name = site_name1
    role = role1
    job_id = ji
    job_elapsed = je
    roles = json.dumps(roles)
    other_party_id = json.dumps(other_party_id)
    other_institutions = json.dumps(other_institutions)
    job_type = "modeling"
    job_create_day = update_date[:10].replace("-","")#"20210607"
    job_create_day_date = update_date[:10] #
    job_create_time = update_time
    job_start_time = update_time
    job_end_time = update_time
    status = "running" #"success" #running / waiting / failed
    job_info = '""'
    institutions_party_id = '{"0": ' + str(party_id1) + '}'
    print(institutions_party_id)

    key = "(`job_id`, `update_time`, `update_date`, `institutions`, `party_id`, `site_name`, `role`, `job_elapsed`, `roles`, `other_party_id`, `institutions_party_id`, `other_institutions`, `job_type`, `job_create_day`, `job_create_day_date`, `job_create_time`, `job_start_time`, `job_end_time`, `status`, `job_info`, `is_end`, `is_report`, `need_report`)"
    value = str((job_id,update_time,update_date,institutions,party_id,site_name,role,job_elapsed,roles,other_party_id,institutions_party_id,other_institutions,job_type,job_create_day,job_create_day_date,job_create_time,job_start_time,job_end_time,status,job_info,1,0,1))
    SQL = "INSERT INTO t_fate_site_job" + key + "value" + value
    cursor.execute(SQL)
    db.commit()
    db.close()

def insert2(roles,ip,ins1,party_id1,party_id2,site_name1,role1,other_institutions,other_party_id):
    db = pymysql.connect(host=ip, user="root", passwd="fate_dev", port=3308, db="fate_manager")
    # db = pymysql.connect(host="10.107.117.102", user="root", passwd="fate_dev", port=3306, db="cloud_manager130_116_test")
    cursor = db.cursor()
    N = "(NULL)"
    update_date = ud
    print(update_date)
    update_t = time.strptime(update_date,"%Y-%m-%d %H:%M:%S")
    update_time = int(time.mktime(update_t)*1000)
    print(update_time)
    institutions = ins1
    party_id = party_id1
    site_name = site_name1
    role = role1
    job_id = ji
    job_elapsed = je
    roles = json.dumps(roles)
    other_party_id = json.dumps(other_party_id)
    other_institutions = json.dumps(other_institutions)
    job_type = "modeling"
    job_create_day = update_date[:10].replace("-","")#"20210607"
    job_create_day_date = update_date[:10] #
    job_create_time = update_time
    job_start_time = update_time
    job_end_time = update_time
    status = "running" #running / waiting / failed / success
    job_info = '""'
    institutions_party_id = json.dumps({"0":party_id2,"1": party_id1})
    print(institutions_party_id)

    key = "(`job_id`, `update_time`, `update_date`, `institutions`, `party_id`, `site_name`, `role`, `job_elapsed`, `roles`, `other_party_id`, `institutions_party_id`, `other_institutions`, `job_type`, `job_create_day`, `job_create_day_date`, `job_create_time`, `job_start_time`, `job_end_time`, `status`, `job_info`, `is_end`, `is_report`, `need_report`)"
    value = str((job_id,update_time,update_date,institutions,party_id,site_name,role,job_elapsed,roles,other_party_id,institutions_party_id,other_institutions,job_type,job_create_day,job_create_day_date,job_create_time,job_start_time,job_end_time,status,job_info,1,0,1))
    SQL = "INSERT INTO t_fate_site_job" + key + "value" + value
    cursor.execute(SQL)
    db.commit()
    db.close()

def insert_db_job(**kwd):
    roles = kwd["kwd"]
    print(roles)
    if roles["guest"][0] == roles["host"][0]:
        ro = {"guest":roles["guest"][-1],"host":roles["host"][-1]}
        other_party_id = {"0":roles["host"][0]}
        other_institutions = {"0":roles["host"][2]}
        insert(roles=ro, ip=roles["guest"][1], ins1=roles["guest"][2], party_id1=roles["guest"][0], site_name1=roles["guest"][3], role1="guest", other_institutions=other_institutions,other_party_id=other_party_id)

    elif roles["guest"][0] != roles["host"][0]:
        if roles["guest"][2] != roles["host"][2]:
            ro = {"guest": roles["guest"][-1],"host":roles["host"][-1]}
            other_party_id1 = {"0":roles["host"][0]}
            other_institutions1 = {"0":roles["host"][2]}

            insert(roles=ro, ip=roles["guest"][1], ins1=roles["guest"][2], party_id1=roles["guest"][0],
                   site_name1=roles["guest"][3], role1="guest",
                   other_institutions=other_institutions1, other_party_id=other_party_id1)
            time.sleep(0.5)
            other_party_id2 = {"0": roles["guest"][0]}
            other_institutions2 = {"0": roles["guest"][2]}

            insert(roles=ro, ip=roles["host"][1], ins1=roles["host"][2], party_id1=roles["host"][0],
                   site_name1=roles["host"][3], role1="host",
                   other_institutions=other_institutions2, other_party_id=other_party_id2)
        else:
            ro = {"guest": roles["guest"][-1], "host": roles["host"][-1]}
            other_party_id1 = {"0": roles["host"][0]}
            other_institutions1 = {"0": roles["host"][2]}

            insert2(roles=ro, ip=roles["guest"][1], ins1=roles["guest"][2], party_id1=roles["guest"][0],
                   party_id2=roles["host"][0] ,site_name1=roles["guest"][3], role1="guest",
                   other_institutions=other_institutions1, other_party_id=other_party_id1)


if __name__ == "__main__":

    roles = dict({
        "guest": [10000, "172.16.153.186", "fate机构-186", "hello-site站点为10000", [10000]],  # guest作为发启方
        "host": [9997, "172.16.153.186", "fate机构-186", "ada1!@#!--site9997", [9997]]
        # "guest": [9999, "172.16.153.235", "FATE-235", "site-9999", [9999]]
    })
    insert_db_job(kwd=roles)


