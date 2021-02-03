openfiles=`ulimit  -a |egrep 'open files'| awk '{print $4}'`
maxuser=`ulimit -a |egrep 'max user processes'| awk '{print $5}'`
if [ $openfiles -lt 64000 ];then
  echo "OPEN FILES CHECK: warning: now open files is $openfiles, and need to turn up to 64000"
else
  echo "OPEN FILES CHECK: success"
fi