pnum=$( ps aux|egrep -v "grep|fate/tools/check.sh|serving|ansilbe|tail|" | grep -c fate  );
if [ $pnum -gt 0 ]
then
  echo "FATE PROCESS CHECK: warning: key fate process exists, please has a check and clean"
else
  echo "FATE PROCESS CHECK: success"
fi