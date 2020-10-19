#!/bin/bash

if [ $# -ne 2 ] ; then
	echo "Error:Param Need 2 !"
	exit
fi
testItem=$1
guestPartyId=$2

dir=/data/projects/fate-cloud/fate-manager

sudoTag=`grep SudoTag $dir/conf/app.ini |sed s/[[:space:]]//g| awk -F '=' '{print $2}'`
testPartyId=`grep TestPartyId $dir/conf/app.ini |sed s/[[:space:]]//g| awk -F '=' '{print $2}'`


#判断flow进程是否存在
echo "====================Start To AutoTest:====================="
flow=`sudo kubectl get pods -n fate-${guestPartyId} |grep python*|grep Running|awk '{print $1}'`
hostflow=`sudo kubectl get pods -n fate-${testPartyId} |grep python*|grep Running|awk '{print $1}'`
echo $flow
if [ -z $flow ] ; then
    echo "Error:flow process not exists" 
    exit
fi

kubectl_bin=" kubectl exec $flow -c python -n fate-${guestPartyId} -- bash -c "
host_kubectl_bin=" kubectl exec $hostflow -c python -n fate-${testPartyId} -- bash -c "
sourcedir="source /data/projects/python/venv/bin/activate && "
if [ $sudoTag == "true" ] ; then
    kubectl_bin="sudo "$kubectl_bin
    host_kubectl_bin="sudo "$host_kubectl_bin
fi

startTime=`date +"%Y-%m-%d %H:%M:%S"`


valueIte=""
if [ $testItem == "Toy" ] ; then
	valueItem="Toy Test"
elif [ $testItem == "Fast" ] ; then
	valueItem="Mininmize Fast Test"
elif [ $testItem == "Normal" ]; then
	valueItem= "Minimize Normal Test"
else
	valueItem="Single Test"
fi

	#statements
echo "************Start To Run $testItem:$startTime,************"
if [ $testItem == "Single" ] || [ $testItem == "Toy" ] ; then

    #开始单边,双边测试
    if [ $testItem == "Single" ] ; then
        $kubectl_bin "$sourcedir cd examples/toy_example && python run_toy_example.py $guestPartyId $guestPartyId 1"
    else
        $kubectl_bin "$sourcedir cd examples/toy_example && python run_toy_example.py $guestPartyId $testPartyId 1"
    fi
elif [ $testItem != "" ] ; then

    #开始单边,双边测试
    if [ $testItem == "Fast" ] ; then
        $kubectl_bin "$sourcedir cd /data/projects/fate/python/examples/scripts/ && python upload_default_data.py -m 1"
        #$host_kubectl_bin "$sourcedir cd /data/projects/fate/python/examples/scripts/ && python upload_default_data.py -m 1"

        $kubectl_bin "$sourcedir cd examples/min_test_task && python run_task.py -m 1 -gid $guestPartyId -hid $testPartyId -aid $testPartyId -f fast"
    else
        $kubectl_bin "$sourcedir cd examples/min_test_task && python run_task.py -m 1 -gid $guestPartyId -hid $testPartyId -aid $testPartyId -f normal"
    fi	
fi
endTime=`date +"%Y-%m-%d %H:%M:%S"`
echo "************End To Run $testItem:$endTime,************"
echo "====================End To AutoTest====================="
